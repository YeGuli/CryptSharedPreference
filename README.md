[![](https://jitpack.io/v/zzbljh123/CryptSharedPreference.svg)](https://jitpack.io/#zzbljh123/CryptSharedPreference)
# CryptSharedPreference

## 1、介绍

SharedPreferences中封装了一系列使用sp保存数据的方法，特点在于可以直接保存数据类与储存时加密数据。

## 2、如何依赖
step 1:
在项目根目录下的build.gradle添加
```groove
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```	
step 2:
添加项目依赖
```groove
	dependencies {
	        compile 'com.github.zzbljh123:CryptSharedPreference:v1.0.0'
	}
```

## 3、如何使用

1. 创建继承自`BaseSet`的数据类：
```java
public class AccountSet extends BaseSet {
  private String username = "";
  private String password = "";
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
}
```

2. 初始化SharedPreferencesHandler：
```java
SharedPreferencesHandler handler = SharedPreferencesHandler.getInstance();
handler.initSecretKey(getApplicationContext(), ""，false);
```

3. 使用SharedPreferencesHandler保存数据：
```java
AccountSet set = new AccountSet();
set.setUsername("kyon");
set.setPassword("asd456@#$%sdfgsgfs");
try {
  handler.saveSetting(context， set);
} catch (IOException e) {
}
```

4. 使用SharedPreferencesHandler读取保存的数据：
```java
try {
	AccountSet set = Shandler.getSettings(getApplicationContext(), AccountSet.class);
} catch (InstantiationException e) {
	e.printStackTrace();
} catch (IllegalAccessException e) {
	e.printStackTrace();
} catch (IllegalArgumentException e) {
	e.printStackTrace();
}
```

## 4、数据加密说明

1. sp的键名使用了SHA512加密；
2. sp的键值使用了AES加密，AES加密所需的key则是由当前设备的IMEI值经过一系列算法生成;

## 5、使用注意

如果初始化秘钥时选择使用设备IMEI码处理秘钥则需要权限`<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
