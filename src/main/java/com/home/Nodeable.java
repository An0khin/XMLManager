package com.home;

import java.util.Collections;
import java.util.Map;

public interface Nodeable {
    Nodeable EMPTY_NODEABLE = new Nodeable() {
        @Override
        public Map<String, String> getValues() {
            return Collections.emptyMap();
        }

        @Override
        public String[] getId() {
            return new String[0];
        }
    };

    Map<String, String> getValues();

    String[] getId(); //{Tag, Value}
}
