import org.apache.commons.lang3.StringEscapeUtils;

public class TrueTest {
    public static void main(String[] args) {
        String test = "a[0].a[1]";
        System.out.println(rmOutBracket(test));
    }

    public static String rmOutBracket(String str) {
        return str.replaceAll("\\[|\\]", "");
    }


    public static String getLastField(String str) {
        int lastIndex = str.lastIndexOf(".");
        if (lastIndex != -1) {
            return str.substring(lastIndex + 1,str.length());
        }
        return str;
    }
}
