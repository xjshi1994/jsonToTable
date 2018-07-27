import com.github.wnameless.json.flattener.JsonFlattener;
import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.util.*;

import static java.lang.System.in;
import static java.lang.System.setOut;

public class Test {
    public int totalRow = 0;
    public boolean flag = true;
    public List<String> typeList;

    public Map<String, Map<String, Map<String, Object>>> getThreeMap(Map<String, Object> flattenJson) {
        Map<String, Map<String, Map<String, Object>>> grouped = new HashMap<>();

        for (Map.Entry<String, Object> entry : flattenJson.entrySet()) {
            String firstKey = null;
            try {
                firstKey = getPreForKey(entry.getKey());
            } catch (Exception e) {
                firstKey = "base";
            }
            if (!grouped.containsKey(firstKey)) {
                grouped.put(firstKey, new HashMap<String, Map<String, Object>>());
            }

            // second level map
            Map<String, Map<String, Object>> secondMap = grouped.get(firstKey);
            String secondKey = removeLast(entry.getKey());

            if (!secondMap.containsKey(secondKey)) {
                secondMap.put(secondKey, new HashMap<String, Object>());
            }

            // third level map
            String thirdKey = getLastField(entry.getKey());
            Map<String, Object> thirdMap = secondMap.get(secondKey);
            thirdMap.put(thirdKey, entry.getValue());
        }

        // after get three map, we can get totalRows.
        getTotalRows(grouped);
        return grouped;
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
            return str.substring(0, lastIndex);
        }


        return str;
    }


    public static String rmOutBracket(String str) {
        return str.replaceAll("\\[|\\]", "");
    }

    public static String getPreForKey(String str) throws Exception {
        if (str.equals(removeLast(str))) {
            throw new Exception();
        }
        return removeBracket(removeLast(str));
    }

    public static String getLastField(String str) {
        int lastIndex = str.lastIndexOf(".");
        if (lastIndex != -1) {
            return str.substring(lastIndex + 1, str.length());
        }
        return str;
    }

    public static String getTableSchema(Map<String, Map<String, Map<String, Object>>> group) {
        String result = "";
        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : group.entrySet()) {
            Map<String, Map<String, Object>> secondMap = entry.getValue();
            for (Map.Entry<String, Map<String, Object>> secondEntry : secondMap.entrySet()) {
                if (entry.getKey().equals("base")) {
                    result = result + " " + secondEntry.getKey();
                } else {
                    Map<String, Object> thirdMap = secondEntry.getValue();
                    for (Map.Entry<String, Object> thirdEntry : thirdMap.entrySet())
                        result = result + " " + entry.getKey() + "." + thirdEntry.getKey();
                }
            }
        }
        return result;
    }

    public static String getColumn(String entryKey, String thirdEntryKey) {
        if (entryKey.equals("base")) {
            // *****************revise here
            // it is array but there is no field in it, so take it as base!
            return rmOutBracket(thirdEntryKey);
        } else {
            return entryKey + "." + thirdEntryKey;
        }
    }

    public void getTotalRows(Map<String, Map<String, Map<String, Object>>> group) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : group.entrySet()) {
            if (!entry.getKey().equals("base")) {
                list.add(entry.getKey());
            }
        }
        Collections.sort(list);
        int num = 0;
        String prev = "";
        String cur = "";
        List<Integer> numList = new LinkedList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            cur = list.get(i);
            if (removeLast(prev).equals(cur)) {
                prev = cur;
                continue;
            } else {
                num = group.get(cur).size();
                numList.add(num);
            }
            prev = cur;
        }
        int result = 1;
        for (int i = 0; i < numList.size(); i++) {
            result = result * numList.get(i);
        }
        totalRow = result;
    }


/*    public Map<String, List<Object>> insertData(Map<String, Map<String, Map<String, Object>>> group) {
        Map<String, List<Object>> result = new HashMap<>();
        int partLevel = 1;
        int wholeLevel = 0;
        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : group.entrySet()) {
            Map<String, Map<String, Object>> secondMap = entry.getValue();
            // base item
            if (entry.getKey().equals("base")) {
                for (Map.Entry<String, Map<String, Object>> secondEntry : secondMap.entrySet()) {
                    Map<String, Object> thirdMap = secondEntry.getValue();
                    for (Map.Entry<String, Object> thirdEntry : thirdMap.entrySet()) {
                        String column = getColumn(entry.getKey(), thirdEntry.getKey());
                        List<Object> sublist = new ArrayList<>();
                        for (int i = 0; i < totalRow; i++) {
                            sublist.add(thirdEntry.getValue());
                        }
                        result.put(column, sublist);
                    }
                }
            } else {
                // nested array
                wholeLevel = totalRow / (partLevel * secondMap.size());
                for (int i = 0; i < wholeLevel; i++) {
                    for (Map.Entry<String, Map<String, Object>> secondEntry : secondMap.entrySet()) {
                        Map<String, Object> thirdMap = secondEntry.getValue();
                        for (Map.Entry<String, Object> thirdEntry : thirdMap.entrySet()) {
                            for (int j = 0; j < partLevel; j++) {
                                // get column
                                System.out.println("***haha***"+entry.getKey());
                                String column = getColumn(entry.getKey(), thirdEntry.getKey());
                                // not exists , new one
                                if (!result.containsKey(column)) {
                                    result.put(column, new ArrayList<>());
                                }
                                result.get(column).add(thirdEntry.getValue());
                            }
                        }
                    }
                }
                partLevel = partLevel * secondMap.size();
            }
        }
        return result;
    }*/


    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream("src/main/resources/json.properties");
             InputStreamReader ir = new InputStreamReader(in, "UTF-8")) {
            properties.load(ir);
            String json = properties.getProperty("json");
            System.out.println(json);
            String json1 = "{\n" +
                    "\t\"cisReport\": [{\n" +
                    "\t\t\"buildEndTime\": \"2018-06-08 11:53:41\",\n" +
                    "\t\t\"creditBehaviorInfo\": {\n" +
                    "\t\t\t\"avgCredits\": 0,\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"last12MthsLoanCnt\": 0,\n" +
                    "\t\t\t\"last1MthsLoanCnt\": 0,\n" +
                    "\t\t\t\"last3MthsLoanCnt\": 0,\n" +
                    "\t\t\t\"last6MthsLoanCnt\": 0,\n" +
                    "\t\t\t\"loanClosedCnt\": 0,\n" +
                    "\t\t\t\"loanNoClosedCnt\": 0,\n" +
                    "\t\t\t\"loanOrderCnt\": 0,\n" +
                    "\t\t\t\"loanOrgCnt\": 0,\n" +
                    "\t\t\t\"subReportType\": \"14238\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"treatResult\": \"1\",\n" +
                    "\t\t\t\"undefinedCnt\": 0\n" +
                    "\t\t},\n" +
                    "\t\t\"econnoisserurInfo\": {\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"state\": \"0\",\n" +
                    "\t\t\t\"subReportType\": \"14236\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"treatResult\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t\"fraudRiskInfo\": {\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"state\": \"0\",\n" +
                    "\t\t\t\"subReportType\": \"14237\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"treatResult\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t\"hasSystemError\": false,\n" +
                    "\t\t\"historySimpleQueryInfo\": {\n" +
                    "\t\t\t\"count\": {\n" +
                    "\t\t\t\t\"last12Month\": 3,\n" +
                    "\t\t\t\t\"last18Month\": 4,\n" +
                    "\t\t\t\t\"last1Month\": 2,\n" +
                    "\t\t\t\t\"last24Month\": 4,\n" +
                    "\t\t\t\t\"last3Month\": 3,\n" +
                    "\t\t\t\t\"last6Month\": 3\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"items\": [{\n" +
                    "\t\t\t\t\"last12Month\": 1,\n" +
                    "\t\t\t\t\"last18Month\": 2,\n" +
                    "\t\t\t\t\"last1Month\": 1,\n" +
                    "\t\t\t\t\"last24Month\": 2,\n" +
                    "\t\t\t\t\"last3Month\": 1,\n" +
                    "\t\t\t\t\"last6Month\": 1,\n" +
                    "\t\t\t\t\"unitMember\": \"商业银行\"\n" +
                    "\t\t\t}, {\n" +
                    "\t\t\t\t\"last12Month\": 1,\n" +
                    "\t\t\t\t\"last18Month\": 1,\n" +
                    "\t\t\t\t\"last1Month\": 1,\n" +
                    "\t\t\t\t\"last24Month\": 1,\n" +
                    "\t\t\t\t\"last3Month\": 1,\n" +
                    "\t\t\t\t\"last6Month\": 1,\n" +
                    "\t\t\t\t\"unitMember\": \"融资租赁及担保类公司\"\n" +
                    "\t\t\t}, {\n" +
                    "\t\t\t\t\"last12Month\": 1,\n" +
                    "\t\t\t\t\"last18Month\": 1,\n" +
                    "\t\t\t\t\"last1Month\": 0,\n" +
                    "\t\t\t\t\"last24Month\": 1,\n" +
                    "\t\t\t\t\"last3Month\": 1,\n" +
                    "\t\t\t\t\"last6Month\": 1,\n" +
                    "\t\t\t\t\"unitMember\": \"汽车金融公司\"\n" +
                    "\t\t\t}],\n" +
                    "\t\t\t\"subReportType\": \"19902\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"suspectedBulllending\": {\n" +
                    "\t\t\t\t\"applyFinclCnt\": 0,\n" +
                    "\t\t\t\t\"applyNetLoanCnt\": 0,\n" +
                    "\t\t\t\t\"appplyCnt\": 0\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"treatResult\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t\"isFrozen\": false,\n" +
                    "\t\t\"overdueLoanInfo\": {\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"subReportType\": \"14239\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"treatResult\": \"2\"\n" +
                    "\t\t},\n" +
                    "\t\t\"personAntiSpoofingDescInfo\": {\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"personAntiSpoofingDesc\": \"1、反欺诈风险评分为20分，风险等级为低，建议通过。\\n2、未命中羊毛党名单。\\n3、未命中欺诈风险名单。\\n4、未检测到信贷行为。\\n5、在近两年被机构查询过4次个人信息。\",\n" +
                    "\t\t\t\"subReportType\": \"14242\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"treatResult\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t\"personAntiSpoofingInfo\": {\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"hitTypes\": \"被机构查询信息\",\n" +
                    "\t\t\t\"riskLevel\": \"低\",\n" +
                    "\t\t\t\"riskScore\": 20,\n" +
                    "\t\t\t\"subReportType\": \"14241\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"suggest\": \"建议通过\",\n" +
                    "\t\t\t\"treatResult\": \"1\"\n" +
                    "\t\t},\n" +
                    "\t\t\"personRiskInfo\": {\n" +
                    "\t\t\t\"errorMessage\": \"\",\n" +
                    "\t\t\t\"subReportType\": \"14227\",\n" +
                    "\t\t\t\"subReportTypeCost\": \"96043\",\n" +
                    "\t\t\t\"treatResult\": \"2\"\n" +
                    "\t\t},\n" +
                    "\t\t\"queryConditions\": [{\n" +
                    "\t\t\t\"caption\": \"被查询者姓名\",\n" +
                    "\t\t\t\"name\": \"name\",\n" +
                    "\t\t\t\"value\": \"温程\"\n" +
                    "\t\t}, {\n" +
                    "\t\t\t\"caption\": \"被查询者证件号码\",\n" +
                    "\t\t\t\"name\": \"documentNo\",\n" +
                    "\t\t\t\"value\": \"45070219861116513X\"\n" +
                    "\t\t}, {\n" +
                    "\t\t\t\"caption\": \"手机号码\",\n" +
                    "\t\t\t\"name\": \"phone\",\n" +
                    "\t\t\t\"value\": \"18521515392\"\n" +
                    "\t\t}],\n" +
                    "\t\t\"queryReasonID\": \"1\",\n" +
                    "\t\t\"reportID\": \"2018060811500223\",\n" +
                    "\t\t\"subReportTypes\": \"96043\",\n" +
                    "\t\t\"subReportTypesShortCaption\": \"1、个人反欺诈分析报告（96043）\",\n" +
                    "\t\t\"treatResult\": 1\n" +
                    "\t}],\n" +
                    "\t\"message\": \"查询成功\",\n" +
                    "\t\"status\": \"OK\"\n" +
                    "}";
            Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(json1);
            Test test = new Test();
            Map<String, Map<String, Map<String, Object>>> grouped = test.getThreeMap(flattenJson);
            System.out.println(test.totalRow);
            Map<String, List<Object>> dataframe = test.insertData(grouped);
            System.out.println(test.getFinalSQL(dataframe, "haha1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // todo
    public Map<String, List<Object>> insertData(Map<String, Map<String, Map<String, Object>>> group) {
        List<Map<String, List<Object>>> subResult = new LinkedList<>();

        Map<String, List<Object>> result = new HashMap<>();

        List<String> deepest = getDeepest(group);
        // for every deepest key to add the subResult;
        for (String str : deepest) {
            if (getSubMap(str, group) != null) {
                subResult.add(getSubMap(str, group));
            }
        }

        System.out.println("sub"+subResult.size());

        result = getFinalMap(subResult, group);

        return result;
    }

    public Map<String, List<Object>> getFinalMap(List<Map<String, List<Object>>> subResult, Map<String, Map<String, Map<String, Object>>> group) {
        Map<String, List<Object>> finalMap = new HashMap<>();

        // insert base first
        if (group.get("base") != null) {
            Map<String, Map<String, Object>> secondMap = group.get("base");
            for (Map.Entry<String, Map<String, Object>> secondEntry : secondMap.entrySet()) {
                Map<String, Object> thirdMap = secondEntry.getValue();
                for (Map.Entry<String, Object> thirdEntry : thirdMap.entrySet()) {
                    String column = getColumn("base", thirdEntry.getKey());
                    List<Object> sublist = new ArrayList<>();
                    for (int i = 0; i < totalRow; i++) {
                        sublist.add(thirdEntry.getValue());
                    }
                    finalMap.put(column, sublist);
                }
            }
        }
        int partLevel = 1;
        int wholeLevel = 0;
        for (Map<String, List<Object>> map : subResult) {
            // revise
            int initSize = 0;
            for(List<Object> l: map.values()) {
                initSize = l.size();
            }
            System.out.println("initSize is " + initSize);
            System.out.println(totalRow);
            wholeLevel = totalRow / (partLevel * initSize);
            insertFinalMap(finalMap, map, wholeLevel, partLevel);
            partLevel = partLevel * initSize;
        }

        return finalMap;
    }

    public void insertFinalMap(Map<String, List<Object>> finalMap, Map<String, List<Object>> map, int wholeLevel, int partLevel) {
        for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
            String smallMapKey = entry.getKey();
            List<Object> smallMapList = entry.getValue();
            if (!finalMap.containsKey(smallMapKey)) {
                List<Object> list = new LinkedList<>();
                finalMap.put(smallMapKey, list);
            }

            // partLevel finished
            for (Object o : smallMapList) {
                for (int i = 0; i < partLevel; i++) {
                    finalMap.get(smallMapKey).add(o);
                }
            }

            List<Object> dup = finalMap.get(smallMapKey);
            for (int j = 0; j < wholeLevel - 1; j++) {
                finalMap.get(smallMapKey).addAll(dup);
            }
        }
    }

    public Map<String, List<Object>> getSubMap(String key1, Map<String, Map<String, Map<String, Object>>> group) {
        Map<String, List<Object>> subMap = new HashMap<>();
        // todo base
        if (key1.equals("base")) {
            return null;
        }
        Map<String, Map<String, Object>> secondMap = group.get(key1);
        for (Map.Entry<String, Map<String, Object>> secondEntry : secondMap.entrySet()) {
            String curKey = secondEntry.getKey();
            while (curKey != null) {
                Map<String, Object> thirdMap = getThirdMap(curKey, group);
                if (thirdMap == null) {
                    System.out.println("third map is null");
                    return null;
                }
                for (Map.Entry<String, Object> thirdEntry : thirdMap.entrySet()) {
                    String column = getColumn(removeBracket(curKey), thirdEntry.getKey());
                    if (!subMap.containsKey(column)) {
                        List<Object> subList = new LinkedList<>();
                        subList.add(thirdEntry.getValue());
                        subMap.put(column, subList);
                    } else {
                        subMap.get(column).add(thirdEntry.getValue());
                    }
                }
                if (curKey.contains(".")) {
                    curKey = removeLast(curKey);
                } else {
                    curKey = null;
                }
            }
        }
        return subMap;
    }

    public Map<String, Object> getThirdMap(String secondKey, Map<String, Map<String, Map<String, Object>>> group) {
        String firstKey = removeBracket(secondKey);
        return group.get(firstKey).get(secondKey);
    }


    public List<String> getDeepest(Map<String, Map<String, Map<String, Object>>> group) {
        List<String> deepest = new LinkedList<>();
        List<String> list = new ArrayList<>();

        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : group.entrySet()) {
            list.add(entry.getKey());
        }

        Collections.sort(list);

        String pre = "";

        for (int i = list.size() - 1; i >= 0; i--) {
            String cur = list.get(i);
            if (!removeLast(pre).equals(cur)) {
                deepest.add(cur);
            }
            pre = cur;
        }
        return deepest;
    }

    public void tranverse(Map<String, List<Object>> result) {

        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            System.out.print(entry.getKey() + " ");
            System.out.println(entry.getValue().get(0).getClass());
        }
        System.out.println();

        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            int k = 0;
            for (int i = 0; i < entry.getValue().size(); i++) {
                System.out.print(entry.getValue().get(i) + " ");
                k = i;
            }
            System.out.println("********" + k);
        }
    }

    public String getInnerSQL(Map<String, List<Object>> result) {
        String innerCreateSQL = "";
        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            String innerType = getLastField(entry.getValue().get(0).getClass().toString());
            if (innerType.equals("String")) {
                innerType = "varchar(1000)";
            } else if (innerType.equals("BigDecimal")) {
                innerType = "float";
            } else {

            }
            innerCreateSQL = innerCreateSQL + entry.getKey().replace(".", "_") + " " + innerType + ",";
        }
        innerCreateSQL = innerCreateSQL.substring(0, innerCreateSQL.length() - 1);
        return innerCreateSQL;
    }

    public String removeLastCommma(String str) {
        return str.substring(0, str.length() - 1);
    }

    public List<String> getInnerInsertSQL(Map<String, List<Object>> result, String tableName) {
        List<String> insertList = new LinkedList<>();

        String tableStructure = "";
        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            tableStructure = tableStructure + entry.getKey().replace(".", "_") + ",";
        }
        tableStructure = removeLastCommma(tableStructure);
        String insertPre = String.format("insert into %s (%s) value", tableName, tableStructure);

        for (int i = 0; i < totalRow; i++) {
            String temp = "";
            for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
                Object value = entry.getValue().get(i);
                if (getLastField(value.getClass().toString()).equals("String")) {
                    temp = String.format(temp + " \'%s\',", value);
                } else {
                    temp = temp + value + ",";
                }

            }
            temp = removeLastCommma(temp);
            insertList.add(String.format(insertPre + "(%s);", temp));
        }
        return insertList;
    }

    public String getCreateSQL(Map<String, List<Object>> result, String tableName) {
        String innerSQL = getInnerSQL(result);
        String createSQL = "create table " + tableName + "(" + innerSQL + ")CHARSET=utf8;";
        return createSQL;
    }

    public String getFinalSQL(Map<String, List<Object>> result, String tableName) {
        String createSQL = getCreateSQL(result, tableName);
        List<String> insertList = getInnerInsertSQL(result, tableName);
        String insertSQL = "";
        for (String str : insertList) {
            insertSQL = insertSQL + str;
        }
        return createSQL + insertSQL;

    }
}
