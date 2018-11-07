package com.otn.tool.common.internal.xos.session;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.otn.tool.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.otn.tool.common.utils.ConfigKey;
import com.otn.tool.common.internal.xos.util.XosUtil;
import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.io.FileUtil;
import com.alu.tools.basic.io.handler.ILineHandler;

public class SessionManager
{
	private Map<Integer, SnaAddress> groupIDToSnaAddr = new HashMap<Integer, SnaAddress>();

	private Map<Integer, IXosSession> groupIDToSession = new HashMap<Integer, IXosSession>();

	private static SessionManager instance = new SessionManager();

	private static Log log = LogFactory.getLog(SessionManager.class);

	private SessionManager()
	{
	}

	public static SessionManager getInstance()
	{
		return instance;
	}

	public void init()
	{
		FileUtil.read(new File(Conf.instance().getProperty(ConfigKey.SNA_CONF_FILE)),
				new ILineHandler()
				{
					private Pattern blank = Pattern.compile("\\s+");

					public boolean handle(String line)
					{
						if (line.startsWith("//")) return true;
						String[] args = blank.split(line);
						if (args.length == 3)
						{
							groupIDToSnaAddr.put(new Integer(args[0]), new SnaAddress(args[1],
									Integer.parseInt(args[2])));
						}
						return true;
					}
				});
		XosUtil.init();
	}

	public void destroy()
	{
		for (IXosSession session : groupIDToSession.values())
		{
			session.endSession();
		}
		groupIDToSnaAddr.clear();
		groupIDToSession.clear();
	}

	public Set<Integer> getAllGroups()
	{
		return Collections.unmodifiableSet(groupIDToSnaAddr.keySet());
	}

	public IXosSession getSession(int groupID) throws SessionException
	{
		IXosSession session = groupIDToSession.get(groupID);
		if (session == null)
		{
			synchronized (this)
			{
				session = groupIDToSession.get(groupID);
				if (session == null)
				{
					session = createSession(groupID);
					groupIDToSession.put(groupID, session);
				}
			}
		}
		return session;
	}

	private IXosSession createSession(int groupID) throws SessionException
	{
		SnaAddress address = groupIDToSnaAddr.get(groupID);
		if (address == null) throw new SessionException("Unsupported eml group: " + groupID);
		IXosSession session = createSession(groupID, address);
		session.startSession();
		log.debug("Create XosSession: '" + session + "'");
		return session;
	}

	private IXosSession createSession(int groupID, SnaAddress address)
	{
		return new XosSession(groupID, address.host, address.port, XosUtil.reconnectInterval(),
				XosUtil.requestQueueSize(), XosUtil.parallelReqNum(), XosUtil.requestTimeout());
	}

	private static class SnaAddress
	{
		private final String host;

		private final int port;

		public SnaAddress(String host, int port)
		{
			this.host = NullUtil.notNull(host);
			this.port = port;
		}
	}
}