package pers.zhixilang.lego.rpc.common;

/**
 *
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 13:54
 */
public class Response {
    private String requestID;

    private Integer code;

    private String msg;

    private Object data;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
