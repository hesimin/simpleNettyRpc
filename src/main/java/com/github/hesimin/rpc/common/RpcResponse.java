package com.github.hesimin.rpc.common;

import java.io.Serializable;

/**
 * @author hesimin 2017-11-12
 */
public class RpcResponse implements Serializable {
    private String    messageId;
    private boolean   success;
    private Object    reponse;
    private Throwable error;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getReponse() {
        return reponse;
    }

    public void setReponse(Object reponse) {
        this.reponse = reponse;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
