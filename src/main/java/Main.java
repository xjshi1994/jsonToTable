import com.github.wnameless.json.flattener.JsonFlattener;

import java.util.List;
import java.util.Map;

public class Main {

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
        Test test = new Test();
        Map<String, Map<String, Map<String, Object>>> grouped = test.getThreeMap(flattenJson);
        test.getTotalRows(grouped);
        System.out.println(grouped);
        System.out.println(test.totalRow);
        Map<String, List<Object>> result = test.insertData(grouped);
        test.tranverse(result);
        System.out.println(test.getFinalSQL(result,"test2"));
    }
}
