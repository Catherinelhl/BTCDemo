package io.bcaas.tools.ecc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Sha256 Tool
 */
public class Sha256Tool {

    /**
     * Double  Sha256 String
     *
     * @return double Sha256 String
     */
    public static String doubleSha256ToString(String message) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] shaFirst = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        byte[] shaSecond = digest.digest(shaFirst);
        return bytesToHex(shaSecond);
    }

    /**
     * Sha-256 String
     *
     * @return double Sha256 String
     */
    public static String sha256ToString(String message) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    /**
     * Sha-256 To byte[]
     *
     * @return byte[]
     */
    public static byte[] sha256ToByte(String message) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

        return encodedhash;
    }

    /**
     * Bytes To Hex
     *
     * @return Hex String
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
