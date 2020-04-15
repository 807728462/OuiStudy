package com.oyf.ookhttp;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:55
 * @描述
 **/
public class OHeaders {

    private Map<String, String> heads;

    public OHeaders(Builder builder) {
        this.heads = builder.heads;
    }

    public Builder newBuilder() {
        return new Builder();
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    public boolean isEmpty() {
        return heads.isEmpty();
    }

    public static final class Builder {

        private Map<String, String> heads;

        public Builder() {
            heads = new HashMap<>();
        }

        public Builder newBuilder() {
            return new Builder();
        }

        public OHeaders build() {
            return new OHeaders(this);
        }

        public void set(String key, String value) {
            heads.put(key, value);
        }

        public void add(String key, String value) {
            heads.put(key, value);
        }
    }
}
