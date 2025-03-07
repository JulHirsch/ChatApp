package common.Encryption;

import java.math.BigInteger;

public class RSAPublicKey extends RSAKey {
    public RSAPublicKey(BigInteger modulus, BigInteger exponent) {
        super(modulus, exponent);
    }

    public static RSAPublicKey fromBase64String(String base64String) {
        return (RSAPublicKey) RSAKey.fromBase64String(base64String, false);
    }
}
