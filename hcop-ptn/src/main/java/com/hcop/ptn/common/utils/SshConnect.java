package com.hcop.ptn.common.utils;

import ch.ethz.ssh2.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SshConnect {
    private static Log log = LogFactory.getLog(SshConnect.class);
    private Connection conn;
    private String ip;
    private String userName;
    private String password;
    private SCPClient client;
    private static final int TIME_OUT = 1000 * 5 * 60;


    public SshConnect(String ip, String userName, String password) {
        this.ip = ip;
        this.userName = userName;
        this.password = password;
    }


    public boolean login() throws IOException {
        conn = new Connection(ip);
        conn.connect(null, 2000, 0);
        return conn.authenticateWithPassword(userName, password);
    }


    public boolean isLogin(){
        if(conn == null) return false;
        return conn.isAuthenticationComplete();
    }


    public void closeConn(){
        if(conn == null) return;
        conn.close();
    }

    public boolean openConn() throws IOException {
        if(conn == null) return false;
        conn.connect();
        return conn.authenticateWithPassword(userName, password);
    }


    /**
     * 执行脚本
     * 如果要批处理执行shell命令，传入的shell脚本要以;号分隔
     *
     * @param shellComm
     * @return result数组，result[0]是输出信息，result[1]是执行shell的错误信息
     * @throws Exception
     */
    public String[] execShell(String shellComm) throws Exception {
        String [] result = new String[2];
        InputStream stdOut = null;
        InputStream stdErr = null;
        Session session = conn.openSession();
        session.execCommand(shellComm);
        //执行结果
        stdOut = new StreamGobbler(session.getStdout());
        result[0] = processStream(stdOut);
        //错误信息
        stdErr = new StreamGobbler(session.getStderr());
        result[1]= processStream(stdErr);

        session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
        session.close();
        return result;
    }


    private String processStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder result = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        while (true)
        {
            String line = br.readLine();
            if (line == null){
                break;
            }
            result.append(line);
            result.append(lineSeparator);
        }
        return result.toString();
    }

    public void downloadFile(String remoteFile, String localDirectory) throws IOException{
        if(conn==null){
            login();
        }
        if(client==null){
            client = new SCPClient(conn);
        }
        //把远程服务器上的文件取到本地localDirectory下
        client.get(remoteFile,localDirectory);
    }


    public void uploadFile(String localFile, String remoteDirectory)throws Exception{
        try{
            if(conn==null){
                login();
            }
            if(client==null){
                client = new SCPClient(conn);
            }
            client.put(localFile,remoteDirectory); //把本地的文件放在远程服务器上对应位置
        }catch (IOException e) {
            log.error("Can't upload file.", e);
        }
    }


    public void uploadFile(String localFile, String remoteDirectory, String permission)throws Exception{
        try{
            if(conn==null){
                login();
            }
            if(client==null){
                client = new SCPClient(conn);
            }
            if(permission == null || permission.equals("")){
                client.put(localFile,remoteDirectory); //把本地的文件放在远程服务器上对应位置
            } else {
                client.put(localFile,remoteDirectory, permission);
            }
        }catch (IOException e) {
            log.error("Can't upload file.", e);
        }
    }

    public void uploadFile(byte[] data, String remoteFileName, String remoteDirectory, String permission)throws Exception{
        try{
            if(conn==null){
                login();
            }
            if(client==null){
                client = new SCPClient(conn);
            }
            if(permission == null || permission.equals("")){
                client.put(data, remoteFileName, remoteDirectory);
            } else {
                client.put(data, remoteFileName, remoteDirectory, permission);
            }

        }catch (IOException e) {
            log.error("Can't upload file.", e);
        }
    }

    public static void main(String args[]) {
        try{
            SshConnect connect = new SshConnect("135.251.97.143", "root", "******");//初始化
            //登录
            if(connect.login()){
                String [] result = connect.execShell("uname"); //执行shell命令并返回结果
                System.out.println(result[0]);
                connect.closeConn();//关闭连接
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public String getIp() {
        return ip;
    }


    public String getUserName() {
        return userName;
    }


    public String getPassword() {
        return password;
    }

}
