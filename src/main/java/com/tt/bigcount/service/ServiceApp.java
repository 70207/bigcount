package com.tt.bigcount.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ConfigurationException;

public class ServiceApp {

    private static final Logger log = LogManager.getLogger(ServiceApp.class.getName());

    public static void main(String[] args){
        Service service = null;
        try {
            service = new Service(301, "127.0.0.1", 10201);

        }
        catch(ConfigurationException ex){
            log.error("server configure failed");
            return;
        }
        catch(IllegalArgumentException ex){
            log.error("server argument error");
            return;
        }


        if(service == null){
            log.error("server create failed");
            return;
        }

        try{
            service.start();
        }
        catch(InterruptedException ex){
            log.error("server start failed");
        }
        finally {
            service.stop();
        }
    }
}
