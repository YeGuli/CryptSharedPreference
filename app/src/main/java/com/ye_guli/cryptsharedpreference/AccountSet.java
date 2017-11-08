package com.ye_guli.cryptsharedpreference;

import com.ye_guli.cryptsplib.BaseSet;

/**
 * Created by Ye_Guli on 2017/11/08.
 * <p>
 * AccountSet
 */
public class AccountSet extends BaseSet {
    private String username;
    private String password;

    public AccountSet() {
    }

    public AccountSet(String username, String password) {
        this.username = username;
        this.password = password;
    }

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

    @Override
    public String toString() {
        return "AccountSet{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
