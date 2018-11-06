package com.otn.tool.common.internal.csg;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.otn.tool.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import alu.nbi.opticscsg.OpticsIMSnmpEMLViewMib.SnmpEMLView;

import com.otn.tool.common.internal.csg.util.SnmpException;
import com.otn.tool.common.internal.util.ConfigKey;
import com.alu.tools.basic.StringUtil;
import com.alu.tools.basic.io.FileUtil;
import com.alu.tools.basic.io.handler.ILineHandler;

public class SnmpEmlViewManager
{
	private Map<Integer, String> groupIdToNameServiceURL = new HashMap<Integer, String>();

	private Map<Integer, NeGroupEmlView> groupIdToView = new HashMap<Integer, NeGroupEmlView>();

	private static SnmpEmlViewManager instance = new SnmpEmlViewManager();

	private static Log log = LogFactory.getLog(SnmpEmlViewManager.class);

	private SnmpEmlViewManager()
	{
	}

	public static SnmpEmlViewManager getInstance()
	{
		return instance;
	}

	public void init()
	{
		FileUtil.read(
				new File(Conf.instance().getProperty(ConfigKey.CSG_EMLVIEW_CONF_FILE)),
				new ILineHandler()
				{
					private Pattern blank = Pattern.compile("\\s+");

					@Override
					public boolean handle(String line)
					{
						if (line.startsWith("//")) return true;
						String[] conf = blank.split(line);
						if (conf.length == 3)
						{
							int port = Integer.parseInt(conf[2]);
							for (String group : StringUtil.split(conf[0], ","))
							{
								groupIdToNameServiceURL.put(new Integer(group),
										getNameServiceURL(conf[1], port));
							}
						}
						return true;
					}
				});
	}

	public SnmpEMLView getEmlView(int groupID) throws SnmpException
	{
		SnmpEMLView view = findEmlView(groupID);
		if (view != null) return view;
		return createEmlView(groupID);
	}

	public void destroy()
	{
		for (NeGroupEmlView view : groupIdToView.values())
		{
			view.stopValidation();
		}
		groupIdToView.clear();
		groupIdToNameServiceURL.clear();
	}

	private synchronized SnmpEMLView createEmlView(int groupID) throws SnmpException
	{
		SnmpEMLView view = findEmlView(groupID);
		if (view != null) return view;
		String nameServiceURL = groupIdToNameServiceURL.get(groupID);
		if (nameServiceURL == null) throw new SnmpException("Invalid SnmpEMLView groupID: "
				+ groupID);
		NeGroupEmlView emlView = NeGroupEmlView.create(groupID, nameServiceURL);
		groupIdToView.put(groupID, emlView);
		log.debug("Create SnmpEMLView for EML group: " + groupID);
		return emlView.getView();
	}

	private SnmpEMLView findEmlView(int groupID)
	{
		NeGroupEmlView view = groupIdToView.get(groupID);
		if (view == null) return null;
		if (view.isValid()) return view.getView();
		view.stopValidation();
		groupIdToView.remove(groupID);
		return null;
	}

	private String getNameServiceURL(String host, int port)
	{
		String temp = Conf.instance().getPropertiesMap().containsKey(ConfigKey.NAME_SERVICE_KEY) ?
				Conf.instance().getProperty(ConfigKey.NAME_SERVICE_KEY) :
				ConfigKey.DEFAULT_NAME_SERVICE_KEY;
		return "corbaloc::"
				+ host
				+ ":"
				+ port
				+ "/"
				+ temp;
	}
}
