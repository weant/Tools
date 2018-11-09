package com.otn.tool.common.sftp;

import java.util.List;

/**
 *FTP文件上传下载接口定义
 *@author jluo018 
 */
public interface FtpFile {
    
    /**
     *上传文件到ftp服务器
     *@param hostname 主机地址
     *@param port 主机端口号
     *@param username ftp连接用户名
     *@param password ftp连接密码
     *@param remotePath 远程文件路径
     *@param fileNames 待上传的文件
     *@return 是否上传成功
     */
    public boolean uploadFile(String hostName, int port, String userName, String password, String remotePath, List<String> localFiles);
    
    /**
     *从ftp服务器下载文件
     *@param hostname 主机地址
     *@param port 主机端口号
     *@param username ftp连接用户名
     *@param password ftp连接密码
     *@param remoteFileName 待下载的远程文件或者文件夹
     *@param localFolderPath 本地保存目录
     *@return 是否下载成功
     */
    public boolean downloadFile(String hostName, int port, String userName, String password, String remoteFileName, String localFolderPath);

}
