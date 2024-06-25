package common.Encryption;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyGenerator {
    private static final int DEFAULT_KEY_SIZE = 2048;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final BigInteger COMMON_PUBLIC_EXPONENT = BigInteger.valueOf(65537);

    public static RSAKeyPair generateKeyPair() {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    public static RSAKeyPair generateKeyPair(int keySize) {
        BigInteger p, q, n, phi, e, d;

        // Step 1: Generate two distinct prime numbers p and q
        p = BigInteger.probablePrime(keySize / 2, secureRandom);
        q = BigInteger.probablePrime(keySize / 2, secureRandom);
        while (p.equals(q)) {
            q = BigInteger.probablePrime(keySize / 2, secureRandom);
        }

        // Step 2: Compute n = p * q and phi = (p-1) * (q-1)
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Step 3: Choose e such that 1 < e < phi and gcd(e, phi) = 1
        e = COMMON_PUBLIC_EXPONENT;
        while (!phi.gcd(e).equals(BigInteger.ONE)) {
            e = BigInteger.probablePrime(16, secureRandom);
        }

        // Step 4: Compute d, the modular multiplicative inverse of e modulo phi
        d = e.modInverse(phi);

        // Create the public and private keys
        RSAPublicKey publicKey = new RSAPublicKey(n, e);
        RSAPrivateKey privateKey = new RSAPrivateKey(n, d);

        return new RSAKeyPair(publicKey, privateKey);
    }
}