package com.otn.tool.common.omsinfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostsUtils {
    private static Log log = LogFactory.getLog(HostsUtils.class);

    private static HostsUtils instance;

    public static HostsUtils getInstance(){
        if(instance == null) {
            instance = new HostsUtils();
        }
        return instance;
    }

    public void updateHostFile(Set<IpAndHost> nmsIPHostnameList) {
        List<IpAndHost> existingHosts = getExistingHosts();

        List<IpAndHost> excluedList = new ArrayList<>();
        for (IpAndHost address : existingHosts) {
            for (IpAndHost newAddress : nmsIPHostnameList) {
                if (address.getHostname().equals(newAddress.getHostname())
                        || address.getIp().equals(newAddress.getIp())) {
                    excluedList.add(address);
                    log.error("remove host:[" + address.getContext() + "]");
                }
            }
        }

        existingHosts.removeAll(excluedList);
        existingHosts.addAll(nmsIPHostnameList);

        writeHosts(existingHosts);
    }

    public boolean removeFromHosts(Set<String> ipHosts) {
        if(ipHosts == null || ipHosts.isEmpty()) {
            return true;
        }

        List<IpAndHost> existingHosts = getExistingHosts();
        List<IpAndHost> removedHosts = new ArrayList<>();
        for(String line : ipHosts) {
            removedHosts.add(new IpAndHost(line));
        }

        existingHosts.removeAll(removedHosts);
        writeHosts(existingHosts);

        return true;
    }

    private List<IpAndHost> getExistingHosts() {
        List<IpAndHost> existingHosts = new ArrayList<> ();

        String osName = System.getProperty("os.name");
        String hostFile = "/etc/hosts";
        if (osName.contains("Win") || osName.contains("win") || osName.contains("WIN")) {
            Map<String, String> m = System.getenv();
            String winDir = m.get("windir");
            if (winDir == null) {
                winDir = m.get("WINDIR");
            }

            if (winDir == null) {
                log.error("Can't found env variable 'windir' or 'WINDIR', please edit hosts manually.");
                return existingHosts;
            }
            hostFile = winDir + "\\system32\\drivers\\etc\\hosts";
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(hostFile));
            String line;
            while ((line = in.readLine()) != null) {
                IpAndHost ipAddress = new IpAndHost(line);
                existingHosts.add(ipAddress);
            }
            in.close();
        } catch (FileNotFoundException e) {
            log.error("Can't found hosts file, please edit it manually.", e);
        } catch (IOException e) {
            log.error("Can't read or write hosts file, please edit it manually.", e);
        }

        return existingHosts;
    }

    private String getHostFile() {
        String osName = System.getProperty("os.name");
        String hostFile = "/etc/hosts";
        if (osName.contains("Win") || osName.contains("win") || osName.contains("WIN")) {
            Map<String, String> m = System.getenv();
            String winDir = m.get("windir");
            if (winDir == null) {
                winDir = m.get("WINDIR");
            }

            if (winDir == null) {
                log.error("Can't found env variable 'windir' or 'WINDIR', please edit hosts manually.");
                return null;
            }

            hostFile = winDir + "\\system32\\drivers\\etc\\hosts";
        }

        return hostFile;
    }

    private boolean writeHosts(List<IpAndHost> hosts) {
        String hostFile = getHostFile();
        String newLine = getCRLF();

        try {
            FileWriter fw = new FileWriter(hostFile);

            for (IpAndHost address : hosts) {
                fw.write(address.getContext() + newLine);
            }

            fw.close();
        } catch (IOException e) {
            log.error("Can't write hosts file, please edit it manually.", e);
        }

        return true;
    }

    private String getCRLF() {
        String osName = System.getProperty("os.name");
        String newLine = "\n";
        if (osName.contains("Win") || osName.contains("win") || osName.contains("WIN")) {
            Map<String, String> m = System.getenv();
            String winDir = m.get("windir");
            if (winDir == null) {
                winDir = m.get("WINDIR");
            }

            if (winDir == null) {
                log.error("Can't found env variable 'windir' or 'WINDIR', please edit hosts manually.");
            }

            newLine = "\r\n";
        }

        return newLine;
    }
}
