import java.util.Scanner;

import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class Client {
    public static Scanner sc = new Scanner(System.in);
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
                
                // Register user
                System.out.print("(System) Enter your username: ");
                String username = sc.nextLine();

                // Check if username is already taken
                while (!chatManagerPrx.registerUser(username, callbackPrx)) {
                    System.out.println("(System) Username already taken. Please enter a different username.");
                    System.out.print("(System) Enter your username: ");
                    username = sc.nextLine();
                }

                // Welcome message
                System.out.println("(System) Welcome " + username + "!" +
                        " Type '/list clients' to list all clients, " +
                        "'/exit' to exit, " +
                        "'to X: message' to send a message to user X, " +
                        "and 'BC: message' to broadcast a message." +
                        "\n---------------CHAT---------------");

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

                /* Old menu 
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
                */
                // Obtaining messages
                while (true) {
                    String input = sc.nextLine();
                    if (input.charAt(0) == '/') {
                        if (input.equals("/list clients")) {
                            chatManagerPrx.listClients(username);
                        } else if (input.equals("/exit")) {
                            communicator.waitForShutdown();
                            break;
                        } else {
                            System.out.println("(System) Error: Invalid command.");
                        }
                    } else if (input.contains(":") && input.charAt(0) == 't' && input.charAt(1) == 'o') {
                        String[] parts = input.split(":");
                        String destUser = parts[0].trim().substring(3);
                        String message = parts[1].trim();
                        chatManagerPrx.sendMessage(message, username, destUser);
                    } else if (input.charAt(0) == 'B' && input.charAt(1) == 'C' && input.charAt(2) == ':') {
                        String[] parts = input.split(":");
                        String message = parts[1].trim();
                        chatManagerPrx.broadCastMessage(message, username);
                    } else {
                        System.out.println("(System) Error: Invalid command.");
                    }
                }

                sc.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
