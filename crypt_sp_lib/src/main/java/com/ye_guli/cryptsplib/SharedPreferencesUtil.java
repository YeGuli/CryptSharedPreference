package com.ye_guli.cryptsplib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Ye_Guli on 2016/3/24.
 * <p>
 * SP相关工具类
 */
class SharedPreferencesUtil {
    private final static String SETTING_SHAREDPREFERENCES_NAME = "zk.setting";
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static SharedPreferences zkSharedPreferences;
    private static SharedPreferences.Editor zkSharedPreferencesEditor;

    /**
     * 更改配置信息
     *
     * @param key   更改的配置项
     * @param value 更改后的值
     * @param type  值的类型
     */
    @SuppressLint("CommitPrefEdits")
    static void changeSetting(Context context, String key, Object value, String type) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        zkSharedPreferencesEditor = zkSharedPreferences.edit();
        switch (type) {
            case "int":
                zkSharedPreferencesEditor.putInt(key, (int) value);
                break;
            case "float":
                zkSharedPreferencesEditor.putFloat(key, (float) value);
                break;
            case "boolean":
                zkSharedPreferencesEditor.putBoolean(key, (boolean) value);
                break;
            case "long":
                zkSharedPreferencesEditor.putLong(key, (long) value);
                break;
            case "String":
                zkSharedPreferencesEditor.putString(key, (String) value);
                break;
            case "set":
                zkSharedPreferencesEditor.putStringSet(key, (Set<String>) value);
                break;
            default:
                break;
        }
        zkSharedPreferencesEditor.apply();
    }

    /**
     * 更改配置信息
     *
     * @param key   更改的配置项
     * @param value 更改后的值
     */
    @SuppressLint("CommitPrefEdits")
    static void changeSetting(Context context, String key, Object value) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        zkSharedPreferencesEditor = zkSharedPreferences.edit();
        if (value instanceof Integer) {
            zkSharedPreferencesEditor.putInt(key, (int) value);
        } else if (value instanceof Float) {
            zkSharedPreferencesEditor.putFloat(key, (float) value);
        } else if (value instanceof Boolean) {
            zkSharedPreferencesEditor.putBoolean(key, (boolean) value);
        } else if (value instanceof Long) {
            zkSharedPreferencesEditor.putLong(key, (long) value);
        } else if (value instanceof String) {
            zkSharedPreferencesEditor.putString(key, (String) value);
        }
        zkSharedPreferencesEditor.apply();
    }

    /**
     * 获取配置信息
     *
     * @param key          指定的配置项
     * @param defaultValue 为空时默认的返回值
     * @param type         值的类型
     */
    static Object getSetting(Context context, String key, Object defaultValue, String type) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        switch (type) {
            case "int":
                return zkSharedPreferences.getInt(key, (int) defaultValue);
            case "float":
                return zkSharedPreferences.getFloat(key, (float) defaultValue);
            case "boolean":
                return zkSharedPreferences.getBoolean(key, (boolean) defaultValue);
            case "long":
                return zkSharedPreferences.getLong(key, (long) defaultValue);
            case "String":
                return zkSharedPreferences.getString(key, (String) defaultValue);
            case "set":
                return zkSharedPreferences.getStringSet(key, (Set<String>) defaultValue);
            default:
                return null;
        }
    }

    /**
     * 获取配置信息
     *
     * @param key          指定的配置项
     * @param defaultValue 为空时默认的返回值
     */
    static Object getSetting(Context context, String key, Object defaultValue) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        if (defaultValue instanceof Integer) {
            return zkSharedPreferences.getInt(key, (int) defaultValue);
        } else if (defaultValue instanceof Float) {
            return zkSharedPreferences.getFloat(key, (float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return zkSharedPreferences.getBoolean(key, (boolean) defaultValue);
        } else if (defaultValue instanceof Long) {
            return zkSharedPreferences.getLong(key, (long) defaultValue);
        } else if (defaultValue instanceof String) {
            return zkSharedPreferences.getString(key, (String) defaultValue);
        } else {
            return null;
        }
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    static void remove(Context context, String key) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        zkSharedPreferencesEditor = zkSharedPreferences.edit();
        zkSharedPreferencesEditor.remove(key).apply();
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     *
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    static boolean contains(Context context, String key) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return zkSharedPreferences.contains(key);
    }

    /**
     * 清除所有数据
     */
    static void clear(Context context) {
        zkSharedPreferences = context.getSharedPreferences(SETTING_SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        zkSharedPreferencesEditor = zkSharedPreferences.edit();
        zkSharedPreferencesEditor.clear().apply();
    }

    /**
     * 获取IMEI码
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_PHONE_STATE"/>}</p>
     *
     * @return IMIE码
     */
    @SuppressLint("HardwareIds")
    static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getDeviceId() : "";
    }

    /**
     * SHA512加密
     *
     * @param data 明文字符串
     *
     * @return 16进制密文
     */
    static String encryptSHA512ToString(String data) {
        return bytes2HexString(hashTemplate(data.getBytes(), "SHA512"));
    }

    /**
     * AES加密
     *
     * @param content 需要加密的内容
     * @param key     密钥
     * @return 加密后的数据
     */
    static byte[] encryptByAES(String content, String key) {
        try {
            byte[] enCodeFormat = getRawKey(key.getBytes());
            SecretKeySpec secKey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, secKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));// 初始化
            return cipher.doFinal(byteContent); // 加密
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密
     *
     * @param content 待解密内容
     * @param key     密钥
     *
     * @return 解密后的数据
     */
    static byte[] decryptByAES(byte[] content, String key) {
        try {
            byte[] enCodeFormat = getRawKey(key.getBytes());
            SecretKeySpec secKey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, secKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));// 初始化
            return cipher.doFinal(content); // 解密
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr string字符串
     * @return 二进制数组
     */
    static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) return null;
        int len = hexStr.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexStr.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf 二进制数组
     * @return string字符串
     */
    static String parseByte2HexStr(byte[] buf) {
        String HEX = "0123456789ABCDEF";
        if (buf == null) return "";
        StringBuilder result = new StringBuilder(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(HEX.charAt(buf[i] & 0x0f));
        }
        return result.toString();
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     *
     * @return 密文字节数组
     */
    private static byte[] encryptSHA512(byte[] data) {
        return hashTemplate(data, "SHA512");
    }

    /**
     * hash加密模板
     *
     * @param data      数据
     * @param algorithm 加密算法
     *
     * @return 密文字节数组
     */
    private static byte[] hashTemplate(byte[] data, String algorithm) {
        if (data == null || data.length <= 0) return null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     *
     * @return 16进制大写字符串
     */
    private static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static byte[] getRawKey(byte[] key) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            KeyGenerator kGen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
            sr.setSeed(key);
            kGen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey sKey = kGen.generateKey();
            return sKey.getEncoded();
        } else {
            KeyGenerator kGen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            sr.setSeed(key);
            kGen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey sKey = kGen.generateKey();
            return sKey.getEncoded();
        }
    }

    //android 7.0以上已经放弃使用SHA1PRNG算法从加密提供者那里取得密钥，因此对于7.0以上在这里实现一个Crypto提供者来生成秘钥
    //made by Ye_Guli on 2017.03.20
    private static class CryptoProvider extends Provider {
        public CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }
}
