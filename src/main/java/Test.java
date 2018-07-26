import com.github.wnameless.json.flattener.JsonFlattener;

import java.util.*;

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

        // *****************revise here
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
            // it is array but there is no field in it, so take it as base!
            return rmOutBracket(thirdEntryKey);
        } else {
            return entryKey + "." + thirdEntryKey;
        }
    }

    public void getTotalRows(Map<String, Map<String, Map<String, Object>>> group) {
        // Except "base" in the first map, multiply nums of key in each group in second map.
        List<Integer> numOfGroup = new LinkedList<>();
        for (Map.Entry<String, Map<String, Map<String, Object>>> entry : group.entrySet()) {
            if (!entry.getKey().equals("base")) {
                numOfGroup.add(entry.getValue().size());
            }
        }
        int result = 1;
        for (int i = 0; i < numOfGroup.size(); i++) {
            result = result * numOfGroup.get(i);
        }
        totalRow = result;
    }


    public Map<String, List<Object>> insertData(Map<String, Map<String, Map<String, Object>>> group) {
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
    }

    public void tranverse(Map<String, List<Object>> result) {

        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            System.out.print(entry.getKey() + " ");
            System.out.println(entry.getValue().get(0).getClass());
        }
        System.out.println();

        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            int k = 0;
            for (int i =0; i < entry.getValue().size();i++) {
                System.out.print(entry.getValue().get(i) + " ");
                k=i;
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
        return str.substring(0, str.length() -1 );
    }

    public List<String> getInnerInsertSQL(Map<String, List<Object>> result, String tableName) {
        List<String> insertList = new LinkedList<>();

        String tableStructure = "";
        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            tableStructure = tableStructure + entry.getKey().replace(".", "_") + ",";
        }
        tableStructure = removeLastCommma(tableStructure);
        String insertPre = String.format("insert into %s (%s) value", tableName,tableStructure);

        for (int i = 0; i < totalRow; i++) {
            String temp = "";
            for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
                Object value = entry.getValue().get(i);
                if (getLastField(value.getClass().toString()).equals("String")) {
                    temp = String.format(temp + " \'%s\',",value);
                }else {
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
    public String getFinalSQL (Map<String, List<Object>> result, String tableName) {
        String createSQL = getCreateSQL(result, tableName);
        List<String> insertList = getInnerInsertSQL(result, tableName);
        String insertSQL = "";
        for (String str : insertList) {
            insertSQL = insertSQL + str;
        }
        return createSQL + insertSQL;

    }

}
