package common.Messages;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageTypeAdapter implements JsonDeserializer<IMessage>, JsonSerializer<IMessage> {

    @Override
    public IMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MessageType messageType = context.deserialize(jsonObject.get("_messageType"), MessageType.class);

        return switch (messageType) {
            case TEXT -> context.deserialize(jsonObject, TextMessage.class);
            case KEY_EXCHANGE -> context.deserialize(jsonObject, KeyExchangeMessage.class);
            default -> throw new JsonParseException("Unknown message type: " + messageType);
        };
    }

    @Override
    public JsonElement serialize(IMessage message, Type type, JsonSerializationContext context) {
        return context.serialize(message);
    }
}