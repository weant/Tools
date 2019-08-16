package com.hcop.ptn.common.omsinfo;

public class IpAndHost {
    private String ip;
    private String hostname;
    private String context;

    public IpAndHost(String ip, String hostname) {
        this.ip = ip;
        this.hostname = hostname;

        this.context = this.ip + "  " + this.hostname;
    }

    public IpAndHost(String context) {
        this.context = context;

        String line = context.trim();
        if(line != null && !"".equals(line)) {
            int index = line.indexOf(" ");
            int lastIndex = line.lastIndexOf(" ");
            if(index != -1) {
                this.ip = line.substring(0, index);
                this.hostname = line.substring(lastIndex + 1);
            } else {
                this.ip = line;
            }
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IpAndHost other = (IpAndHost) obj;

        if (hostname == null) {
            if (other.hostname != null) {
                return false;
            }
        } else if (!hostname.equals(other.hostname)) {
            return false;
        }
        if (ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!ip.equals(other.ip)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ip + "-" + hostname;
    }

    public String getIp() {
        return ip == null ? "" : ip;
    }

    public String getHostname() {
        return hostname == null ? "" : hostname;
    }

    public String getContext() {
        return this.context;
    }
}
