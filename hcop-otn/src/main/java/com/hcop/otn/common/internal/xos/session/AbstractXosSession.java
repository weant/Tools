package com.hcop.otn.common.internal.xos.session;

import com.alu.tools.basic.ArgumentUtil;
import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.exception.BasicRuntimeException;
import com.alu.tools.basic.io.IoUtil;
import com.hcop.otn.common.internal.xos.util.CastorUtil;
import com.lucent.oms.xml.naInterface.Message_T;
import com.lucent.oms.xml.naInterface.types.OpRole_T;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class AbstractXosSession implements IXosSession
{
	private int groupID;

	private String host;

	private int port;

	private Socket socket;

	private Scanner sockReader;

	private PrintWriter sockWriter;

	private volatile boolean run;

	private long reconnectInterval;

	private Thread sessionThread;

	private static final Pattern XOS_MSG_PATTERN = Pattern.compile(
			"<namessage>.+?</namessage>", Pattern.DOTALL);

	private static Log log = LogFactory.getLog(AbstractXosSession.class);

	public AbstractXosSession(int groupID, String host, int port, long reconnectInterval)
	{
		this.groupID = groupID;
		this.host = NullUtil.notNull(host);
		this.port = port;
		this.reconnectInterval = ArgumentUtil.checkMin(reconnectInterval, 1);
	}

	@Override
	public String toString()
	{
		return "XOS-" + groupID + '@' + host + ':' + port;
	}

	public int getGroupID()
	{
		return groupID;
	}

	public void startSession()
	{
		run = true;
		sessionThread = new Thread(new Runnable()
		{
			public void run()
			{
				execute();
			}
		}, this.toString());
		sessionThread.start();
		log.info("XosSession " + this + " started");
	}

	public void endSession()
	{
		run = false;
		sessionThread.interrupt();
		closeConn();
		log.info("XosSession " + this + " shutdown");
	}

	public boolean available()
	{
		return run;
	}

	protected void sendRequest(String request)
	{
		sockWriter.println(request);
		sockWriter.flush();
		log.debug("Send request: '" + request + "'");
	}

	private boolean connect()
	{
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));
			sockReader = new Scanner(socket.getInputStream());
			sockWriter = new PrintWriter(socket.getOutputStream());
			log.debug("XosSession " + this + " connect to SNA");
			return true;
		}
		catch (IOException e)
		{
			log.debug("XosSession " + this + " failed to connect to SNA", e);
			closeConn();
			return false;
		}
	}

	private void closeConn()
	{
		IoUtil.close(sockWriter);
		IoUtil.close(sockReader);
		IoUtil.close(socket);
	}

	private boolean recvMessage()
	{
		String message = sockReader.findWithinHorizon(XOS_MSG_PATTERN, 0);
		if (message == null) return false;
		//log.error("++++++Receive msg: '" + message + "'");
		log.trace("Receive msg: '" + message + "'");
		try
		{
			msgReceived(CastorUtil.unmarshal(message, Message_T.class));
		}
		catch (BasicRuntimeException e)
		{
			log.error("Failed to process response message", e);
		}
		return true;
	}

	private void execute()
	{
		while (run)
		{
			try
			{
				while (run)
				{
					if (connect())
					{
						break;
					}
					Thread.sleep(reconnectInterval);
				}
				if (run)
				{
					connectionEstablished();
				}
				while (run)
				{
					if (!recvMessage()) break;
				}
				if (run)
				{
					log.debug("XosSession lost connection: " + this);
					connectionLost();
				}
			}
			catch (Exception e)
			{
				if (run) log.error("XosSession error", e);
			}
			finally
			{
				closeConn();
			}
		}
	}

	private void msgReceived(Message_T message)
	{
		switch (message.getHdr().getOpRole().getType())
		{
			case OpRole_T.RESP_TYPE:
				responseReceived(message);
				break;
			default:
		}
	}

	protected abstract void responseReceived(Message_T response);

	protected abstract void connectionEstablished();

	protected abstract void connectionLost();
}
