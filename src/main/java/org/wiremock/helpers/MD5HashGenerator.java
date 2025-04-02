package org.wiremock.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashGenerator {
    public static String calculateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] hash = md5.digest(input.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
