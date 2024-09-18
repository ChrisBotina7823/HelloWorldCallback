
public class Server {
    public static void main(String[] args) {
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg")) {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Service");
            com.zeroc.Ice.Object object = new ChatI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimpleChat"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }
}