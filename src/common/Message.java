package common;

public class Message {
    public String Sender;
    public String Text;
    public String Receiver = "global";

    public Message(String text){
        Text = text;
    }

    public Message(String text, String sender){
        Text = text;
        Sender = sender;
    }

    public Message(String text, String sender, String receiver){
        Text = text;
        Sender = sender;
        Receiver = receiver;
    }
}
