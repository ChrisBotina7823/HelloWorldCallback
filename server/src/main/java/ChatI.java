import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.zeroc.Ice.Current;

import Demo.CallbackPrx;

public class ChatI implements Demo.Chat {
    // Map to store users and their respective callback proxies
    Map<String, CallbackPrx> users = new HashMap<>();
    // Map to store pending messages for each user
    Map<String, List<String>> pendingMessages = new HashMap<>();

    // Semaphore to control map access
    Semaphore semaphore = new Semaphore(1);

    public ChatI() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    checkConnections();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean registerUser(String username, CallbackPrx callback, Current current) {
        try {
            semaphore.acquire();
            username = username.trim();

            if (users.containsKey(username)) {
                return false;
            }

            if (pendingMessages.containsKey(username)) {
                // User is reconnecting, store callback and mark for pending message delivery
                users.put(username, callback);
                System.out.println("User " + username + " reconnected");

                // Deliver pending messages
                List<String> messages = pendingMessages.get(username);
                if (messages != null) {
                    callback.reportResponse("(System) Pending messages:");
                    for (String message : messages) {
                        callback.reportResponse(message);
                    }
                    messages.clear(); // Clear the messages after delivering
                }
            } else {
                users.put(username, callback);
                pendingMessages.put(username, new ArrayList<>());
                System.out.println("User " + username + " registered");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            semaphore.release();
        }
    }

    @Override
    public void unRegisterUser(String username, Current current) {
        try {
            semaphore.acquire();
            users.remove(username);
            System.out.println("User " + username + " unregistered");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    @Override
    public void listClients(String username, Current current) {
        System.out.println(users.size());
        CallbackPrx callback = users.get(username);
        StringBuilder sb = new StringBuilder();
        sb.append("\nList of clients:\n");
        int cnt = 1;
        for (String user : users.keySet()) {
            sb.append("(" + cnt + "): " + user + "\n");
            cnt++;
        }
        callback.reportResponse(sb.toString());
    }

    @Override
    public void sendMessage(String s, String fromUser, String destUser, Current current) {
        CallbackPrx destPrx = users.get(destUser);
        CallbackPrx fromPrx = users.get(fromUser);
        if (destPrx != null && fromPrx != destPrx) {
            destPrx.reportResponse(fromUser + ": " + s);
        } else if (fromPrx == destPrx) {
            fromPrx.reportResponse("You cannot send message to yourself");
        } else {
            fromPrx.reportResponse("User " + destUser + " is currently offline. Your message will be delivered when they reconnect.");
            // Store the message in pending messages
            List<String> messages = pendingMessages.get(destUser);
            if (messages != null) {
                messages.add(fromUser + ": " + s);
            } else {
                messages = new ArrayList<>();
                messages.add(fromUser + ": " + s);
                pendingMessages.put(destUser, messages);
            }
        }
    }

    @Override
    public void broadCastMessage(String s, String fromUser, Current current) {
        for (String user : users.keySet()) {
            if (!user.equals(fromUser)) {
                CallbackPrx destPrx = users.get(user);
                destPrx.reportResponse(fromUser + ": " + s);
            }
        }
    }

    public void printString(String msg, com.zeroc.Ice.Current current) {
        System.out.println(msg);
    }

    public void checkConnections() {
        for (String user : new ArrayList<>(users.keySet())) {
            CallbackPrx callback = users.get(user);
            try {
                callback.ice_ping();
            } catch (Exception e) {
                users.remove(user);
                System.out.println("User " + user + " disconnected");
            }
        }
    }

    public void fact(long n, Demo.CallbackPrx callback, com.zeroc.Ice.Current current) {
        Thread thread = new Thread(() -> {
            BigInteger fact = BigInteger.ONE;
            for (long i = 1; i <= n; i++) {
                fact = fact.multiply(BigInteger.valueOf(i));
            }
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                // TODO: handle exception
            }
            String response = "Factorial of " + n + " is: " + fact;
            callback.reportResponse(response);
        });
        thread.start();
    }
}