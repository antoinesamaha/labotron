package com.neofoc.app.connection.basicsocket;

import java.util.ArrayList;

public abstract class ServiceRefresher {

    protected abstract int getServiceCount();

    protected abstract BServiceInterface getServiceAt(int i);

    private ArrayList<SingleRefreshThread> refreshThreadList = null;

    public ServiceRefresher() {
    }

    public void dispose() {
        if (refreshThreadList != null) {
            for (int i = 0; i < refreshThreadList.size(); i++) {
                SingleRefreshThread thread = (SingleRefreshThread) refreshThreadList.get(i);
                if (thread != null) {
                    thread.dispose();
                }
            }
            refreshThreadList.clear();
            refreshThreadList = null;
        }
    }

    public void refresh() {
        if (refreshThreadList == null) {
            refreshThreadList = new ArrayList<SingleRefreshThread>();
            for (int i = 0; i < getServiceCount(); i++) {
                SingleRefreshThread srt = new SingleRefreshThread(getServiceAt(i));
                refreshThreadList.add(srt);
            }
        }
        for (int i = 0; i < refreshThreadList.size(); i++) {
            SingleRefreshThread singleRefreshthread = (SingleRefreshThread) refreshThreadList.get(i);
            if (singleRefreshthread != null && !singleRefreshthread.isRefreshing()) {
                Thread thread = new Thread(singleRefreshthread);
                thread.start();
            }
        }
    }

    public class SingleRefreshThread implements Runnable {
        private boolean refreshing = false;
        private BServiceInterface serviceInterface = null;

        public SingleRefreshThread(BServiceInterface serviceInterface) {
            this.serviceInterface = serviceInterface;
        }

        public void dispose() {
            serviceInterface = null;
        }

        public boolean isRefreshing() {
            return refreshing;
        }

        public void run() {
            refreshing = true;
            serviceInterface.refreshLaunchStatus();
            serviceInterface.refreshSwitchStatus();
            refreshing = false;
        }
    }
}
