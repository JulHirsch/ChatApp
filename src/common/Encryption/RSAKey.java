package common.Encryption;

import java.math.BigInteger;

public abstract class RSAKey implements IKey{
    protected BigInteger modulus;
    protected BigInteger exponent;

    public RSAKey(BigInteger modulus, BigInteger exponent) {
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getExponent() {
        return exponent;
    }
}

