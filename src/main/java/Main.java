import com.github.wnameless.json.flattener.JsonFlattener;

import java.util.List;
import java.util.Map;

public class Main {

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

 /*       Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(json);
        Test test = new Test();
        Map<String, Map<String, Map<String, Object>>> grouped = test.getThreeMap(flattenJson);
        test.getTotalRows(grouped);
        System.out.println(test.totalRow);
        System.out.println(grouped);

        Map<String, List<Object>> result = test.insertData(grouped);
        test.tranverse(result);
        System.out.println(test.getFinalSQL(result,"test2"));*/
    }
}
