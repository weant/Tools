package com.hcop.otn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import com.hcop.otn.api.*;
import com.hcop.otn.api.utils.AccessControlFilter;
import com.hcop.otn.api.utils.SSEAccessControlFilter;
import com.hcop.otn.api.utils.ClassThief;
import com.hcop.otn.common.internal.xos.session.SessionManager;
import com.hcop.otn.constants.ConfLoader;
import com.hcop.otn.constants.ConfLoaderException;
import com.hcop.otn.constants.ConfigKey;
import com.hcop.otn.servlet.SSEServlet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;

/**
 */
public class Main {
    private static Logger log;
    public static void main(String[] args) throws Exception{
        //initLogger();
        loadConf(args);

        confLog();
        log = LogManager.getLogger(Main.class);

        SessionManager.getInstance().init();

        registerHcopOtnServerImpl();
        ////////////////////////
        //swagger context
        String[] packages = new String[] {
                "com.hcop.otn.restful.server.api"};

        ResourceConfig config = new ResourceConfig().packages(packages).register(JacksonFeature.class)
                .register(AccessControlFilter.class);


        ServletHolder swagger_servlet = new ServletHolder(new ServletContainer(config));
        Servlet sv=new SSEServlet();

        ServletHolder SSE_servlet=new ServletHolder(sv);
        ServletContextHandler swaggerContext = new ServletContextHandler();
        swaggerContext.setContextPath("/hcop-otn/*");
        swaggerContext.addServlet(swagger_servlet, "/api/*");
        swaggerContext.addServlet(SSE_servlet, "/notif");

        swaggerContext.addFilter(SSEAccessControlFilter.class,"/notif", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));


        ////////////////////////
        //webapp context
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setDescriptor("webapp" + "/WEB-INF/web.xml");

        URL webAppDir = Thread.currentThread().getContextClassLoader().getResource("webapp");
        if (webAppDir == null) {
            throw new RuntimeException(String.format("No %s directory was found into the JAR file", "webapp"));
        }
        try {
            webAppContext.setResourceBase(webAppDir.toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {swaggerContext, webAppContext});

        ////////////////////////

        final int port = ConfLoader.getInstance().getInt(ConfigKey.server_port);
        final Server server = new Server(port);


        server.setHandler(handlers);




        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        server.start();
                        log.info("hcop-otn server started@" + port);
                        server.join();
                    } catch (Exception e) {
                        log.error("hcop-otn server start@" + port + " failed", e);
                        shutDown();
                    }
                } finally {
                    log.error("hcop-otn server exit@" + port + " failed");
                    server.destroy();
                }

            }
        }).start();
    }

    private static void confLog() throws Exception{
        String log_conf_file=ConfLoader.getInstance().getConf(ConfigKey.log_conf_file, null);
        if((new File(log_conf_file).canRead())){
            System.setProperty("log4j.configurationFile", log_conf_file);
        }else{
            /*LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
            try {
                loggerContext.getConfiguration().getRootLogger().setLevel(ConfLoader.getInstance().getBoolean(ConfigKey.debug) ? Level.DEBUG : Level.INFO);
            } catch (ConfLoaderException e) {
                loggerContext.getConfiguration().getRootLogger().setLevel(Level.DEBUG);
            }*/

            String confPath = System.getProperty(ConfigKey.CONFIGURATION_PATH);
            if (confPath == null) {
                confPath = System.getenv(ConfigKey.CONFIGURATION_PATH);
            }
            File currentDir = new File(confPath);
            System.setProperty("log4j.configurationFile", currentDir.getAbsolutePath() + "/hcop-otn.log4j2.xml");
        }

        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.reconfigure();
    }

    private static void loadConf(String[] args) {
        parseCommandLine(args);
        if(!ConfLoader.getInstance().getConf(ConfigKey.conf_file, "").isEmpty()){
            try {
                ConfLoader.getInstance().loadConf(ConfLoader.getInstance().getConf(ConfigKey.conf_file, ""));
            } catch (ConfLoaderException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parseCommandLine(String[] _args){

        class Args{
            @Parameter(names={"-"+ConfigKey.server_port}, order = 0, description="Http server port")
            private int server_port=ConfigKey.default_server_port;

            @Parameter(names={"-"+ConfigKey.conf_file}, order = 1, description = "Configuation file")
            private String conf_file="";

            @Parameter(names={"-"+ConfigKey.log_conf_file}, order = 2, description ="Log4j2 configuration file")
            private String log_conf_file="";

            @Parameter(names={"-"+ConfigKey.debug}, order = 3, description = "Debug mode (only in case log_conf_file is not available)")
            private boolean debug=false;

            @Parameter(names={"-help"}, order = 4, help=true, description = "Show this help")
            private boolean help=false;
        }
        Args args=new Args();
        JCommander jc=new JCommander(args, _args);
        if(args.help){
            jc.usage();
            System.exit(0);
        }

        ConfLoader.getInstance().setInt(ConfigKey.server_port, args.server_port);
        ConfLoader.getInstance().setConf(ConfigKey.conf_file, args.conf_file);
        ConfLoader.getInstance().setConf(ConfigKey.log_conf_file, args.log_conf_file);
        ConfLoader.getInstance().setBoolean(ConfigKey.debug, args.debug);

    }
    private static void shutDown() {
        log.info("Shutdown.");
        System.exit(1);
    }

    private static void initLogger() {
        String confPath = System.getProperty(ConfigKey.CONFIGURATION_PATH);
        if (confPath == null) {
            confPath = System.getenv(ConfigKey.CONFIGURATION_PATH);
        }
        File currentDir = new File(confPath);
        System.setProperty("log4j.configurationFile", currentDir.getAbsolutePath() + "/log4j2.xml");
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.reconfigure();
        log = LogManager.getLogger(Main.class);
    }


    private static void registerHcopOtnServerImpl() {
        try {
            ClassThief.setFinalStatic("com.hcop.otn.restful.server.api.factories.NesApiServiceFactory",
                    "service", new NesApiServiceImpl());
            ClassThief.setFinalStatic("com.hcop.otn.restful.server.api.factories.EquipmentsApiServiceFactory",
                    "service", new EquipmentsApiServiceImpl());
            ClassThief.setFinalStatic("com.hcop.otn.restful.server.api.factories.TpsApiServiceFactory",
                    "service", new TpsApiServiceImpl());
            ClassThief.setFinalStatic("com.hcop.otn.restful.server.api.factories.ConnectionsApiServiceFactory",
                    "service", new ConnectionsApiServiceImpl());
            ClassThief.setFinalStatic("com.hcop.otn.restful.server.api.factories.OpApiServiceFactory",
                    "service", new OpApiServiceImpl());
        } catch (Exception e) {
            log.error("registerHcopOtnServerImpl", e);
            shutDown();
        }
    }


}
