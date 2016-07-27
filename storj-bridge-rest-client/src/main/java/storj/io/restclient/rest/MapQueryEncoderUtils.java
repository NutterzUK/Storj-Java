package storj.io.restclient.rest;

import java.util.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MapQueryEncoderUtils {

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder("?");
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 1) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }

        return sb.toString();
    }
}