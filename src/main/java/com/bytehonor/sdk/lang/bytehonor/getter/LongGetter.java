package com.bytehonor.sdk.lang.bytehonor.getter;

import com.bytehonor.sdk.define.bytehonor.error.ParameterExcption;
import com.bytehonor.sdk.lang.bytehonor.string.StringCreator;
import com.bytehonor.sdk.lang.bytehonor.string.StringObject;

/**
 * @author lijianqiang
 *
 */
public class LongGetter {

    public static Long parse(String src) {
        try {
            return Long.valueOf(src);
        } catch (Exception e) {
            throw new ParameterExcption(StringCreator.create().append(src).append(" is not number ").toString());
        }
    }

    public static Long optional(String src) {
        return optional(src, null);
    }

    public static Long optional(String src, Long def) {
        if (StringObject.isEmpty(src)) {
            return def;
        }
        try {
            return parse(src);
        } catch (Exception e) {
            return def;
        }
    }

    public static Long optional(Long val, Long def) {
        return val != null ? val : def;
    }
}
