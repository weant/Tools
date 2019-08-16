package com.hcop.ptn.common.internal.csg;

import alu.nbi.opticscsg.NetworkAdministrationReply.NetworkAdminReply;
import alu.nbi.opticscsg.NetworkDefs.ActionInfosListMore;
import alu.nbi.opticscsg.NetworkDefs.ActionInfosMore;
import alu.nbi.opticscsg.NetworkDefs.ActionResult;
import alu.nbi.opticscsg.NetworkDefs.ActionStatus;
import alu.nbi.opticscsg.OpticsIMIdlNeAction.IdlNeActPackage.ArchivingModeStruct;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;
import org.omg.CosNotification.Property;

public class DefNetworkAdminReply implements NetworkAdminReply
{
	private static final long serialVersionUID = 2221583185817529698L;

	public void action_on_managed_element_completed(ActionResult arg0)
	{
	}

	public void action_on_managed_element_list_completed(ActionResult[] arg0)
	{
	}

	public void action_on_network_element_completed(ActionInfosMore arg0)
	{
	}

	public void action_on_network_element_list_completed(ActionInfosListMore arg0)
	{
	}

	public void create_managed_element_completed(Property[] arg0, ActionStatus arg1)
	{
	}

	public void delete_managed_element_completed(ActionResult arg0)
	{
	}

	public void delete_managed_element_list_completed(ActionResult[] arg0)
	{
	}

	public void get_archiving_mode_completed(ArchivingModeStruct[] arg0)
	{
	}

	public void set_archiving_mode_completed(ActionInfosMore arg0)
	{
	}

	public void update_managed_element_completed(Property[] arg0, ActionStatus arg1)
	{
	}

	public boolean _is_a(String repositoryIdentifier)
	{
		return false;
	}

	public boolean _is_equivalent(Object other)
	{
		return false;
	}

	public boolean _non_existent()
	{
		return false;
	}

	public int _hash(int maximum)
	{
		return 0;
	}

	public Object _duplicate()
	{
		return null;
	}

	public void _release()
	{
	}

	public Object _get_interface_def()
	{
		return null;
	}

	public Request _request(String operation)
	{
		return null;
	}

	public Request _create_request(Context ctx, String operation, NVList arg_list,
			NamedValue result)
	{
		return null;
	}

	public Request _create_request(Context ctx, String operation, NVList arg_list,
			NamedValue result, ExceptionList exclist, ContextList ctxlist)
	{
		return null;
	}

	public Policy _get_policy(int policy_type)
	{
		return null;
	}

	public DomainManager[] _get_domain_managers()
	{
		return null;
	}

	public Object _set_policy_override(Policy[] policies, SetOverrideType set_add)
	{
		return null;
	}
}
