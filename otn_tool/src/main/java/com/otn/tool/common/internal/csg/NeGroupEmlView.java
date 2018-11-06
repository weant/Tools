package com.otn.tool.common.internal.csg;

import java.util.Timer;
import java.util.TimerTask;

import com.otn.tool.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import alu.nbi.opticscsg.OpticsIMSnmpEMLViewMib.SnmpEMLView;
import alu.nbi.opticscsg.OpticsIMSnmpEMLViewMib.SnmpEMLViewHelper;

import com.otn.tool.common.internal.csg.util.CorbaException;
import com.otn.tool.common.internal.csg.util.OrbInstance;
import com.otn.tool.common.internal.csg.util.SnmpException;
import com.otn.tool.common.internal.util.ConfigKey;

public class NeGroupEmlView
{
	private int groupID;

	private volatile boolean valid;

	private SnmpEMLView emlView;

	private EmlViewChecker checker;

	private static Timer timer = new Timer();

	private static Log log = LogFactory.getLog(NeGroupEmlView.class);

	private NeGroupEmlView(int groupID, SnmpEMLView emlView)
	{
		this.groupID = groupID;
		this.emlView = emlView;
		validate();
		if (isValid()) startValidation();
	}

	public static NeGroupEmlView create(int groupID, String nameServiceURL)
			throws SnmpException
	{
		try
		{
			SnmpEMLView view =
					SnmpEMLViewHelper.narrow(OrbInstance.getInstance().resolveNSPath(
							nameServiceURL, getCsgEmlViewName(groupID)));
			NeGroupEmlView emlView = new NeGroupEmlView(groupID, view);
			if (emlView.isValid()) return emlView;
			throw new SnmpException("EmlView not valid: " + groupID);
		}
		catch (CorbaException e)
		{
			throw new SnmpException("Failed to create EmlView: " + groupID, e);
		}
	}

	public boolean isValid()
	{
		return valid;
	}

	public int getGroupId()
	{
		return groupID;
	}

	public SnmpEMLView getView()
	{
		return emlView;
	}

	public void stopValidation()
	{
		if (checker != null) checker.cancel();
	}

	private void startValidation()
	{
		checker = new EmlViewChecker();
		long heartbeatInterval =
				Conf.instance().getPropertiesMap().containsKey(ConfigKey.EMLVIEW_HEART_BEAT_INTERVAL) ?
				Long.valueOf(Conf.instance().getProperty(ConfigKey.EMLVIEW_HEART_BEAT_INTERVAL)) :
						ConfigKey.DEFAULT_EMLVIEW_HEART_BEAT_INTERVAL;
		timer.schedule(checker, heartbeatInterval, heartbeatInterval);
	}

	private void validate()
	{
		try
		{
			valid = emlView.heartbeat();
		}
		catch (Exception e)
		{
			valid = false;
			log.warn("SnmpEMLView failure", e);
		}
	}

	private static String getCsgEmlViewName(int groupID)
	{
		String temp = Conf.instance().getPropertiesMap().containsKey(ConfigKey.CSG_EMLVIEW_NAME_PATTERN) ?
				Conf.instance().getProperty(ConfigKey.CSG_EMLVIEW_NAME_PATTERN) :
				ConfigKey.DEFAULT_CSG_EMLVIEW_NAME_PATTERN;
		return temp.replaceFirst("#", String.valueOf(groupID));
	}

	private class EmlViewChecker extends TimerTask
	{
		@Override
		public void run()
		{
			validate();
		}
	}
}