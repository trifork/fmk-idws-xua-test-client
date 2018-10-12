package com.trifork.idwsxua.fmktestclient.security;

public class TokenProvider {
    private static final ThreadLocal<Object> tokenHolder = new ThreadLocal<>();

    public static void setToken(Object token) {
        tokenHolder.set(token);
    }

    public static Object getToken() {
        return tokenHolder.get();
    }
}
