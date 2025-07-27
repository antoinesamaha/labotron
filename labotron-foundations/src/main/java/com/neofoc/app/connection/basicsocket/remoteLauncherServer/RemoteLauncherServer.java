package com.neofoc.app.connection.basicsocket.remoteLauncherServer;

import com.neofoc.app.connection.basicsocket.BServer;
import com.neofoc.app.connection.basicsocket.BServerListener;
import com.neofoc.app.connection.basicsocket.BService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RemoteLauncherServer extends BServer implements BServerListener {

    public static final String FINISH = "FINISH LAUNCH";

    public RemoteLauncherServer(int port) {
        super(port);
        setServerListener(this);
    }

    public void postReply() {
        // TODO Auto-generated method stub
    }

    public String replyToReceivedRequest(String request) {
        String reply = BService.REPLY_FAILED;
        try {
            Process process = Runtime.getRuntime().exec(request);

            if (process != null) {
                InputStream stream = process.getInputStream();
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(stream));
                String line = null;
                boolean exit = false;
                do {
                    line = bufferReader.readLine();
                    if (line != null) {
                        System.out.println(line);
                    }
                    exit = line == null || line.endsWith(FINISH);
                } while (!exit);
            }

            reply = BService.REPLY_SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            reply = BService.REPLY_FAILED;
        }
        return reply;
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                int port = Integer.valueOf(args[0]);
                RemoteLauncherServer server = new RemoteLauncherServer(port);
                server.startPolling();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
