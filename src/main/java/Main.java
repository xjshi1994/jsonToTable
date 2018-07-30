import com.github.wnameless.json.flattener.JsonFlattener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream("src/main/resources/json.properties");
             InputStreamReader ir = new InputStreamReader(in, "UTF-8")) {
            properties.load(ir);
            String json = properties.getProperty("json");
            String json1 = "{\n" +
                    "    \"error\": 0,\n" +
                    "    \"status\": \"success\",\n" +
                    "    \"results\": [\n" +
                    "        {\n" +
                    "            \"currentCity\": \"青岛\",\n" +
                    "            \"index\": [\n" +
                    "                {\n" +
                    "                    \"title\": \"穿衣\",\n" +
                    "                    \"zs\": \"较冷\",\n" +
                    "                    \"tipt\": \"穿衣指数\",\n" +
                    "                    \"des\": \"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"title\": \"紫外线强度\",\n" +
                    "                    \"zs\": \"中等\",\n" +
                    "                    \"tipt\": \"紫外线强度指数\",\n" +
                    "                    \"des\": \"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。\"\n" +
                    "                }\n" +
                    "            ]\n" +
                    "\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(json1);
            Test test = new Test();
            Map<String, Map<String, Map<String, Object>>> grouped = test.getThreeMap(flattenJson);
            Map<String, List<Object>> dataframe = test.insertData(grouped);
            System.out.println(test.getFinalSQL(dataframe, "haha1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
