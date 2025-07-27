package com.neofoc.app.connection.basicsocket;

public interface BServerListener {
    public String replyToReceivedRequest(String request);

    public void postReply();
}
