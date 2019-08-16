package com.hcop.ptn.common.jcorba;

import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class Utilities {
	public static org.omg.CORBA.Object resolveNSPath(NamingContextExt ctx, String namePath) 
			throws org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, CannotProceed {
		String[] path = namePath.split("/");
		NameComponent[] names = new NameComponent[path.length];
		for (int i = 0; i < path.length; ++i) {
			//NameComponent[] nc = ctx.to_name(path[i]);
			//names[i] = nc[0];
			names[i] = new NameComponent();
			names[i].id = path[i];
			names[i].kind = "";
		}
		return ctx.resolve(names);
	}
	
	public static String deleteDot(String dotString) {
		String result = "";
		if (dotString != null && dotString.length() != 0) {
			result = dotString.replace(".", "");
		}
		return result;
	}
}
