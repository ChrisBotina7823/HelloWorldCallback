module Demo
{

    interface Callback{
        void reportResponse(string response);
    }

    interface Chat {
        bool registerUser(string username, Callback* callback);
        void unRegisterUser(string username);
        void listClients(string username);
        void sendMessage(string message, string fromUser, string destUser);
        void broadCastMessage(string message, string fromUser);
        void printString(string s);
        void fact(long n, Callback* callback);
        void fib(long n, Callback* callback);
    }

}