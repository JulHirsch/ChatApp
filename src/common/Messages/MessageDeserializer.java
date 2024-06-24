package common.Messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageDeserializer {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(IMessage.class, new MessageTypeAdapter())
            .create();

    public static IMessage fromJson(String jsonString) {
        return gson.fromJson(jsonString, IMessage.class);
    }

    public static String toJson(IMessage message) {
        return gson.toJson(message);
    }
}