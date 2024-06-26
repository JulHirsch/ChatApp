package common.Encryption;

import java.math.BigInteger;
import java.util.Base64;

public abstract class RSAKey implements IKey {
    protected BigInteger modulus;
    protected BigInteger exponent;

    public RSAKey(BigInteger modulus, BigInteger exponent) {
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public static RSAKey fromBase64String(String base64String, boolean isPrivateKey) {
        if (base64String.length() < 8) {
            throw new IllegalArgumentException("Invalid Base64 string format.");
        }

        // Extract lengths
        int modulusLength = Integer.parseInt(base64String.substring(0, 4));
        int exponentLength = Integer.parseInt(base64String.substring(4 + modulusLength, 8 + modulusLength));

        if (base64String.length() != 8 + modulusLength + exponentLength) {
            throw new IllegalArgumentException("Invalid Base64 string format.");
        }

        // Extract Base64 encoded strings
        String modulusBase64 = base64String.substring(4, 4 + modulusLength);
        String exponentBase64 = base64String.substring(8 + modulusLength, 8 + modulusLength + exponentLength);

        BigInteger modulus = new BigInteger(Base64.getDecoder().decode(modulusBase64));
        BigInteger exponent = new BigInteger(Base64.getDecoder().decode(exponentBase64));

        if (isPrivateKey) {
            return new RSAPrivateKey(modulus, exponent);
        } else {
            return new RSAPublicKey(modulus, exponent);
        }
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    public String toBase64String() {
        String modulusBase64 = Base64.getEncoder().encodeToString(modulus.toByteArray());
        String exponentBase64 = Base64.getEncoder().encodeToString(exponent.toByteArray());

        // Prefix lengths to ensure correct parsing
        String modulusLength = String.format("%04d", modulusBase64.length());
        String exponentLength = String.format("%04d", exponentBase64.length());

        return modulusLength + modulusBase64 + exponentLength + exponentBase64;
    }
}