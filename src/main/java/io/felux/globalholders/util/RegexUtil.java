package io.felux.globalholders.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static String extract(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while (m.find())
            return m.group(0);

        return null;
    }
}
