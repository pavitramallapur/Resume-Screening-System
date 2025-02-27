package com.example.Sample.util;

import java.util.Base64;

public class Base64UrlDecode {
    public static void main(String[] args) {
        String tokenPart = "headerBase64url"; // Replace with your token part (Header, Payload, etc.)
        byte[] decodedBytes = Base64.getUrlDecoder().decode(tokenPart);
        String decodedString = new String(decodedBytes);
        System.out.println(decodedString);
    }
}
