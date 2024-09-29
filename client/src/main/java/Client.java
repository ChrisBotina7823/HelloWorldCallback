import java.util.Scanner;

import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class Client {
    public static void main(String[] args) {
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg")) {

            Demo.ChatPrx chatManagerPrx = Demo.ChatPrx
                    .checkedCast(communicator.propertyToProxy("Chat.Proxy"));

            try {
                ObjectAdapter adapter = communicator.createObjectAdapter("Callback");
                Demo.Callback callback = new CallbackI();

                ObjectPrx prx = adapter.add(callback, Util.stringToIdentity("callback"));
                Demo.CallbackPrx callbackPrx = Demo.CallbackPrx.checkedCast(prx);
                adapter.activate();

                Scanner sc = new Scanner(System.in);
                System.out.println("Enter your username: ");
                String username = sc.nextLine();
                chatManagerPrx.registerUser(username, callbackPrx);

                /*
                 * System.out.println("Waiting for response...");
                 * long start = System.currentTimeMillis();
                 * chatManagerPrx.printString("Hello World");
                 * chatManagerPrx.fact(20, callbackPrx);
                 * // System.out.println("Factorial of 10 is: " );
                 * System.out.println("Time taken: " + (System.currentTimeMillis() - start) +
                 * "ms");
                 *
                 */

                int option = -1;
                while (option != 0) {
                    System.out.println("Welcome: ");
                    System.out.println(("0. Exit"));
                    System.out.println(("1. List clients"));
                    System.out.println(("2. Send message"));
                    System.out.println(("3. Broadcast message"));
                    option = sc.nextInt();
                    switch (option) {
                        case 1:
                            chatManagerPrx.listClients(username);
                            break;
                        case 2:
                            System.out.println("Enter destination user: ");
                            String destUser = sc.next();
                            System.out.println("Enter message: ");
                            sc.nextLine();
                            String message = sc.nextLine();
                            chatManagerPrx.sendMessage(message, username, destUser);
                            break;
                        case 3:
                            System.out.println("Enter message: ");
                            sc.nextLine();
                            String broadcastMessage = sc.nextLine();
                            chatManagerPrx.broadCastMessage(broadcastMessage, username);
                            break;
                        default:
                            break;
                    }
                }
                communicator.waitForShutdown();
                sc.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
