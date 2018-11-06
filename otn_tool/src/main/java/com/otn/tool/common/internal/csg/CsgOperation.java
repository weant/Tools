package com.otn.tool.common.internal.csg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Any;
import com.otn.tool.common.internal.csg.util.CsgUtil;
import com.otn.tool.common.internal.csg.util.OrbInstance;
import com.otn.tool.common.internal.csg.util.SnmpException;
import alu.nbi.opticscsg.NetworkAdministration.ActionInfos;
import alu.nbi.opticscsg.NetworkAdministration.ActionInfosHolder;
import alu.nbi.opticscsg.NetworkAdministrationReply.NetworkAdminReplyPOATie;
import alu.nbi.opticscsg.NetworkDefs.ActionStatus;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetColumnFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetContRectanglesByRowFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetContRectanglesFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetMultiColumnByRowFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetMultiColumnFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetMultiObjFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetNextFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetRectanglesByRowFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetRectanglesFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.GetTableFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.SetFailed;
import alu.nbi.opticscsg.OpticsIMCsg.CsgGroupPackage.SetMultiObjFailed;
import alu.nbi.opticscsg.OpticsIMCsgVar.CsgVarPackage.AttrListHolder;
import alu.nbi.opticscsg.OpticsIMCsgVar.CsgVarPackage.AttrVal;
import alu.nbi.opticscsg.OpticsIMCsgVar.CsgVarPackage.AttrValHolder;
import alu.nbi.opticscsg.OpticsIMSnmpEMLViewMib.SnmpEMLView;

public class CsgOperation
{
	private static Log log = LogFactory.getLog(CsgOperation.class);
	public static AttrVal get(int groupID, int neID, AttrVal value) throws SnmpException
	{
		try
		{
			AttrValHolder holder = new AttrValHolder(value);
			getEmlView(groupID).Get(CsgUtil.toNeName(neID), holder);
			return holder.value;
		}
		catch (GetFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getNext(int groupID, int neID, AttrVal[] value)
			throws SnmpException
	{
		try
		{
			AttrListHolder holder = new AttrListHolder(value);
			CsgOperation.getEmlView(groupID).GetNext(CsgUtil.toNeName(neID), holder);
			return holder.value;
		}
		catch (GetNextFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getMultiObj(int groupID, int neID, AttrVal[] value)
			throws SnmpException
	{
		try
		{
			AttrListHolder holder = new AttrListHolder(value);
			getEmlView(groupID).GetMultiObj(CsgUtil.toNeName(neID), holder);
			return holder.value;
		}
		catch (GetMultiObjFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getColumn(int groupID, int neID, String columnOid)
			throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetColumn(CsgUtil.toNeName(neID), columnOid, holder);
			return holder.value;
		}
		catch (GetColumnFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getMultiColumn(int groupID, int neID, String[] columnOidList)
			throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetMultiColumn(CsgUtil.toNeName(neID), columnOidList, holder);
			return holder.value;
		}
		catch (GetMultiColumnFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getMultiColumnByRow(int groupID, int neID, String[] columnOidList)
			throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID)
					.GetMultiColumnByRow(CsgUtil.toNeName(neID), columnOidList, holder);
			return holder.value;
		}
		catch (GetMultiColumnByRowFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getTable(int groupID, int neID, String tableOid)
			throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetTable(CsgUtil.toNeName(neID), tableOid, holder);
			return holder.value;
		}
		catch (GetTableFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getContRectangles(int groupID, int neID, String[] columnOidList,
			Any[] fromIndex, Any[] toIndex, short repetitions) throws SnmpException
	{
		log.debug("From index:" + CsgUtil.toString(fromIndex));
		log.debug("To index:" + CsgUtil.toString(toIndex));
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetContRectangles(CsgUtil.toNeName(neID), columnOidList,
					fromIndex, toIndex, repetitions, holder);
			return holder.value;
		}
		catch (GetContRectanglesFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getContRectanglesByRow(int groupID, int neID,
			String[] columnOidList, Any[] fromIndex, Any[] toIndex, short repetitions)
			throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetContRectanglesByRow(CsgUtil.toNeName(neID), columnOidList,
					fromIndex, toIndex, repetitions, holder);
			return holder.value;
		}
		catch (GetContRectanglesByRowFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getRectangles(int groupID, int neID, String[] columnOidList,
			Any[][] instanceList) throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetRectangles(CsgUtil.toNeName(neID), columnOidList,
					instanceList, holder);
			return holder.value;
		}
		catch (GetRectanglesFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static AttrVal[] getRectanglesByRow(int groupID, int neID, String[] columnOidList,
			Any[][] instanceList) throws SnmpException
	{
		AttrListHolder holder = new AttrListHolder();
		try
		{
			getEmlView(groupID).GetRectanglesByRow(CsgUtil.toNeName(neID), columnOidList,
					instanceList, holder);
			return holder.value;
		}
		catch (GetRectanglesByRowFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static void set(int groupID, int neID, AttrVal value) throws SnmpException
	{
		try
		{
			getEmlView(groupID).Set(CsgUtil.toNeName(neID), value);
		}
		catch (SetFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static void setMultiObj(int groupID, int neID, AttrVal[] value) throws SnmpException
	{
		try
		{
			getEmlView(groupID).SetMultiObj(CsgUtil.toNeName(neID), value);
		}
		catch (SetMultiObjFailed e)
		{
			throw toSnmpException(e);
		}
		catch (Exception e)
		{
			throw toSnmpException(e);
		}
	}

	public static void action_on_network_element(int groupID, int neID, ActionInfos infos)
			throws SnmpException
	{
		NetworkAdminReplyPOATie reply = new NetworkAdminReplyPOATie(new DefNetworkAdminReply());
		OrbInstance.getInstance().activeObject(reply);
		ActionStatus s =
				getEmlView(groupID).action_on_network_element(CsgUtil.toNeName(neID), infos,
						reply._this(), new ActionInfosHolder());
		if (s.value() == ActionStatus._FAILURE)
		{
			throw new SnmpException("Action failure.");
		}
	}

	private static SnmpEMLView getEmlView(int groupID) throws SnmpException
	{
		return SnmpEmlViewManager.getInstance().getEmlView(groupID);
	}

	private static SnmpException toSnmpException(GetFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetMultiObjFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetColumnFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetMultiColumnFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetMultiColumnByRowFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetTableFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetContRectanglesFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetContRectanglesByRowFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetRectanglesFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetRectanglesByRowFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(GetNextFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(SetFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(SetMultiObjFailed exception)
	{
		return new SnmpException(exception.status.reason.toString());
	}

	private static SnmpException toSnmpException(Exception exception)
	{
		if (exception instanceof SnmpException) return (SnmpException) exception;
		return new SnmpException(exception);
	}
}
