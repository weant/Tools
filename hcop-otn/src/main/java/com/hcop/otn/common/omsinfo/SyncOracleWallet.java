package com.hcop.otn.common.omsinfo;

import com.hcop.otn.common.properties.OmsConf;
import com.hcop.otn.common.sftp.FtpFile;
import com.hcop.otn.common.sftp.SFtpFileImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.hcop.otn.constants.ConfigKey.CONFIGURATION_PATH;


public class SyncOracleWallet {
    private static Log log = LogFactory.getLog(SyncOracleWallet.class);
    
    private final String localFolderPath = "/wallet";
    private final int port = 22;
    private static SyncOracleWallet cwMgr;
    
    private static final String OTN = "otn";
    private static final String EML = "eml";
    private String otnIp = "";
    
    public static SyncOracleWallet instance(){
        if(cwMgr == null){
            cwMgr = new SyncOracleWallet();
        }
        return cwMgr;
    }

    private SyncOracleWallet(){
        
    }
    
    public String downloadWalletByOms(){
        Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
        
        ExecutorService exec = Executors.newFixedThreadPool(3);// 3个线程大小的线程池
        List<Future<Map<String, Boolean>>> futures = new ArrayList<Future<Map<String,Boolean>>>();

        //get otn cwallet
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

            String ip = omsConfMap.get(name+"_"+instance+"_IP");
            String userName = omsConfMap.get(name+"_"+instance+"_USER");
            String password = omsConfMap.get(name+"_"+instance+"_PASSWORD");
            int version = Integer.parseInt(omsConfMap.get("OMS_VERSION"));

            this.otnIp = ip;

            DownLoadTask otnTask = new DownLoadTask(ip, userName, password, OTN, Integer.parseInt(instance), version, ip);
            futures.add(exec.submit(otnTask));
        }

        //get eml cwallet
        String[] emlInstances = omsConfMap.get("EML_INSTANCE").split(",");
        for(int i = 0; i < emlInstances.length; i++) {
            String instance = emlInstances[i];
            if(omsConfMap.containsKey("EML_"+instance+"_IP")){
                String ip = omsConfMap.get("EML_"+instance+"_IP");
                String userName = omsConfMap.get("EML_"+instance+"_USER");
                String password = omsConfMap.get("EML_"+instance+"_PASSWORD");
                int version = Integer.parseInt(omsConfMap.get("OMS_VERSION"));

                DownLoadTask emlTask = new DownLoadTask(ip, userName, password, EML, Integer.parseInt(instance), version, this.otnIp);
                futures.add(exec.submit(emlTask));
            }
        }

        Map<String, Boolean> result = new HashMap<>();
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
        	msgBuffer.append("save wallet file error.\r\n");
        	return msgBuffer.toString();
        }
        
        return "";
    }
    
    class DownLoadTask implements Callable<Map<String, Boolean>>{
    	
    	private String host;
		private String userName;
		private String password;
		private String dbName;
        private String otnIp;
        private int inst;
        private int version;

		public DownLoadTask(String host, String userName, String password, String dbName, int inst, int version, String otnIp){
    		this.host = host;
    		this.userName = userName;
    		this.password = password;
    		this.dbName = dbName;
    		this.inst = inst;
    		this.version = version;
    		this.otnIp = otnIp;
    	}

		@Override
		public Map<String, Boolean> call() throws Exception {
			boolean success = downloadWallet(host, port, userName, password, dbName, inst, version, otnIp);
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

    public boolean downloadWallet(String host, int port, String userName, String password, String dbName, int inst, int version, String otnIp){
        log.info("begin to download " + dbName + " wallet!");
        String realDbName = dbName;
        switch(dbName) {
            case EML:
                if(host.equals(otnIp)) {
                    realDbName = "OTNE";
                }
                break;
            case OTN:
                if(version >= 14) {
                    realDbName = "OTNE";
                }
                break;
            default:
        }
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
        File currentDir = new File(System.getProperty(CONFIGURATION_PATH) == null ? System.getenv(CONFIGURATION_PATH) : System.getProperty(CONFIGURATION_PATH));
    	String folderPath = currentDir.getAbsolutePath() + localFolderPath + "/" + dbName.toLowerCase() + "_" + inst;
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

    public static void main(String[] args) {
        SyncOracleWallet.instance().downloadWalletByOms();
    }

}
