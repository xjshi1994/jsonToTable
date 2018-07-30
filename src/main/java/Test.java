import com.github.wnameless.json.flattener.JsonFlattener;
import java.io.*;
import java.util.*;

public class Test {
    public int totalRow = 0;

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
                grouped.put(firstKey, new HashMap<>());
            }

            // second level map
            Map<String, Map<String, Object>> secondMap = grouped.get(firstKey);
            String secondKey = removeLast(entry.getKey());

            if (!secondMap.containsKey(secondKey)) {
                secondMap.put(secondKey, new HashMap<>());
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
