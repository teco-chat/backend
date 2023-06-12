package chat.teco.tecochat.common.util;

import java.util.Base64;

public class Base64Util {

    public static String decodeBase64(String encodedString) {
        byte[] names = Base64.getDecoder()
                .decode(encodedString);
        return new String(names).intern();
    }
}
