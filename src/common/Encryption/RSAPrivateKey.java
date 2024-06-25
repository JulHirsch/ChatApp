package common.Encryption;

import java.math.BigInteger;

public class RSAPrivateKey extends RSAKey {
    public RSAPrivateKey(BigInteger modulus, BigInteger exponent) {
        super(modulus, exponent);
    }
}
