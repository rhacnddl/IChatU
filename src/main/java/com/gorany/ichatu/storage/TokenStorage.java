package com.gorany.ichatu.storage;

import java.util.HashMap;
import java.util.Map;

public class TokenStorage {

    public static TokenStorage instance;
    private Map<Long, String> map;

    private TokenStorage(){

        map = new HashMap<>();
    }

    public static TokenStorage getInstance(){
        if(instance == null)
            instance = new TokenStorage();

        return instance;
    }

    public Map<Long, String> getMap() {
        return map;
    }
}
