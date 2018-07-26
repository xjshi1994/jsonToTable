import com.github.wnameless.json.flattener.JsonFlattener;

import java.util.*;

public class FlattJsonView {
    public static void main(String[] args) {
        String json = "{\n" +
                "\t\"cellphone\": \"18521515392\",\n" +
                "\t\"message\": \"查询成功\",\n" +
                "\t\"name\": \"温程\",\n" +
                "\t\"resultDesc\": [{\n" +
                "\t\t\"hitDetailId\": \"5b0673cb87e4300001636195\",\n" +
                "\t\t\"rules\": [{\n" +
                "\t\t\t\"detail\": \"未命中羊毛党名单\",\n" +
                "\t\t\t\"name\": \"命中羊毛党名单\",\n" +
                "\t\t\t\"result\": false,\n" +
                "\t\t\t\"ruleId\": \"pycredit_econnoisserur_state\"\n" +
                "\t\t}],\n" +
                "\t\t\"score\": 0,\n" +
                "\t\t\"source\": \"tongdun\",\n" +
                "\t\t\"sourceName\": \"同盾\",\n" +
                "\t\t\"suggest\": \"建议通过\"\n" +
                "\t}, {\n" +
                "\t\t\"hitDetailId\": \"5b0673ccd13e300001474d00\",\n" +
                "\t\t\"rules\": [{\n" +
                "\t\t\t\"detail\": \"未命中羊毛党名单\",\n" +
                "\t\t\t\"name\": \"命中羊毛党名单\",\n" +
                "\t\t\t\"result\": false,\n" +
                "\t\t\t\"ruleId\": \"pycredit_econnoisserur_state\"\n" +
                "\t\t}],\n" +
                "\t\t\"score\": 0,\n" +
                "\t\t\"source\": \"pycredit\",\n" +
                "\t\t\"sourceName\": \"鹏元\",\n" +
                "\t\t\"suggest\": \"建议通过\"\n" +
                "\t}],\n" +
                "\t\"source\": [\"tongdun\", \"pycredit\"],\n" +
                "\t\"ssn\": \"45070219861116513X\",\n" +
                "\t\"status\": \"OK\"\n" +
                "}";
        Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(json);
        String[] a = flattenJson.keySet().toArray(new String[0]);
        Map<String, List<String>> grouped = new HashMap<>();
        for(Map.Entry<String, Object> entry : flattenJson.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        // HOW TO GET TYPE
/*        for (Map.Entry<String, Object> entry : flattenJson.entrySet()) {
            System.out.println(entry.getValue().getClass());
        }*/

/*        int k = 0;
        for(Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            System.out.printf("This GROUP %d%n", k++);
            for(String i : entry.getValue()) {
                System.out.println(i);
            }
        }*/
    }
    // remove []
    public static String removeBracket(String str) {
        String result = "";
        result = str.replaceAll("\\[.*?\\]", "");
        return result;
    }

    public static String removeLast(String str) {
        int lastIndex = str.lastIndexOf(".");
        if (lastIndex != -1) {
            return str.substring(0,lastIndex);
        }
        return str;
    }

    public static String getPreForKey(String str) throws Exception {
        if (str.equals(removeLast(str))) {
            throw new Exception();
        }
        return removeBracket(removeLast(str));
    }
}
