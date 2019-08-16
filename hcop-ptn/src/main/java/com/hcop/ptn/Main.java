package com.hcop.ptn;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import com.hcop.ptn.alarm.AlarmReceiver;
import com.hcop.ptn.api.ConnectionsApiServiceImpl;
import com.hcop.ptn.api.NesApiServiceImpl;
import com.hcop.ptn.api.TpsApiServiceImpl;
import com.hcop.ptn.api.utils.AccessControlFilter;
import com.hcop.ptn.api.utils.SSEAccessControlFilter;
import com.hcop.ptn.api.AlarmsApiServiceImpl;
import com.hcop.ptn.api.utils.ClassThief;
import com.hcop.ptn.common.internal.xos.session.SessionManager;
import com.hcop.ptn.constants.ConfLoader;
import com.hcop.ptn.constants.ConfLoaderException;
import com.hcop.ptn.constants.ConfigKey;
import com.hcop.ptn.servlet.SSEServlet;
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

        loadConf(args);

        confLog();
        log = LogManager.getLogger(Main.class);

        SessionManager.getInstance().init();

        new Thread(()->{
            AlarmReceiver rec = AlarmReceiver.instance();
            rec.initCorba();
            try {
                rec.startup();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();

        registerHcopPtnServerImpl();
        ////////////////////////
        //swagger context
        String[] packages = new String[] {
                "com.hcop.ptn.restful.server.api"};

        ResourceConfig config = new ResourceConfig().packages(packages).register(JacksonFeature.class)
                .register(AccessControlFilter.class);


        ServletHolder swagger_servlet = new ServletHolder(new ServletContainer(config));
        Servlet sv=new SSEServlet();

        ServletHolder SSE_servlet=new ServletHolder(sv);
        ServletContextHandler swaggerContext = new ServletContextHandler();
        swaggerContext.setContextPath("/hcop-ptn/*");
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
                        log.info("hcop-ptn server started@" + port);
                        server.join();
                    } catch (Exception e) {
                        log.error("hcop-ptn server start@" + port + " failed", e);
                        shutDown();
                    }
                } finally {
                    log.error("hcop-ptn server exit@" + port + " failed");
                    server.destroy();
                }

            }
        }).start();

        //Notification simulator
        /*new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        Thread.sleep(2000);
                        for(;;){
                            SSESourceMgr.getInstance().send((new Date().toString()));
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {

                    }
                } finally {

                }

            }
        }).start();*/
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
            System.setProperty("log4j.configurationFile", currentDir.getAbsolutePath() + "/hcop-ptn.log4j2.xml");
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




    private static void registerHcopPtnServerImpl() {
        try {
            ClassThief.setFinalStatic("com.hcop.ptn.restful.server.api.factories.AlarmsApiServiceFactory",
                    "service", new AlarmsApiServiceImpl());

            ClassThief.setFinalStatic("com.hcop.ptn.restful.server.api.factories.ConnectionsApiServiceFactory",
                    "service", new ConnectionsApiServiceImpl());
            ClassThief.setFinalStatic("com.hcop.ptn.restful.server.api.factories.NesApiServiceFactory",
                    "service", new NesApiServiceImpl());
            ClassThief.setFinalStatic("com.hcop.ptn.restful.server.api.factories.TpsApiServiceFactory",
                    "service", new TpsApiServiceImpl());
        } catch (Exception e) {
            log.error("registerHcopPtnServerImpl", e);
            shutDown();
        }
    }


}
