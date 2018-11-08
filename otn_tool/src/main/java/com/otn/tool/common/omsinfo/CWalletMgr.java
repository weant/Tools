package com.otn.tool.common.omsinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.otn.tool.common.properties.OmsConf;
import com.otn.tool.common.utils.sftp.FtpFile;
import com.otn.tool.common.utils.sftp.SFtpFileImpl;
import com.sun.imageio.plugins.common.I18N;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CWalletMgr {
    private static Log log = LogFactory.getLog(CWalletMgr.class);
    
    private final String localFolderPath = "/wallet";
    private final int port = 22;
    private static CWalletMgr cwMgr;
    
    private static final String OTN = "otn";
    private static final String EML = "eml";
    
    public static CWalletMgr instance(){
        if(cwMgr == null){
            cwMgr = new CWalletMgr();
        }
        return cwMgr;
    }

    private CWalletMgr(){
        
    }
    
    public String downloadWalletByNms(){
        NMSConfMgr nms = NMSConfMgr.instance();

        Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
        
        ExecutorService exec = Executors.newFixedThreadPool(3);// 3个线程大小的线程池
        List<Future<Map<String, Boolean>>> futures = new ArrayList<Future<Map<String,Boolean>>>();
        
        //get eml cwallet
        Set<Entry<Integer, Map<String, String>>> emlEntrySet = nms.getCurrentEMLConf();
        for(Entry<Integer, Map<String, String>> entry : emlEntrySet){
        	int inst = entry.getKey();
            Map<String, String> value = entry.getValue();
            
            String userName = value.get("eml_user");
            String password = value.get("eml_password");
            String ip = value.get("ip");
            //[Feb 28, 2017][Huang Yang] - Start
            int version = Integer.parseInt(value.get("nms_digital_version"));
            String hostName = value.get("host");
            DownLoadTask emlTask = new DownLoadTask(ip, userName, password, EML, inst, version, hostName);
            //DownLoadTask emlTask = new DownLoadTask(ip, userName, password, EML, inst);
            //[Feb 28, 2017][Huang Yang] - End
            futures.add(exec.submit(emlTask));
        }
        
        //get otn cwallet
        Entry<Integer, Map<String, String>> otnEntrySet = nms.getCurrentSDHNPRConf();
        int otnInst = otnEntrySet.getKey();
        Map<String, String> otnValueMap = otnEntrySet.getValue();
        String otnIp = otnValueMap.get("ip");
        String otnName = otnValueMap.get("otn_user");
        String otnPassword = otnValueMap.get("otn_password");
        //[Feb 28, 2017][Huang Yang] - Start
        int version = Integer.parseInt(otnValueMap.get("nms_digital_version"));
        String hostName = otnValueMap.get("host");
        DownLoadTask otnTask = new DownLoadTask(otnIp, otnName, otnPassword, OTN, otnInst, version, hostName);
        //DownLoadTask otnTask = new DownLoadTask(otnIp, otnName, otnPassword, OTN, otnInst);
        //[Feb 28, 2017][Huang Yang] - End
        
        futures.add(exec.submit(otnTask));
        
        
        Map<String, Boolean> result = new HashMap<String,Boolean>();
        for(Future<Map<String, Boolean>> future : futures){
        	try {
        		result.putAll(future.get());
			} catch (InterruptedException e) {
				log.error("download cwallet task error!", e);
			} catch (ExecutionException e) {
				log.error("download cwallet task error!", e);
			}
        }
        exec.shutdown();
        
        StringBuffer msgBuffer = new StringBuffer();
        for(String dbName : result.keySet()){
        	boolean success = result.get(dbName);
        	
        	if(!success){
        		msgBuffer.append(dbName).append(",");
        	}
        }
        if(msgBuffer.length() > 0){
        	msgBuffer.deleteCharAt(msgBuffer.length() - 1);
        	msgBuffer.append(I18N.instance().getString("comm_msg_err_writeWallet") + "\r\n");
        	return msgBuffer.toString();
        }
        
        return "";
    }
    
    class DownLoadTask implements Callable<Map<String, Boolean>>{
    	
    	private String host;
		private String userName;
		private String password;
		private String dbName;
		private int inst;
		//[Feb 28, 2017][Huang Yang] - Start
		private int version;
		private String hostName;
		
		public DownLoadTask(String host, String userName, String password, String dbName, int inst, int version, String hostName){
    		this.host = host;
    		this.userName = userName;
    		this.password = password;
    		this.dbName = dbName;
    		this.inst = inst;
    		this.version = version;
    		this.hostName = hostName;
    	}
		//[Feb 28, 2017][Huang Yang] - End

		public DownLoadTask(String host, String userName, String password, String dbName, int inst){
    		this.host = host;
    		this.userName = userName;
    		this.password = password;
    		this.dbName = dbName;
    		this.inst = inst;
    	}

		@Override
		public Map<String, Boolean> call() throws Exception {
			//[Feb 28, 2017][Huang Yang] - Start
			//boolean success = downloadWallet(host, port, userName, password, dbName, inst);
			boolean success = downloadWallet(host, port, userName, password, dbName, inst, version, hostName);
			//[Feb 28, 2017][Huang Yang] - End
			Map<String, Boolean> result = new HashMap<String, Boolean>();
			result.put(dbName.toUpperCase() + "_" + inst, success);
			return result;
		}
    	
    }
    
    public boolean downloadWallet(String host, int port, String userName, String password, String dbName, int inst){
        log.info("begin to download " + dbName + " wallet!");
        String filePath = getRemoteWalletPath(dbName, inst);
        FtpFile ftpFile = new SFtpFileImpl();
        String storePath = getLocalFolderPath(dbName, inst);
        log.info("file store path : " + storePath);
        boolean result = false;
        try {
        	result = ftpFile.downloadFile(host, port, userName, password, filePath, storePath);
		} catch (Exception e) {
			log.error("download " + filePath + " failed! host: " + host + ", port: " + port
					+ ", username: " + userName + ", password: " + password, e);
			return result;
		}
        log.info("download " + dbName + " wallet finished! result: " + result);
        return result;
    }

    public boolean downloadWallet(String host, int port, String userName, String password, String dbName, int inst, int version, String hostName){
        log.info("begin to download " + dbName + " wallet!");
        String realDbName = NmsVersionUtil.getNameInRemoteHost(version, hostName, dbName).toLowerCase();
        String filePath = getRemoteWalletPath(realDbName, inst);
        FtpFile ftpFile = new SFtpFileImpl();
        String storePath = getLocalFolderPath(dbName, inst);
        log.info("file store path : " + storePath);
        boolean result = false;
        try {
        	result = ftpFile.downloadFile(host, port, userName, password, filePath, storePath);
		} catch (Exception e) {
			log.error("download " + filePath + " failed! host: " + host + ", port: " + port
					+ ", username: " + userName + ", password: " + password, e);
			return result;
		}
        log.info("download " + dbName + " wallet finished! result: " + result);
        return result;
    }
    
    private String getLocalFolderPath(String dbName, int inst){
    	String systemPath = System.getenv("ALLUSERSPROFILE");
    	String folderPath = systemPath + "/" + localFolderPath + "/" + dbName.toLowerCase() + "_" + inst;
		File localFolder = new File(folderPath);
        if(!localFolder.exists()){
            localFolder.mkdirs();
        }
        return folderPath;
    }

    private String getRemoteWalletPath(String dbName, int inst) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("/usr/Systems/");
        buffer.append(dbName.toUpperCase() + "_" + inst);
        buffer.append("/ORACLE/network/admin/");
        buffer.append("cwallet.sso");
        return buffer.toString();
    }
}
