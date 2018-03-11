package com.lewis_v.audiohandle.play;

/**
 * auth: lewis-v
 * time: 2018/3/1.
 */

public enum AudioPlayMode {
    UNDEFINE(-1),//未定义类型
    MEGAPHONE(0),//扩音器
    RECEIVER(1);//听筒

    private int typeName;

    AudioPlayMode(int typeName) {
        this.typeName = typeName;
    }

    public int getTypeName() {
        return typeName;
    }

    public static AudioPlayMode getByTypeName(int type){
        switch (type){
            case 0:
                return MEGAPHONE;
            case 1:
                return RECEIVER;
        }
        return UNDEFINE;
    }
}
