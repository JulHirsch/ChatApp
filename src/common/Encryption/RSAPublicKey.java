package common.Encryption;

import java.math.BigInteger;

public class RSAPublicKey extends RSAKey {
    public RSAPublicKey(BigInteger modulus, BigInteger exponent) {
        super(modulus, exponent);
    }
}
