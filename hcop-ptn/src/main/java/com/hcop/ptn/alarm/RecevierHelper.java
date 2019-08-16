package com.hcop.ptn.alarm;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class RecevierHelper {
	
	public static String getV4Ips() {
		String result = "";
		try {
			StringBuilder ips = new StringBuilder();
			Enumeration<NetworkInterface> interfs = NetworkInterface.getNetworkInterfaces();
			while (interfs.hasMoreElements()) {
				NetworkInterface interf = interfs.nextElement();
				Enumeration<InetAddress> addres = interf.getInetAddresses();
				while (addres.hasMoreElements()) {
					InetAddress in = addres.nextElement();
					if (in instanceof Inet4Address) {
						if (!in.getHostAddress().equals("127.0.0.1")) {
							ips.append(in.getHostAddress());
							ips.append("|");
						}
					}
				}
			}
			if (ips.length() > 0) {
				result = ips.substring(0, ips.length() - 1);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return result;
	}
	
}
