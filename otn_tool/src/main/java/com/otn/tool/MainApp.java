package com.otn.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class MainApp {
    private static Logger log;
    private static int port = 8102;

    static public void main(String[] args) {
        initLogger();
        initPort(args);
        initServer();
    }

    private static void initLogger() {
        File confFile = new File("./conf/log4j.xml");
        String filePath = confFile.getAbsolutePath();
        System.setProperty("log4j.configurationFile", filePath);
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.reconfigure();
        log = LogManager.getLogger(MainApp.class);
    }

    private static void initPort(String[] args) {
        if (args.length == 2 && args[0].equals("--port")) {
            port = Integer.valueOf(args[1]);
        }
        log.debug("Server Port = " + port);
    }

    private static void initServer() {
        Server server = new Server(port);

        WebAppContext webapp = new WebAppContext();
        webapp.setDefaultsDescriptor("WEB-INF/web.xml");
        webapp.setContextPath("/");
        webapp.setResourceBase("./");
        server.setHandler(webapp);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.error("start server error", e);
        }
    }
}
