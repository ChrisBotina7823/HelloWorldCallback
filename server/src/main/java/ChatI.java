import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.zeroc.Ice.Current;

import Demo.CallbackPrx;

public class ChatI implements Demo.Chat {
    Map<String, CallbackPrx> users = new HashMap<>();

    @Override
    public boolean registerUser(String username, CallbackPrx callback, Current current) {
        if(!users.containsKey(username)) {
            System.out.println("User " + username + " registered");
            users.put(username, callback);
            return true;
        } else {
            System.out.println("User " + username + " already registered");
            return false;
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
            fromPrx.reportResponse("User " + destUser + " not found");
        }
    }

    @Override
    public void broadCastMessage(String s, String fromUser, Current current) {
        // CallbackPrx fromPrx = users.get(fromUser);
        for (String user : users.keySet()) {
            if (!user.equals(fromUser)) {
                CallbackPrx destPrx = users.get(user);
                destPrx.reportResponse(fromUser + ": " + s);
            }
        }
        // fromPrx.reportResponse("Broadcast message sent");
    }
    

    public void printString(String msg, com.zeroc.Ice.Current current) {
        System.out.println(msg);
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
