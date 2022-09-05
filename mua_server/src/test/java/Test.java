import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Locale;

/**
 * @author easemob_developer
 * @date 2022/5/26
 */
public class Test {

    public static void main(String... arguments){
//        UUID uuid = UUID.randomUUID();
//        System.out.println("uuid : "+uuid);

        for (int i = 0; i < 10000; i++) {
            String s1 = RandomUtil.randomString(32);
            System.out.println(s1);
        }

    }

    //生成8位随机字符串（重复概率1/218万亿）
    private static String generateShortUuid() {
        String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z" };
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 12; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }
}
