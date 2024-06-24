package common.Encryption;

public class CaesarEncryptionService implements IEncryptionService<CaesarKey> {
    private static final int ALPHABET_SIZE = 26;
    private static final char UPPERCASE_A = 'A';
    private static final char UPPERCASE_Z = 'Z';
    private static final char LOWERCASE_A = 'a';
    private static final char LOWERCASE_Z = 'z';

    @Override
    public String encrypt(String plainText, CaesarKey key) {
        if (plainText == null) return "";
        return shiftString(plainText, key.shift());
    }

    @Override
    public String decrypt(String cipherText, CaesarKey key) {
        if (cipherText == null) return "";
        return shiftString(cipherText, -key.shift());
    }

    private String shiftString(String text, int shift) {
        StringBuilder shiftedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            shiftedText.append(shiftChar(text.charAt(i), shift));
        }
        return shiftedText.toString();
    }

    private char shiftChar(char character, int shift) {
        if (Character.isLowerCase(character)) return shiftLowercaseChar(character, shift);
        if (Character.isUpperCase(character)) return shiftUppercaseChar(character, shift);
        return character; // Non-alphabetic characters are not encrypted
    }

    private char shiftUppercaseChar(char character, int shift) {
        int shiftedChar = character + shift;
        if (shiftedChar > UPPERCASE_Z) {
            return (char) (shiftedChar - ALPHABET_SIZE);
        } else if (shiftedChar < UPPERCASE_A) {
            return (char) (shiftedChar + ALPHABET_SIZE);
        }
        return (char) shiftedChar;
    }

    private char shiftLowercaseChar(char character, int shift) {
        int shiftedChar = character + shift;
        if (shiftedChar > LOWERCASE_Z) {
            return (char) (shiftedChar - ALPHABET_SIZE);
        } else if (shiftedChar < LOWERCASE_A) {
            return (char) (shiftedChar + ALPHABET_SIZE);
        }
        return (char) shiftedChar;
    }
}
