package com.gorany.ichatu.storage;

import com.gorany.ichatu.dto.ChatDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* 일단 BufferStorage 라는 싱글톤 패턴을 가진 객체에 저장하나,
* 추후 Redis에 저장하여 캐싱하고, 일정 사이즈만큼 차면 DB에 persist 하도록 */
public class BufferStorage {

    public static BufferStorage instance;
    private Map<Long, List<ChatDTO>> bufferMap;

    private BufferStorage(){
        bufferMap = new HashMap<>();
    }

    public static BufferStorage getInstance(){
        if(instance == null)
            instance = new BufferStorage();

        return instance;
    }

    public Map<Long, List<ChatDTO>> getBufferMap(){
        return bufferMap;
    }
}
