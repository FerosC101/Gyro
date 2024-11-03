package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int user_id;
    private String username;
    private String password;
    private List<Achievement> achievement;

    public User(int user_id, String username, String password) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.achievement = new ArrrayList<>();

    }
}
