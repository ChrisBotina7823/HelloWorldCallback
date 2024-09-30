import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class CallbackI implements Demo.Callback {
    public void reportResponse(String response, com.zeroc.Ice.Current current) {
        System.out.println(response);
    }
}


