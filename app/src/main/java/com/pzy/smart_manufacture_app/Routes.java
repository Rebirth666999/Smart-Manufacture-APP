package com.pzy.smart_manufacture_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;


import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class Routes {
    private static final String TAG = "Routes";
    private static final String JWT_SECRET_KEY_PREF = "jwt_secret_key";
    private static final String ALGORITHM = "HmacSHA256";
    private static final String DEFAULT_SECRET_KEY = "this-is-a-default-secret-key-for-development-1234567890";

    // 生成并保存安全的HS256密钥
    private static SecretKey getOrCreateSecretKey(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyBase64 = prefs.getString(JWT_SECRET_KEY_PREF, null);
        
        if (keyBase64 != null) {
            try {
                byte[] keyBytes = Base64.decode(keyBase64, Base64.DEFAULT);
                return new SecretKeySpec(keyBytes, ALGORITHM);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid stored key format", e);
            }
        }
        
        // 使用固定密钥或生成新密钥并保存
        byte[] keyBytes = DEFAULT_SECRET_KEY.getBytes();
        String encodedKey = Base64.encodeToString(keyBytes, Base64.DEFAULT);
        prefs.edit().putString(JWT_SECRET_KEY_PREF, encodedKey).apply();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("server_url", "");
    }

    public static void saveBaseUrl(Context context, String url) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("server_url", url);
        editor.apply();
    }

    public static boolean isTokenValid(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token = prefs.getString("token", "");
        String rawToken = prefs.getString("raw_token", "");
        return !token.isEmpty() || !rawToken.isEmpty();
    }

    public static String getValidToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token = prefs.getString("token", "");
        String rawToken = prefs.getString("raw_token", "");

        // 确保token总是以"Bearer "开头
        if (!token.isEmpty()) {
            if (!token.startsWith("Bearer ")) {
                return "Bearer " + token;
            }
            return token;
        } else if (!rawToken.isEmpty()) {
            return "Bearer " + rawToken;
        }
        
        return "";
    }

    // 确保异常接口地址格式正确
    public static String getExceptionBaseUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String url = prefs.getString("exception_server_url", "");

        // 确保URL以/结尾
        if (!url.isEmpty() && !url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    public static void saveExceptionBaseUrl(Context context, String url) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("exception_server_url", url);
        editor.apply();
    }
}