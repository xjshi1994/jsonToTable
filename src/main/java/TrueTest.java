import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;

public class TrueTest {
    public static void main(String[] args) {
        String[] test = {"a.c","a", "a.c.b", "a.d", "b.e", "b.e.f"};
        List<String> list = Arrays.asList(test);
        Collections.sort(list);
        for (String str : list) {
            System.out.println(str);
        }

        List<Integer> l1 = new LinkedList<>();
        List<Integer> l2 = new LinkedList<>();
        l1.add(1);
        l1.add(1);
        l1.add(2);
        l1.add(2);
        l1.addAll(l1);
        System.out.println(l1);
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
