package common;

public class Message {
    public String Sender;
    public String Text;
    public String Receiver = "global";

    public Message(String text){
        Text = text;
    }

    public Message(String text, String receiver){
        Text = text;
        Receiver = receiver;
    }
}
