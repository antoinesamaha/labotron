package com.neofoc.app.connection;

import javax.comm.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public interface SerialPortInterface {
    void setParametersFromProperties(Properties props) throws Exception;

    void dispose();

    void openConnection() throws Exception;

    void closeConnection();

    boolean isSerialPortNull();

    void addEventListener(SerialPortEventListener serialPortListener) throws Exception;

    void removeEventListener();

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;
}
