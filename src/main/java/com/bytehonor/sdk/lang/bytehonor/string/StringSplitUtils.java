package com.bytehonor.sdk.lang.bytehonor.string;

import java.util.ArrayList;
import java.util.List;

import com.bytehonor.sdk.lang.bytehonor.constant.CharConstants;

public class StringSplitUtils {

    /**
     * 逗号分隔，中英逗号均支持
     * 
     * @param src
     * @return
     */
    public static List<String> split(String src) {
        if (StringObject.isEmpty(src)) {
            return new ArrayList<String>();
        }
        int length = src.length();
        char[] chars = src.toCharArray();
        char[] targets = new char[length * 2];
        for (int i = 0; i < length; i++) {
            if (chars[i] == ',' || chars[i] == '，') {
                targets[i] = CharConstants.BLANK;
            } else {
                targets[i] = chars[i];
            }
        }
        return splitWithBlank(new String(targets, 0, length));
    }

    public static List<String> splitWithBlank(String src) {
        return split(src, CharConstants.BLANK);
    }

    public static List<String> split(String src, char sp) {
        if (StringObject.isEmpty(src)) {
            return new ArrayList<String>();
        }
        int length = src.length();
        List<String> res = new ArrayList<String>(length);
        int begin = 0;
        int count = 0;
        boolean findOne = false;
        boolean beginNewOne = true;
        char[] charArray = src.toCharArray();
        for (int i = 0; i < length; i++) {
            if (sp != charArray[i]) {
                if (beginNewOne) {
                    begin = i;
                    beginNewOne = false;
                }
                count++;
                findOne = false;
            } else {
                findOne = true;
            }

            if (findOne && count > 0) {
                res.add(new String(charArray, begin, count));
                count = 0;
                beginNewOne = true;
            }
        }

        if (count > 0) {
            res.add(new String(charArray, begin, count));
        }

        return res;
    }
}
