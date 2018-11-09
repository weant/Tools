package com.otn.tool.common.sftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 通过sftp方式实现文件上传下载
 */
public class SFtpFileClient {
    private static Log log = LogFactory.getLog(SFtpFileClient.class);

    public ChannelSftp connect(String hostName, int port, String userName, String password) {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(userName, hostName, port);
            Session sshSession = jsch.getSession(userName, hostName, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshConfig.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            log.info("Sftp session connect to host:" + hostName);
        } catch (Exception e) {
            log.error(e);
        }
        return sftp;
    }

    public boolean uploadFile(String hostName, int port, String userName, String password, String remotePath,
            List<String> localFiles) {
        ChannelSftp sftp = null;
        boolean flag = false;
        try {
            sftp = connect(hostName, port, userName, password);
            for (String fileName : localFiles) {
                File file = new File(fileName);
                if (!file.exists()) {
                    throw new Exception("The file for upload does not exist and the file is:" + fileName);
                }
                if (!file.isFile()) {
                    throw new Exception("The file for upload is a dictionary and the file is:" + fileName);
                }
                sftp.cd(remotePath);
                remotePath = PathToolKit.formatPath4FTP(remotePath);
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    sftp.put(in, file.getName());
                } finally {
                    in.close();
                }
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                log.error(e);
            }
            if (sftp != null) {
                try {
                    sftp.disconnect();
                } catch (Exception e) {
                    sftp = null;
                }
            }
        }
        return flag;
    }

    public boolean downloadFile(String hostName, int port, String userName, String password, String remoteFileName,
            String localFolderPath) {
        ChannelSftp sftp = null;
        boolean flag = false;
        try {
            sftp = connect(hostName, port, userName, password);
            FileOutputStream out = null;
            String remoteFolder = null;
            if (!isDictionary(sftp, remoteFileName)) {
                remoteFolder = PathToolKit.genParentPath4FTP(remoteFileName);
                sftp.cd(remoteFolder);
                File tempFolder = new File(localFolderPath);
                if(!tempFolder.exists()) {
                	tempFolder.mkdirs();
                }
                File file = new File(localFolderPath + File.separator + new File(remoteFileName).getName());
                out = new FileOutputStream(file);
                sftp.get(new File(remoteFileName).getName(), out);
                out.close();
                flag = true;
            } else {
                throw new Exception("The file download" + remoteFileName + "does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                log.error(e);
            }
            if (sftp != null) {
                try {
                    sftp.disconnect();
                } catch (Exception e) {
                    sftp = null;
                }
            }
        }
        return flag;
    }

    private boolean isDictionary(ChannelSftp sftp, String remotePath) {
        remotePath = PathToolKit.formatPath4FTP(remotePath);
        try {
            sftp.cd(remotePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SFtpFileClient ftpFile = new SFtpFileClient();
        List<String> files = new ArrayList<String>();
        files.add("D:\\config1h.txt");
        try {
            ftpFile.downloadFile("135.251.97.181", 22, "alcatel", "alcatel", "/home/alcatel/evc.csv", "D:\\");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
