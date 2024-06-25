package common.Encryption;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

public class RSAEncryptionService implements IEncryptionService<RSAKey> {
    private static final int CHUNK_SIZE = 64; // Define chunk size (adjust based on your key size and padding)

    @Override
    public String encrypt(String plainText, RSAKey key) {
        validatePublicKey(key);
        RSAPublicKey publicKey = (RSAPublicKey) key;

        List<byte[]> plainTextChunks = splitIntoChunks(plainText.getBytes(StandardCharsets.UTF_8));
        List<byte[]> encryptedChunks = encryptChunks(plainTextChunks, publicKey);

        // Convert the encrypted chunks to a single byte array with length prefixes
        ByteBuffer buffer = ByteBuffer.allocate(calculateTotalLength(encryptedChunks));
        for (byte[] chunk : encryptedChunks) {
            buffer.putInt(chunk.length);
            buffer.put(chunk);
        }
        byte[] cipherBytes = buffer.array();

        // Convert the byte array to a base64-encoded string
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    @Override
    public String decrypt(String cipherText, RSAKey key) {
        validatePrivateKey(key);
        RSAPrivateKey privateKey = (RSAPrivateKey) key;

        // Decode the base64-encoded string to a byte array
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

        // Extract the encrypted chunks from the byte array
        ByteBuffer buffer = ByteBuffer.wrap(cipherBytes);
        List<byte[]> encryptedChunks = new ArrayList<>();
        while (buffer.hasRemaining()) {
            int length = buffer.getInt();
            byte[] chunk = new byte[length];
            buffer.get(chunk);
            encryptedChunks.add(chunk);
        }

        List<byte[]> decryptedChunks = decryptChunks(encryptedChunks, privateKey);

        return concatenateChunks(decryptedChunks);
    }

    private void validatePublicKey(RSAKey key) {
        if (!(key instanceof RSAPublicKey)) {
            throw new IllegalArgumentException("Key must be an instance of RSAPublicKey");
        }
    }

    private void validatePrivateKey(RSAKey key) {
        if (!(key instanceof RSAPrivateKey)) {
            throw new IllegalArgumentException("Key must be an instance of RSAPrivateKey");
        }
    }

    private List<byte[]> splitIntoChunks(byte[] data) {
        List<byte[]> chunks = new ArrayList<>();
        for (int i = 0; i < data.length; i += CHUNK_SIZE) {
            int chunkSize = Math.min(CHUNK_SIZE, data.length - i);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(data, i, chunk, 0, chunkSize);
            chunks.add(chunk);
        }
        return chunks;
    }

    private List<byte[]> encryptChunks(List<byte[]> chunks, RSAPublicKey publicKey) {
        List<byte[]> encryptedChunks = new ArrayList<>();
        for (byte[] chunk : chunks) {
            BigInteger chunkBigInt = new BigInteger(1, chunk);
            BigInteger encryptedChunk = chunkBigInt.modPow(publicKey.getExponent(), publicKey.getModulus());
            encryptedChunks.add(encryptedChunk.toByteArray());
        }
        return encryptedChunks;
    }

    private List<byte[]> decryptChunks(List<byte[]> encryptedChunks, RSAPrivateKey privateKey) {
        List<byte[]> decryptedChunks = new ArrayList<>();
        for (byte[] encryptedChunk : encryptedChunks) {
            BigInteger encryptedChunkBigInt = new BigInteger(1, encryptedChunk);
            BigInteger decryptedChunk = encryptedChunkBigInt.modPow(privateKey.getExponent(), privateKey.getModulus());
            decryptedChunks.add(decryptedChunk.toByteArray());
        }
        return decryptedChunks;
    }

    private String concatenateChunks(List<byte[]> chunks) {
        StringBuilder plainText = new StringBuilder();
        for (byte[] chunk : chunks) {
            plainText.append(new String(chunk, StandardCharsets.UTF_8));
        }
        return plainText.toString();
    }

    private int calculateTotalLength(List<byte[]> chunks) {
        int totalLength = 0;
        for (byte[] chunk : chunks) {
            totalLength += Integer.BYTES + chunk.length;
        }
        return totalLength;
    }
}