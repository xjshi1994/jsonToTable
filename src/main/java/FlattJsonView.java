import com.github.wnameless.json.flattener.JsonFlattener;

import java.util.*;

public class FlattJsonView {
    public static void main(String[] args) {
        String json = "{\n" +
                "\t\"finalDecision\": \"REVIEW\",\n" +
                "\t\"finalScore\": 20,\n" +
                "\t\"message\": \"查询成功\",\n" +
                "\t\"riskItems\": [{\n" +
                "\t\t\"decision\": \"Accept\",\n" +
                "\t\t\"riskDetail\": [{\n" +
                "\t\t\t\"description\": \"身份证命中中风险关注名单\",\n" +
                "\t\t\t\"fraudTypeDisplayName\": \"异常借款\",\n" +
                "\t\t\t\"greyListDetails\": [{\n" +
                "\t\t\t\t\"evidenceTime\": 1506246327000,\n" +
                "\t\t\t\t\"fraudType\": \"suspiciousLoan\",\n" +
                "\t\t\t\t\"fraudTypeDisplayName\": \"异常借款\",\n" +
                "\t\t\t\t\"riskLevel\": \"中\",\n" +
                "\t\t\t\t\"value\": \"362201198509090027\"\n" +
                "\t\t\t}],\n" +
                "\t\t\t\"hitTypeDisplayName\": \"借款人身份证\",\n" +
                "\t\t\t\"type\": \"grey_list\"\n" +
                "\t\t}],\n" +
                "\t\t\"riskName\": \"身份证命中中风险关注名单\",\n" +
                "\t\t\"ruleId\": 12673553,\n" +
                "\t\t\"score\": 10\n" +
                "\t}, {\n" +
                "\t\t\"decision\": \"Accept\",\n" +
                "\t\t\"riskDetail\": [{\n" +
                "\t\t\t\"description\": \"手机号命中中风险关注名单\",\n" +
                "\t\t\t\"fraudTypeDisplayName\": \"异常借款\",\n" +
                "\t\t\t\"greyListDetails\": [{\n" +
                "\t\t\t\t\"evidenceTime\": 1506246327000,\n" +
                "\t\t\t\t\"fraudType\": \"suspiciousLoan\",\n" +
                "\t\t\t\t\"fraudTypeDisplayName\": \"异常借款\",\n" +
                "\t\t\t\t\"riskLevel\": \"中\",\n" +
                "\t\t\t\t\"value\": \"18311359005\"\n" +
                "\t\t\t}],\n" +
                "\t\t\t\"hitTypeDisplayName\": \"借款人手机\",\n" +
                "\t\t\t\"type\": \"grey_list\"\n" +
                "\t\t}],\n" +
                "\t\t\"riskName\": \"手机号命中中风险关注名单\",\n" +
                "\t\t\"ruleId\": 12673743,\n" +
                "\t\t\"score\": 10\n" +
                "\t}],\n" +
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
