package com.weibo.datasys.common.exception;
/** 
 * 多数情况下，创建自定义异常需要继承Exception，本例继承Exception的子类RuntimeException 
 * @author Mahc 
 * 
 */  
public class ContentBlankException extends RuntimeException {  
  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String retCd ;  //异常对应的返回码  
    private String msgDes;  //异常对应的描述信息  
      
    public ContentBlankException() {  
        super();  
    }  
  
    public ContentBlankException (String message) {  
        super(message);  
        msgDes = message;  
    }  
  
    public ContentBlankException(String retCd, String msgDes) {  
        super();  
        this.retCd = retCd;  
        this.msgDes = msgDes;  
    }  
  
    public String getRetCd() {  
        return retCd;  
    }  
  
    public String getMsgDes() {  
        return msgDes;  
    }  
}  