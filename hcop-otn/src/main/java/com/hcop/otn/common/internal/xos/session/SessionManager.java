package com.hcop.otn.common.internal.xos.session;

import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.io.FileUtil;
import com.alu.tools.basic.io.handler.ILineHandler;
import com.hcop.otn.common.internal.xos.util.XosUtil;
import com.hcop.otn.common.properties.Conf;
import com.hcop.otn.common.utils.ConfigKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.hcop.otn.constants.ConfigKey.CONFIGURATION_PATH;

public class SessionManager
{
	private Map<Integer, SnaAddress> groupIDToSnaAddr = new HashMap<>();

	private Map<Integer, IXosSession> groupIDToSession = new HashMap<>();

	private static SessionManager instance = new SessionManager();

	private static Log log = LogFactory.getLog(SessionManager.class);

	private static Pattern blank = Pattern.compile("\\s+");

	private SessionManager()
	{
	}

	public static SessionManager getInstance()
	{
		return instance;
	}

	public void init()
	{
		File currentDir = new File(System.getProperty(CONFIGURATION_PATH) == null ? System.getenv(CONFIGURATION_PATH) : System.getProperty(CONFIGURATION_PATH));
		FileUtil.read(new File(currentDir.getAbsolutePath() + File.separator + Conf.instance().getProperty(ConfigKey.SNA_CONF_FILE)),
				new ILineHandler() {
					@Override
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