package common.Encryption;

import java.math.BigInteger;

public class RSAPrivateKey extends RSAKey {
    public RSAPrivateKey(BigInteger modulus, BigInteger exponent) {
        super(modulus, exponent);
    }

    public static RSAPrivateKey fromBase64String(String base64String) {
        return (RSAPrivateKey) RSAKey.fromBase64String(base64String, true);
    }
}
