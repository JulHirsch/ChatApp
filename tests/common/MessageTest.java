package common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testMessageConstructorWithAllFields() {
        Message message = new Message("Hello", "sender1", "customName1", "receiver1", "AES", "key1");

        assertEquals("Hello", message.getText());
        assertEquals("sender1", message.getSender());
        assertEquals("customName1", message.getCustomName());
        assertEquals("receiver1", message.getReceiver());
        assertEquals("AES", message.getEncryptionType());
        assertEquals("key1", message.getEncryptionKey());
    }

    @Test
    public void testMessageConstructorWithGlobalReceiver() {
        Message message = new Message("Hello", "customName1",null, "AES", "key1");

        assertEquals("Hello", message.getText());
        assertEquals("", message.getSender());
        assertEquals("customName1", message.getCustomName());
        assertEquals(Message.GLOBAL_RECEIVER, message.getReceiver());
        assertEquals("AES", message.getEncryptionType());
        assertEquals("key1", message.getEncryptionKey());
    }

    @Test
    public void testIsGlobal() {
        Message globalMessage = new Message("Hello", "sender1", "customName1", Message.GLOBAL_RECEIVER, "AES", "key1");
        assertTrue(globalMessage.isGlobal());

        Message nonGlobalMessage = new Message("Hello", "sender1", "customName1", "receiver1", "AES", "key1");
        assertFalse(nonGlobalMessage.isGlobal());
    }

    @Test
    public void testSerializationToJson() {
        Message message = new Message("Hello", "sender1", "customName1", "receiver1", "AES", "key1");
        String json = message.toJson();

        assertNotNull(json);
        assertTrue(json.contains("\"_text\":\"Hello\""));
        assertTrue(json.contains("\"_sender\":\"sender1\""));
        assertTrue(json.contains("\"_customName\":\"customName1\""));
        assertTrue(json.contains("\"_receiver\":\"receiver1\""));
        assertTrue(json.contains("\"_encryptionType\":\"AES\""));
        assertTrue(json.contains("\"_encryptionKey\":\"key1\""));
    }

    @Test
    public void testDeserializationFromJson() {
        String json = "{\"_sender\":\"sender1\",\"_customName\":\"customName1\",\"_text\":\"Hello\",\"_receiver\":\"receiver1\",\"_encryptionType\":\"AES\",\"_encryptionKey\":\"key1\"}";
        Message message = Message.fromJson(json);

        assertNotNull(message);
        assertEquals("Hello", message.getText());
        assertEquals("sender1", message.getSender());
        assertEquals("customName1", message.getCustomName());
        assertEquals("receiver1", message.getReceiver());
        assertEquals("AES", message.getEncryptionType());
        assertEquals("key1", message.getEncryptionKey());
    }

    @Test
    public void testNullFieldsHandling() {
        Message message = new Message(null, null, null, null, null, null);

        assertEquals("", message.getText());
        assertEquals("", message.getSender());
        assertEquals("", message.getCustomName());
        assertEquals(Message.GLOBAL_RECEIVER, message.getReceiver());
        assertEquals("", message.getEncryptionType());
        assertEquals("", message.getEncryptionKey());
    }
}