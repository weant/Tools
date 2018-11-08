package com.otn.tool.common.utils.sftp;

/**
 *自定义的ftp运行时异常 
 */
public class FtpRuntimeException extends RuntimeException {
   /**    
     * serialVersionUID:TODO（用一句话描述这个变量表示什么）    
     *    
     * @since Ver 1.1    
     */    
    
    private static final long serialVersionUID = -558462933920497529L;

public FtpRuntimeException() {
       super();
   }
   
   public FtpRuntimeException(String message) {
       super(message);
   }
   
   public FtpRuntimeException(String message, Throwable cause) {
       super(message, cause);
   }
   
   public FtpRuntimeException(Throwable cause) {
       super(cause);
   }

}
