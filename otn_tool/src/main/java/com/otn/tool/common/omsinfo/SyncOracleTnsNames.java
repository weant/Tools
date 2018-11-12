package com.otn.tool.common.omsinfo;

import com.alu.tools.basic.exception.IoRuntimeException;
import com.alu.tools.basic.io.FileUtil;
import com.otn.tool.common.properties.OmsConf;
import com.otn.tool.common.utils.SshConnect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SyncOracleTnsNames {

	private  Log log = LogFactory.getLog(SyncOracleTnsNames.class);
	
	private static SyncOracleTnsNames loader  = new SyncOracleTnsNames();
	
	private String msg ="";
	
	private SyncOracleTnsNames(){
	}
	
	public static SyncOracleTnsNames init(){
		return loader;
	}

	public  void loadTnsNames(){
		msg ="";
		deleteOldFile();
		Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
		initAllFile(omsConfMap);
		
	}
	
	/**
	 * 删除旧文件
	 */
	private void deleteOldFile() {
		File currentDir = new File(".");
		String filename = currentDir.getAbsolutePath() +"/wallet";
		new File(filename).mkdirs();
		File file = new File(filename);
		delete(file);
	}
	
	private void delete(File files){
		if(files.isFile()){
			if(files.getName().equals("tnsnames.ora")){
				files.delete();
			}
		}else{
			for(File file:files.listFiles()){
				delete(file);
			}
		}
	}
	
	/**
	 * 读取服务器文件，转换为map，key值为sid_eml,sid_otn,sid_pkt,walkey_eml,walkey_otn,walkey_pkt
	 * @return
	 * @throws IOException 
	 */
	private void initAllFile(Map<String, String> omsConfMap){
		try {
			getEmlConfig2Map(omsConfMap);
		} catch (Exception e) {
			log.error("eml's file 'tnsnames.ora' get error",e);
			msg +="read eml oracle config error!\r\n";
		}
		
		try {
			getOtnConfig2Map(omsConfMap);
		} catch (Exception e) {
			log.error("otn's file 'tnsnames.ora' get error",e);
			msg +="read ont oracle config error!\r\n";
		}
		
	}
	

	/**
	 * 读取otn
	 * @return
	 * @throws Exception
	 */
	private void getOtnConfig2Map(Map<String, String> omsConfMap) throws Exception {
		String[] otnInstances = omsConfMap.get("OTN_INSTANCE").split(",");
		for(int i = 0; i < otnInstances.length; i++) {
			String instance = otnInstances[i];
			String name;
			if(omsConfMap.containsKey("OTN_"+instance+"_IP")){
				name = "OTN";
			} else if (omsConfMap.containsKey("OTNE_"+instance+"_IP")) {
				name = "OTNE";
			} else {
				continue;
			}

			String otnPassword = omsConfMap.get(name+"_"+instance+"_PASSWORD");;
			String otnUser = omsConfMap.get(name+"_"+instance+"_USER");
			String otnIp = omsConfMap.get(name+"_"+instance+"_IP");
			SshConnect sshSession = new SshConnect(otnIp, otnUser, otnPassword);
			sshSession.login();

			String hostName = omsConfMap.get(name+"_"+instance+"_HOSTNAME").toUpperCase();
			String otnName = hostName.toUpperCase().indexOf("EML") == -1 ? "OTNE" : "OTN";;
			String cmd = "cat /usr/Systems/" + otnName + "_" + instance + "/ORACLE/network/admin/tnsnames.ora | grep -v '#'";

			String[] result = sshSession.execShell(cmd);
			sshSession.closeConn();
			List<String> values = sortMapValues(analysisStr(result[0].trim(), "EML" + instance));
			saveMap2File(values, splitFileStr("otn_" + instance));
		}
	}

	/**
	 * eml tnsnames读取
	 * @return  key值为sid_eml,walkey_eml的map
	 * @throws Exception
	 */
	private  void getEmlConfig2Map(Map<String, String> omsConfMap)throws Exception{
		String[] emlInstances = omsConfMap.get("EML_INSTANCE").split(",");
		for(int i = 0; i < emlInstances.length; i++) {
			String instance = emlInstances[i];
			if(omsConfMap.containsKey("EML_"+instance+"_IP")) {
				String emlPassword = omsConfMap.get("EML_"+instance+"_PASSWORD");
				String emlUser = omsConfMap.get("EML_"+instance+"_USER");
				String emlIp = omsConfMap.get("EML_"+instance+"_IP");
				SshConnect sshSession = new SshConnect(emlIp, emlUser, emlPassword);
				sshSession.login();

				String hostName = omsConfMap.get("EML_"+instance+"_HOSTNAME").toUpperCase();
				String emlName = hostName.toUpperCase().indexOf("EML") == -1 ? "OTNE" : "EML";
				String cmd = "cat /usr/Systems/" + emlName + "_" + instance + "/ORACLE/network/admin/tnsnames.ora | grep -v '#'";

				String[] result = sshSession.execShell(cmd);
				sshSession.closeConn();
				List<String> values = sortMapValues(analysisStr(result[0].trim(), "EML" + instance));
				saveMap2File(values, splitFileStr("eml_" + instance));
			}
		}
	}
	
	/**
	 * 解析字符串
	 * @param str
	 * @return 
	 */
	private  Map<String,String> analysisStr(String str,String type){
		Map<String,String> map = new HashMap<String, String>();
		String key1 = "sid_" + type;
		String key2 = "walkey_"+type;

		String[] values = str.split("\r\n\r\n");
		map.put(key1, values[0]);

		String key2Values = "";
		for(int i = 1;i<values.length;i++){
			//BMML_ALIAS, SNML_ALIAS,SNA_ALIAS,ETH_ALIAS,MPLS_ALIAS
			//ANALOG,SDH
			if(values[i].startsWith("WDM")||
			   values[i].startsWith("SNML")||
			   values[i].startsWith("SNA")||
			   values[i].startsWith("ETH")||
			   values[i].startsWith("MPLS")||
			   values[i].startsWith("ANALOG")||
			   values[i].startsWith("SDH")){
				key2Values +=values[i] +"\r\n\r\n";
			}else if(values[i].startsWith("SYSTEM_ALIAS")){
				key2Values += values[i] +"\r\n\r\n";
			}
		}
		map.put(key2, key2Values);
		return map;
	}
	
	/**
	 * 将存有数据的map转换为value值排过序的list，以便写入文件
	 * @param map
	 * @return
	 */
	private  List<String> sortMapValues(Map<String,String> map){
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(map.keySet());
		Collections.sort(keyList);
		
		List<String> values = new ArrayList<String>();
		
		for(String key:keyList){
			values.add(map.get(key));
		}
		return values;
	}
	
	/**
	 * 保存数据到 ../oralWallet/tnsnames.ora
	 * @param list
	 */
	private  void saveMap2File(List<String> list,String fileName){

		File currentDri = new File(".");
		String filename = currentDri.getAbsolutePath() + "/" + fileName;
		new File(filename).getParentFile().mkdirs();
		String content = "";
		for(String str:list){
			content += str +"\r\n\r\n";
		}
		try{
			FileUtil.write(new File(filename), content,true);
		}catch(IoRuntimeException e){
			log.error("write data to file[tnsname.ora] error",e);
			msg +="Failed to write tnsname.ora\r\n";
		}
	}
	
	public String getMsg(){
		return msg;
	}
	
	public static void main(String[] args) {
		SyncOracleTnsNames.init().loadTnsNames();
	}
	
	private String splitFileStr(String str){
		return "wallet/"+str+"/tnsnames.ora";
	}
}
