package com.tt.bigcount.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ConfigurationException;

public class ServerApp {

    private static final Logger log = LogManager.getLogger(ServerApp.class.getName());

    public static void main(String[] args){
        Server server = null;
        try {
            server = new Server(201, "127.0.0.1", 10011);

        }
        catch(ConfigurationException ex){
            log.error("server configure failed");
            return;
        }
        catch(IllegalArgumentException ex){
            log.error("server argument error");
            return;
        }


        if(server == null){
            log.error("server create failed");
            return;
        }

        try{
            server.start();
        }
        catch(InterruptedException ex){
            log.error("server start failed");
        }
        finally {
            server.stop();
        }
    }
}
