package com.gorany.ichatu.storage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckStorage {

    public static CheckStorage instance;
    private Map<Long, Set<Long>> checkMap;

    private CheckStorage(){
        checkMap = new HashMap<>();
    }

    public static CheckStorage getInstance(){
        if(instance == null)
            instance = new CheckStorage();

        return instance;
    }

    public Map<Long, Set<Long>> getCheckMap(){
        return checkMap;
    }
}
