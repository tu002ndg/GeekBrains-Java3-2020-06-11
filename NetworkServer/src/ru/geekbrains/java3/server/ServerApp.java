package ru.geekbrains.java3.server;



public class ServerApp {
    private static final int DEFAULT_PORT = 8189;
//    static final Logger rootLogger = LogManager.getRootLogger();



    public static void main(String[] args) {

        int port = getServerPort(args);
        NetworkServer server = new NetworkServer(port);
//        serverLogger.debug("Debug");
//        serverLogger.info("Info");
//        serverLogger.warn("Warn");
//        serverLogger.error("Error");
//        serverLogger.fatal("Fatal");
//        serverLogger.info("Hello, world!");


//        serverLogger.info(server.sendMessage());

//        rootLogger.info("Root logger: "+server.sendMessage());


        server.start();

    }

    private static int getServerPort(String[] args) {
        int port = DEFAULT_PORT;
        if(args.length==1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.printf(
                        "Некорректный формат порта,будет использован порт по умолчанию [%s]%n",
                        port);
            }
        }
        return port;
    }
}
