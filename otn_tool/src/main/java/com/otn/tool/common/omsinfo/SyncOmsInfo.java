package com.otn.tool.common.omsinfo;

import com.alu.tools.basic.io.FileUtil;
import com.otn.tool.common.omsinfo.dynabean.EqualableDynaBeanClass;
import com.otn.tool.common.restful.RestFulConstant;
import com.otn.tool.common.utils.SshConnect;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncOmsInfo {

    private static Log LOG = LogFactory.getLog(SyncOmsInfo.class);
    private static final String USER = "root";
    private static final String PASSWORD="Install10";
    private static DynaClass instanceClazz=null;
    private static String usmHostName;
    private static String omsVersion;
    private static String usmIp;
    private static String usmUser;
    private static String usmPassword;
    private static String deafultInstanceNum;
    private static String lineSeparator = System.getProperty("line.separator");
    private static Set<IpAndHost> ipAndHostSet;

    static {
        DynaProperty[] props = new DynaProperty[]{
                new DynaProperty("hostname", String.class),
                new DynaProperty("dbPort", String.class),
                new DynaProperty("dir", String.class),
                new DynaProperty("ipAddress", String.class),
                new DynaProperty("nsPort", String.class),
                new DynaProperty("instanceNum", String.class),
                new DynaProperty("version", String.class)
        };

        EqualableDynaBeanClass dClazz = new EqualableDynaBeanClass("InstanceData", props, new String[]{"hostname"});

        instanceClazz = dClazz;
    }

    public static boolean loadConf(String ip, String user, String password) throws Exception{
        String username = user == null ? USER : user;
        String ps  = password == null ? PASSWORD : password;
        ipAndHostSet = new HashSet<>();
        usmIp = ip;
        usmUser = username;
        usmPassword = ps;
        SshConnect sshSession = new SshConnect(ip, username, ps);
        sshSession.login();

        //获取网管版本
        getNmsVersion(sshSession);

        //获取虚拟机实例信息(host,dbport,path,instancenum)
        Collection<DynaBean> instanceData = getInstanceInfo(sshSession);

        //获取虚拟机的ip信息
        obtainAndSetIPAddressOfInstances(sshSession, instanceData);

        //获取并设置每个Instance对应的ns_port
        obtainAndSetNSPortInfoOfInstances(sshSession, instanceData);
        //获取并初始化实例数据中的版本信息
        obtainAndSetVersionInfoOfInstances(sshSession, instanceData);

        //获取eml实例
        Collection<DynaBean> emlInstances = getInstancesWithType(instanceData);

        //根据EML实例获取csg文件所需信息
        generateCsgFile(emlInstances, sshSession);

        //获取USM hostname
        usmHostName = getUSMName(sshSession);
        usmHostName = usmHostName.replace("\r\n", "").trim();

        sshSession.closeConn();

        saveFile(instanceData);

        HostsUtils.getInstance().updateHostFile(ipAndHostSet);

        return true;
    }


    private static void saveFile(Collection<DynaBean> instanceData) {
        StringBuilder emlInstances = new StringBuilder();
        StringBuilder otnInstances = new StringBuilder();

        ipAndHostSet.add(new IpAndHost(usmIp, usmHostName));

        StringBuilder sb = new StringBuilder();
        sb.append("USM_HOSTNAME=");
        sb.append(usmHostName);
        sb.append(lineSeparator);

        sb.append("USM_IP=");
        sb.append(usmIp);
        sb.append(lineSeparator);

        sb.append("USM_USER=");
        sb.append(usmUser);
        sb.append(lineSeparator);

        sb.append("USM_PASSWORD=");
        sb.append(usmPassword);
        sb.append(lineSeparator);

        sb.append("REST_PORT=");
        sb.append(RestFulConstant.OTN_PORT);
        sb.append(lineSeparator);

        sb.append("REST_USER=");
        sb.append(RestFulConstant.GUI_USERNAME);
        sb.append(lineSeparator);

        sb.append("REST_PASSWORD=");
        sb.append(RestFulConstant.GUI_PASSWORD);
        sb.append(lineSeparator);

        sb.append("OMS_VERSION=");
        sb.append(omsVersion);
        sb.append(lineSeparator);

        for(DynaBean bean : instanceData) {
            if(bean.get("dir").toString().contains("EML") || bean.get("dir").toString().contains("OTNE")) {
                emlInstances.append(bean.get("instanceNum").toString());
                emlInstances.append(",");
            }
            if(bean.get("dir").toString().contains("OTN")) {
                otnInstances.append(bean.get("instanceNum").toString());
                otnInstances.append(",");
            }

            ipAndHostSet.add(new IpAndHost(bean.get("ipAddress").toString(), bean.get("hostname").toString()));

            String server = bean.get("dir").toString();
            sb.append(server +"_HOSTNAME=");
            sb.append(bean.get("hostname").toString());
            sb.append(lineSeparator);

            sb.append(server +"_IP=");
            sb.append(bean.get("ipAddress").toString());
            sb.append(lineSeparator);

            sb.append(server +"_DB_PORT=");
            sb.append(bean.get("dbPort").toString());
            sb.append(lineSeparator);

            sb.append(server +"_NS_PORT=");
            sb.append(bean.get("nsPort").toString());
            sb.append(lineSeparator);

            sb.append(server +"_USER=");
            sb.append("alcatel");
            sb.append(lineSeparator);

            sb.append(server +"_PASSWORD=");
            sb.append("alcatel");
            sb.append(lineSeparator);
        }

        sb.append("EML_INSTANCE=");
        if(emlInstances.length() > 0) {
            sb.append(emlInstances.substring(0, emlInstances.lastIndexOf(",")));
        }
        sb.append(lineSeparator);

        sb.append("OTN_INSTANCE=");
        if(otnInstances.length() > 0) {
            sb.append(otnInstances.substring(0, otnInstances.lastIndexOf(",")));
        }

        String omsFile = "/conf/omsConf.properties";
        generateFile(sb.toString(), omsFile);
    }

    /**
     * 生成csgFile
     * @param emlInstances
     * @throws Exception
     */

    private static void generateCsgFile(Collection<DynaBean> emlInstances, SshConnect sshSession) throws Exception {
//		/usr/Systems/EML_1/conf/NameServerFile.nr6;
        String cmd=null;
        String reg="EML_(\\d+)_SNA\"(\\s*:\\s*[\\s\\.A-Za-z0-9\"=]+){2}\\s*:\\s*PORT\\s*=\\s*(\\d+);";

        /*
         * sna content is like below:
         *
         * 100  eml9637  5212
         * 101  eml9637  5256

         *
         */
        StringBuilder snaContent = new StringBuilder();
        /*
         * csg content is like below:
         *
         * 100,101  eml9637  5040
         *
         */
        StringBuilder csgContent = new StringBuilder();

        /*
         *
         * // gth content is like below:
         * 100,101 oms96
         *
         *
         */
        StringBuilder gthContent = new StringBuilder();
        for(DynaBean emlInstance:emlInstances){
            String dir = (String)emlInstance.get("dir");
            cmd=" grep SNA /usr/Systems/"+dir+"/conf/NameServerFile.nr6";
            String[] result = sshSession.execShell(cmd);
            Collection<String> keyStr = findKeyStr(result[0], reg, new int[]{1,3});

            String hostname = (String)emlInstance.get("hostname");
            String nsPort = (String)emlInstance.get("nsPort");
            int i=1;
            for(String s: keyStr){
                if(i%2==1){//snaNo;
                    snaContent.append(s);
                    snaContent.append(" ");
                    snaContent.append(hostname);

                    if(i > 1){
                        csgContent.append(",");
                        gthContent.append(",");
                    }
                    csgContent.append(s);
                    gthContent.append(s);

                }else{//snaPort;
                    snaContent.append(" ");
                    snaContent.append(s);
                    snaContent.append("\r\n");
                }
                i++;
            }

            csgContent.append(" ");
            csgContent.append(hostname);
            csgContent.append(" ");
            csgContent.append(nsPort);
            csgContent.append("\r\n");


            gthContent.append(" ");
            gthContent.append(hostname);
            gthContent.append("\r\n");


        }


        String csgFile = "/conf/csgConf.txt";
        String snaFile = "/conf/snaConf.txt";
        String gthFile ="/conf/gthConf.txt";

        generateFile(csgContent.toString(),csgFile);
        generateFile(snaContent.toString(),snaFile);
        generateFile(gthContent.toString(),gthFile);
    }


    /**
     * @param content
     * @param filename
     */
    private static void generateFile(String content, String filename) {
        File currentDir = new File(".");
        String folderPath = currentDir.getAbsolutePath() + filename;
        FileUtil.write(new File(folderPath), content, false);
    }


    @SuppressWarnings("unchecked")
    private static Collection<DynaBean> getInstancesWithType(Collection<DynaBean> instanceData) {

        String[] keys = new String[]{"EML"};
        Collection<DynaBean> output = Collections.emptyList();
        for(final String key : keys){
            output =CollectionUtils.select(instanceData, new Predicate(){
                @Override
                public boolean evaluate(Object object) {
                    DynaBean bean = (DynaBean)object;
                    String dir = (String)bean.get("dir");

                    int version = Integer.parseInt(bean.get("version").toString());
                    if(version >= 14 && dir.indexOf("OTNE") != -1 && "EML".equals(key)) {
                        return true;
                    }

                    return dir.indexOf(key)!=-1;
                }

            });

            if(!output.isEmpty()){
                break;
            }
        }

        return output;
    }


    /**
     *
     * 获取实例数据对应的版本信息
     * @param sshSession
     * @param instanceData
     * @throws Exception
     */
    private static void obtainAndSetVersionInfoOfInstances(
            SshConnect sshSession, Collection<DynaBean> instanceData) throws Exception {
        String cmd = "cat /alu/Kernel/data/TOC";
        String[] result = sshSession.execShell(cmd);
        String reg = "([A-Z]+_\\d+)\\-(\\d+(\\.\\d+)?)";
        Collection<String> keyStr = findKeyStr(result[0], reg, new int[]{1,2});
        LOG.info("obtainAndSetVersionInfoOfInstances TOC : " + keyStr.toString());

        //将String集合组织成kv形式 k=dir v=version
        Map<String,String> mappings = new HashMap<>();
        int i=1;
        String dir=null;
        String version=null;
        for(String s : keyStr){
            if(i%2==1){
                dir = s;
            }else{
                version=s;
                mappings.put(dir, version);
            }
            i++;
        }
        //遍历instanceData集合，取出每个instance的dir值，在mapping中查找对应version值，并设置
        for(DynaBean bean:instanceData){
            String dirKey = (String)bean.get("dir");
            bean.set("version", mappings.get(dirKey));
        }

    }


    /**
     *
     * 获取并设置实例数据的nsPort字段内容
     * @param sshSession
     * @param instanceData
     */
    private static void obtainAndSetNSPortInfoOfInstances(
            SshConnect sshSession, Collection<DynaBean> instanceData) {
        for(DynaBean bean: instanceData){
            String dir = (String)bean.get("dir");

            try {
                bean.set("nsPort", getNSPort(sshSession,dir));
            } catch (Exception e) {
                String hostname = (String)bean.get("hostname");
                LOG.error("can't obtain nsPort for "+hostname+",and it's dir is "+dir,e);
            }
        }
    }


    private static Object getNSPort(SshConnect sshSession, String dir) throws Exception {
        String file = "/usr/Systems/"+dir+"/conf/NameServerFile.nr6";

        String cmd = "grep NameServerProcess "+file;
        String[] result = sshSession.execShell(cmd);

        String reg = "PORT\\s*=\\s*(\\d+)";
        Collection<String> ports = findKeyStr(result[0], reg, new int[]{1});
        LOG.info("getNSPort NameServerFile : " + ports.toString());
        if(ports!=null&&!ports.isEmpty()){
            Iterator<String> it = ports.iterator();
            String port = it.next();
            return port;
        }
        return null;
    }


    /**
     *
     * 获取并设置实例的ip地址信息
     * 后置条件:实例的ip地址被设置
     * @param sshSession
     * @param instanceData
     * @throws Exception
     */
    private static void obtainAndSetIPAddressOfInstances(SshConnect sshSession,
                                                         Collection<DynaBean> instanceData) throws Exception {
        String reg = "((\\d{1,3}\\.){3}\\d{1,3})\\s+(\\w+)";

        String file="/etc/hosts";
        String command = " cat -n "+file;
        String[] result = sshSession.execShell(command);
        Collection<String> strList = findKeyStr(result[0], reg,new int[]{1,3});
        LOG.info("obtainAndSetIPAddressOfInstances hosts : " + strList.toString());

        parseAndSetIP(instanceData, strList);
    }


    /**
     * 解析并设置实例数据的ip地址信息
     * @param instanceData
     * @param strList
     */
    private static void parseAndSetIP(Collection<DynaBean> instanceData,
                                      Collection<String> strList) {

        /*
         * 将String集合组织成kv的map形式。 k=host, v=ip
         */
        String ip=null;
        String host=null;
        Map<String,String> mappings = new HashMap<>();
        int i=1;
        for(String s:strList){
            if(i%2==0){
                host=s;
                mappings.put(host, ip);
            }else{
                ip=s;
            }
            i++;
        }


        for(DynaBean bean: instanceData){
            String hostname = (String)bean.get("hostname");
            ip = mappings.get(hostname);
            bean.set("ipAddress", ip);
        }
    }

    /**
     *
     * 获取实例信息网管版本
     * @param sshSession
     * @return
     * @throws Exception
     */
    private static void getNmsVersion(SshConnect sshSession) throws Exception {
        String command = "cd /usr/Systems/;ls | egrep 'OTN.*_(Master|Presentation)'";
        String[] result = sshSession.execShell(command);
        String[] splitArray = result[0].trim().split("_");
        deafultInstanceNum = splitArray[1];
        omsVersion = splitArray[2];
    }


    /**
     *
     * 获取实例信息 包括hostname
     * @param sshSession
     * @return
     * @throws Exception
     */
    private static Collection<DynaBean> getInstanceInfo(SshConnect sshSession) throws Exception {
        String file="/usr/Systems/Global_Instance/CPMGUISVR/conf/param.cfg";
        String filter="DBServer_1_Base_1_URL";
        String command = "grep "+filter+" "+file;
        String[] result = sshSession.execShell(command);
        String reg = "(\\w+:\\d+:\\w+)";
        Collection<String> strList = findKeyStr(result[0], reg, new int[]{1});
        LOG.info("getInstanceInfo param: " + strList.toString());
        return parseInstanceData(strList);

    }


    /**
     * 根据strList信息生成instanceData
     * @param strList
     * @return
     * @throws Exception
     */
    private static Collection<DynaBean> parseInstanceData(
            Collection<String> strList) throws Exception {
        HashSet<DynaBean> beans = new HashSet<>();
        for(String s: strList){
            beans.add(parseInstance(s));
        }
        return beans;
    }


    /**
     * 解析实例
     * @param s
     * @return
     * @throws Exception
     */
    private static DynaBean parseInstance(String s) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(s,":");
        DynaBean instanceData = createInstanceData();
        String host = tokenizer.nextToken();
        instanceData.set("hostname", host);

        String dbPort = tokenizer.nextToken();
        instanceData.set("dbPort", dbPort);

        String sid = tokenizer.nextToken();
        initDirAndInstanceNO(sid,instanceData);
        return instanceData;
    }


    /**
     * 根据sid获取路径信息
     * @param sid
     * @return
     */
    private static void initDirAndInstanceNO(String sid, DynaBean instance) {
        if(Integer.valueOf(omsVersion) >= 18) {
            instance.set("instanceNum", deafultInstanceNum);
            instance.set("dir", String.join("_", sid,deafultInstanceNum));
        } else {
            String reg="([A-Za-z]+)(\\d+)(_(\\d+))";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(sid);
            if(matcher.find()){
                String prefix = matcher.group(1);
                String instanceNumStr=matcher.group(3);
                String instanceNum = matcher.group(4);
                String dir="";
                if(sid.indexOf("be")!=-1){
                    dir = "PKT"+instanceNumStr;
                }else{
                    String versionStr = matcher.group(2);
                    int versionInt = Integer.parseInt(versionStr);
                    if(versionInt >= 14) {
                        if(prefix.toUpperCase().indexOf("OTN") != -1) {
                            prefix = "OTNE";
                        }
                    }
                    //prefix = NmsVersionUtil.getNameInRemoteHost(versionInt, null, prefix);
                    dir= (prefix + instanceNumStr).toUpperCase();
                }

                instance.set("instanceNum", instanceNum);
                instance.set("dir", dir);
            }else{
                throw new RuntimeException("can't parse this sid \""+sid+"\" with regex ([A-Za-z]+)(\\d+)(_\\d+)");
            }
        }
    }


    /**
     * 根据reg在input中查找关键信息
     * @param input
     * @param reg
     * @return
     */
    private static Collection<String> findKeyStr(String input, String reg,int[] indices) {

        ArrayList<String> matchStr = new ArrayList<>();
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(input);

        boolean found = false;
        while(matcher.find()){
            int gc = matcher.groupCount();
            for(int i = 0; i <= gc; i++){
                found=false;
                //查找当前i是否在indices数组中，若在说明是需要的groupIndex
                for(int j=0, length=indices.length; j<length ;j++ ){
                    if(indices[j] == i){
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    continue;
                }

                matchStr.add(matcher.group(i));
            }


        }
        return matchStr;
    }

    private static DynaBean createInstanceData() throws Exception{
        return instanceClazz.newInstance();
    }

    private static String getUSMName(SshConnect sshSession){
        String cmd = "hostname";
        try {
            String[] result = sshSession.execShell(cmd);
            return result[0];
        } catch (Exception e) {
            LOG.error("get usm hostname failed!", e);
        }
        return "";
    }

    public static void main(String[] args){
        try {
            SyncOmsInfo.loadConf("135.251.96.82","root","Install10");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
