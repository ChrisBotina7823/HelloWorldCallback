module Demo
{

    interface Callback{
        void reportResponse(string response);
    }

    interface Chat {
        void registerUser(string username, Callback* callback);
        void listClients(string username);
        void sendMessage(string message, string fromUser, string destUser);
        void broadCastMessage(string message, string fromUser);
        void printString(string s);
        void fact(long n, Callback* callback);
    }

}