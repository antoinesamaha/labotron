package com.neofoc.app.connection.basicsocket;

public class BServiceClient extends BClient implements BService {

    private BServiceInterface serviceInterface = null;
    private String launcherIP = null;
    private int launcherPort = 0;

    public BServiceClient(BServiceInterface serviceInterface, String serverIP, int serverPort, String launcherServerIP, int launcherServerPort) {
        super(serverIP, serverPort);
        this.serviceInterface = serviceInterface;
        this.launcherIP = launcherServerIP;
        this.launcherPort = launcherServerPort;
    }

    public void dispose() {
        launcherIP = null;
        serviceInterface = null;
        super.dispose();
    }

    public boolean exit() {
        boolean error = true;
        String ret = sendMessage(BService.SEND_EXIT);
        if (ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0) {
            error = false;
        }
        serviceInterface.refreshSwitchStatus();
        return error;
    }

    public boolean ping() {
        boolean error = true;
        String ret = sendMessage(BService.SEND_PING);
        if (ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0) {
            error = false;
        }
        return error;
    }

    public boolean switchOff() {
        boolean error = true;
        String ret = sendMessage(BService.SEND_SWITCH_OFF);
        if (ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0) {
            error = false;
        }
        serviceInterface.refreshSwitchStatus();
        return error;
    }

    public boolean switchOn() {
        boolean error = true;
        String ret = sendMessage(BService.SEND_SWITCH_ON);
        if (ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0) {
            error = false;
        }
        serviceInterface.refreshSwitchStatus();
        return error;
    }

    public boolean violentExit() {
        boolean error = true;
        String ret = sendMessage(BService.SEND_VIOLENT_EXIT);
        if (ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0) {
            error = false;
        }
        serviceInterface.refreshSwitchStatus();
        return error;
    }

    public boolean isOn() {
        serviceInterface.refreshSwitchStatus();
        return serviceInterface.isOn();
    }

    public String getName() {
        return serviceInterface.getName();
    }

    public boolean launch() {
        boolean error = false;
        BClient client = new BClient(launcherIP, launcherPort);
        String response = client.sendMessage(serviceInterface.getLaunchCommand());
        error = response.compareTo(BService.REPLY_SUCCESS) != 0;
        return error;
    }
}
