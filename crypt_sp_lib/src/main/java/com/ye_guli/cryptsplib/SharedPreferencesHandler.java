package com.ye_guli.cryptsplib;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Ye_Guli on 2016/5/18.
 * <p>
 * 可将数据加密保存的SP模块，需将想要保存的数据类继承BaseSet，之后便可以调用sava方法保存数据。
 */
public class SharedPreferencesHandler {
    private final String TAG = "SPHandler";
    private String secretKey;
    private static SharedPreferencesHandler ourInstance;

    public static SharedPreferencesHandler getInstance() {
        if (ourInstance == null) {
            synchronized (SharedPreferencesHandler.class) {
                if (ourInstance == null) {
                    ourInstance = new SharedPreferencesHandler();
                }
            }
        }
        return ourInstance;
    }

    private SharedPreferencesHandler() {
    }

    /**
     * 初始化存储秘钥
     *
     * @param context    上下文
     * @param customKey  自定义秘钥，若为空则使用默认秘钥
     * @param isNeedIMEI 是否使用设备IMEI码处理秘钥，为true的情况下需添加权限 {@code <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     *
     * @return 数据
     */
    public void initSecretKey(Context context, String customKey, boolean isNeedIMEI) {
        String realKey = customKey.isEmpty() ? "&*v#C$D%66^F&gg@14^kyon" : customKey;
        if (isNeedIMEI) {
            secretKey = SharedPreferencesUtil.encryptSHA512ToString(SharedPreferencesUtil.getIMEI(context) + realKey).substring(0, 16);//生成秘钥
        } else {
            secretKey = SharedPreferencesUtil.encryptSHA512ToString(realKey).substring(0, 16);//生成秘钥
        }
    }

    /**
     * 读取配置文件
     *
     * @param context 上下文
     * @param tClass  取数据的类型
     *
     * @return 数据
     */
    public synchronized <T extends BaseSet> T getSettings(Context context, Class<T> tClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException {
        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey not init!");
        }
        return getSettingFromSp(context, tClass.getSimpleName(), tClass);
    }

    /**
     * 读取配置文件
     *
     * @param context 上下文
     * @param tClass  要取数据的类型
     *
     * @return Rx数据
     */
    public <T extends BaseSet> Flowable<T> getSettingsForRx(final Context context, final Class<T> tClass) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                if (secretKey == null) {
                    e.onError(new IllegalArgumentException("secretKey not init!"));
                }
                T t = getSettingFromSp(context, tClass.getSimpleName(), tClass);
                if (t == null) {
                    e.onError(new NullPointerException());
                } else {
                    e.onNext(t);
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    /**
     * 读取配置文件(同步方法)
     *
     * @param context 上下文
     * @param tClass  要取数据的类型
     *
     * @return Rx数据
     */
    public <T extends BaseSet> Flowable<T> getSettingsForRxExecute(final Context context, final Class<T> tClass) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                if (secretKey == null) {
                    e.onError(new IllegalArgumentException("secretKey not init!"));
                }
                T t = getSettingFromSp(context, tClass.getSimpleName(), tClass);
                if (t == null) {
                    e.onError(new NullPointerException());
                } else {
                    e.onNext(t);
                }
            }
        }, BackpressureStrategy.BUFFER)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    /**
     * 保存配置文件
     *
     * @param context 上下文
     * @param t       要保存的数据
     */
    public synchronized <T extends BaseSet> void saveSetting(Context context, T t) throws IOException, IllegalArgumentException {
        if (secretKey == null) {
            throw new IllegalArgumentException("secretKey not init!");
        } else {
            saveSettingToSp(context, t.getClass().getSimpleName(), t);
        }
    }

    /**
     * 保存配置文件
     *
     * @param context 上下文
     * @param t       要保存的数据
     *
     * @return Rx
     */
    public <T extends BaseSet> Flowable<T> saveSettingsForRx(final Context context, final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                if (secretKey == null) {
                    e.onError(new IllegalArgumentException("secretKey not init!"));
                }
                if (t == null || context == null) {
                    e.onError(new NullPointerException());
                } else {
                    saveSettingToSp(context, t.getClass().getSimpleName(), t);
                    e.onComplete();
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    /**
     * 保存配置文件(同步方法)
     *
     * @param context 上下文
     * @param t       要保存的数据
     *
     * @return Rx
     */
    public <T extends BaseSet> Flowable<T> saveSettingsForRxExecute(final Context context, final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                if (secretKey == null) {
                    e.onError(new IllegalArgumentException("secretKey not init!"));
                }
                if (t == null || context == null) {
                    e.onError(new NullPointerException());
                } else {
                    saveSettingToSp(context, t.getClass().getSimpleName(), t);
                    e.onComplete();
                }
            }
        }, BackpressureStrategy.BUFFER)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    /**
     * 判断指定数据类是否存在于配置文件中
     *
     * @param context 上下文
     * @param tClass  要判断的数据类
     */
    public synchronized <T extends BaseSet> boolean contansSetting(Context context, Class<T> tClass) {
        String encryptedKey = SharedPreferencesUtil.encryptSHA512ToString(tClass.getSimpleName());//使用SHA512加密键名
        return SharedPreferencesUtil.contains(context, encryptedKey);
    }

    /**
     * 清空指定数据类
     *
     * @param context 上下文
     * @param tClass  要清除的的数据类
     */
    public synchronized <T extends BaseSet> void clearSetting(Context context, Class<T> tClass) {
        String encryptedKey = SharedPreferencesUtil.encryptSHA512ToString(tClass.getSimpleName());//使用SHA512加密键名
        SharedPreferencesUtil.remove(context, encryptedKey);
    }

    /**
     * 清空配置文件
     *
     * @param context 上下文
     */
    public synchronized void clearAllSetting(Context context) {
        SharedPreferencesUtil.clear(context);
    }

/////////////////////////////////////私有方法/////////////////////////////////////

    /**
     * 提取保存的数据
     *
     * @param context 上下文
     * @param key     键名
     * @param tClass  需要提取的值的类型
     */
    private synchronized <T extends BaseSet> T getSettingFromSp(Context context, String key, Class<T> tClass) throws IllegalAccessException, InstantiationException {
        String encryptedKey = SharedPreferencesUtil.encryptSHA512ToString(key);//使用SHA512加密键名
        String value = (String) SharedPreferencesUtil.getSetting(context, encryptedKey, "");//根据键名提取数据
        if (value == null || value.equals("")) {
            return tClass.newInstance();
        } else {
            try {
                String decryptedValue = new String(SharedPreferencesUtil.decryptByAES(SharedPreferencesUtil.parseHexStr2Byte(value), secretKey), "UTF-8");//使用AES解密保存的值
                return deSerialization(decryptedValue);//返回反序列化后的数据
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 保存数据
     *
     * @param context 上下文
     * @param key     键名
     * @param value   需要保存的值
     */
    private synchronized <T extends BaseSet> void saveSettingToSp(Context context, String key, T value) throws IOException {
        String encryptedKey = SharedPreferencesUtil.encryptSHA512ToString(key);//使用SHA512加密键名
        String serializedValue = serialize(value);//序列化需要保存的值
        String encryptedValue = SharedPreferencesUtil.parseByte2HexStr(SharedPreferencesUtil.encryptByAES(serializedValue, secretKey));//使用AES加密需要保存的值
        SharedPreferencesUtil.changeSetting(context, encryptedKey, encryptedValue);//保存加密后的数据
    }

    /**
     * 序列化对象
     *
     * @param t 需要序列化的实体类
     *
     * @return 序列化数据
     *
     * @throws IOException io异常
     */
    private <T extends BaseSet> String serialize(T t) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(t);
        String objectVal = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return objectVal;
    }

    /**
     * 反序列化对象
     *
     * @param str 需要反序列化的数据
     *
     * @return 实体类
     *
     * @throws IOException            io异常
     * @throws ClassNotFoundException 未找到指定类异常
     */
    private <T extends BaseSet> T deSerialization(String str) throws IOException, ClassNotFoundException {
        byte[] buffer = Base64.decode(str, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = new ObjectInputStream(bais);
        T t = (T) ois.readObject();
        ois.close();
        bais.close();
        return t;
    }
}
