package com.neofoc.app.connection.basicsocket;

public class BServiceServer extends BServer implements BServerListener, BService {

    private BServiceInterface serviceListener = null;
    private boolean exitOnPostReply = false;
    private boolean violentExitOnPostReply = false;

    public BServiceServer(BServiceInterface serviceListener, int port) {
        this(serviceListener, port, null);
    }

    public BServiceServer(BServiceInterface serviceListener, int port, LogInterface logInterface) {
        super(port);
        setLogInterface(logInterface);
        this.serviceListener = serviceListener;
        setServerListener(this);
        startPolling();
    }

    public void dispose() {
        serviceListener = null;
        setServerListener(null);
        super.dispose();
    }

    public String replyToReceivedRequest(String message) {
        String ret = BServiceServer.REPLY_FAILED;

        if (message.equals(BServiceServer.SEND_SWITCH_ON)) {
            try {
                switchOn();
                if (serviceListener.isOn()) {
                    ret = BServiceServer.REPLY_SUCCESS;
                } else {
                    ret = BServiceServer.REPLY_FAILED;
                }
            } catch (Exception e) {
                logException(e);
            }
        } else if (message.equals(BServiceServer.SEND_SWITCH_OFF)) {
            try {
                switchOff();
                if (serviceListener.isOn()) {
                    ret = BServiceServer.REPLY_FAILED;
                } else {
                    ret = BServiceServer.REPLY_SUCCESS;
                }
            } catch (Exception e) {
                logException(e);
            }
        } else if (message.equals(BServiceServer.SEND_EXIT)) {
            try {
                switchOff();
                if (serviceListener.isOn()) {
                    ret = BServiceServer.REPLY_FAILED;
                } else {
                    ret = BServiceServer.REPLY_SUCCESS;
                    exitOnPostReply = true;
                }
            } catch (Exception e) {
                logException(e);
            }
        } else if (message.equals(BServiceServer.SEND_VIOLENT_EXIT)) {
            try {
                violentExitOnPostReply = true;
                switchOff();
                ret = BServiceServer.REPLY_SUCCESS;
            } catch (Exception e) {
                logException(e);
            }
        } else if (message.equals(BServiceServer.SEND_PING)) {
            ret = BServiceServer.REPLY_SUCCESS;
        }

        return ret;
    }

    public void postReply() {
        if (exitOnPostReply) {
            serviceListener.exit();
            exitOnPostReply = false;
        } else if (violentExitOnPostReply) {
            System.exit(0);
        }
    }

    public boolean switchOff() {
        return serviceListener.switchOff();
    }

    public boolean switchOn() {
        return serviceListener.switchOn();
    }

    public boolean exit() {
        return false;
    }

    public boolean ping() {
        //Only for the client
        return false;
    }

    public boolean violentExit() {
        return false;
    }

    public boolean isOn() {
        return serviceListener.isOn();
    }

    public String getName() {
        return serviceListener.getName();
    }

    public boolean launch() {
        //Only for the client
        return false;
    }
}
