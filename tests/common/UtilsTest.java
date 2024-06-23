package common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    public void testValidIPAddresses() {
        assertTrue(Utils.isValidIPAddress("192.168.0.1"));
        assertTrue(Utils.isValidIPAddress("255.255.255.255"));
        assertTrue(Utils.isValidIPAddress("0.0.0.0"));
        assertTrue(Utils.isValidIPAddress("127.0.0.1"));
        assertTrue(Utils.isValidIPAddress("1.1.1.1"));
    }

    @Test
    public void testInvalidIPAddresses() {
        assertFalse(Utils.isValidIPAddress("256.256.256.256"));
        assertFalse(Utils.isValidIPAddress("192.168.0.256"));
        assertFalse(Utils.isValidIPAddress("192.168.0"));
        assertFalse(Utils.isValidIPAddress("192.168.0.1.1"));
        assertFalse(Utils.isValidIPAddress("abc.def.ghi.jkl"));
        assertFalse(Utils.isValidIPAddress("123.456.789.0"));
        assertFalse(Utils.isValidIPAddress("192.168.0.-1"));
    }

    @Test
    public void testEmptyAndNullIPAddresses() {
        assertFalse(Utils.isValidIPAddress(""));
        assertFalse(Utils.isValidIPAddress(null));
    }

    @Test
    public void testEdgeCases() {
        assertFalse(Utils.isValidIPAddress("192.168.0.1 "));  // Trailing space
        assertFalse(Utils.isValidIPAddress(" 192.168.0.1"));  // Leading space
        assertFalse(Utils.isValidIPAddress("192.168.0.1\n")); // Trailing newline
        assertFalse(Utils.isValidIPAddress("\n192.168.0.1")); // Leading newline
    }
}