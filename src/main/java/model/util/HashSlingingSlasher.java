package model.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

/**
 * Squidward: You mean you've never heard the story of the... hash-slinging slasher?
 * SpongeBob: The slash-bringing hasher?
 * Squidward: The hash-slinging slasher.
 * SpongeBob: The sash wringing... the trash thinging... mash flinging... the flash springing, bringing the the crash thinging the...
 * Squidward: Yes. The hash-slinging slasher.
 */

public class HashSlingingSlasher{
    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    /**
     * Salts and Hashes the given password to make it nice and secure!
     * @param password The password to hash
     * @return The resulting hash
     * @throws Exception
     */
    public static String getSaltedHash(String password) throws Exception{
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks the given password against the stored hash
     * @param password The password to check
     * @param stored The stored hash
     * @return Whether or not the password matches the hash
     * @throws IllegalStateException
     */
    public static boolean check(String password, String stored) throws Exception{
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2)
            throw new IllegalStateException("The stored password have the form 'salt$hash'");
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    private static String hash(String password, byte[] salt) throws Exception{
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
        return Base64.encodeBase64String(key.getEncoded());
    }
}