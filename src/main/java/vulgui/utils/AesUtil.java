package vulgui.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;

public class AesUtil {
    public static byte[] encrypt(byte[] plainText, byte[] key) throws Exception {
        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // init key.
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(plainText);

        // Combine IV and encrypted part.
        byte[] encryptedIvandtext = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIvandtext, 0, ivSize);
        System.arraycopy(encrypted, 0, encryptedIvandtext, ivSize, encrypted.length);

        return encryptedIvandtext;
    }
}
