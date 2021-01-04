package start;

import FileUtil.MyFileUtil;
import FileUtil.MyFileWriter;
import analyseLog.MethodSequenceUtil;
import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import event.MyEvent;
import event.MyParameter;

import java.util.*;

public class BuildModel {
    public static void main(String args[]){
        List<MyMethod> callSeq = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/newVersionAPI/shipudaquan/searchFood/methodLog.txt");
        System.out.println("start generate API");
        List<MyEvent> events = GenerateMyEventUtil.generateMyEventList(callSeq);
        System.out.println("end generate API");

        String userInput[] = new String[]{"食物名称","油条"};
        List<String[]> userInputList = new ArrayList<>();
        userInputList.add(userInput);
        assignUserInputTag(userInputList,events);

        JSONArray jsonArray = new JSONArray();
        for(MyEvent myEvent:events){
            jsonArray.add(myEvent.toJSONObject());
        }
        generateApi(jsonArray,"searchFood");
    }
    private static void assignUserInputTag(List<String[]> tagValues,List<MyEvent> events){
        for(MyEvent event:events){
            if(event.getMethodName().equals(MyEvent.SETTEXT)){
                //给输入参数分配标签
                for(MyParameter myParameter:event.getParameters()){
                    //在第一个用户实例中寻找标签
                    for(String item[]:tagValues){
                        //用户给的值相同于用户实例中的值相同，分配标签
                        if(item[1].equals(myParameter.value)){
                            myParameter.type = item[0];
                            break;
                        }
                    }
                }
            }
        }
    }
    private static void generateApi(JSONArray jsonArray,String link,String... params){
        String pathName = link+".txt";
        //保存API模板和API输出
        JSONObject apiObject = new JSONObject();
        apiObject.put("API_MODEL",jsonArray);

        //保存筛选过的API输出
        JSONObject pageResult = new JSONObject();
        JSONObject pageJSON = MyFileUtil.readJSONObject("C:/Users/17916/Desktop/test/pageContent.txt");
        Set<String> keys = pageJSON.keySet();
        for(String key:keys){
            for(String param:params){
                if(pageJSON.getString(key).equals(param)){
                    pageResult.put(key,"");
                    break;
                }
            }
        }
        apiObject.put("API_OUTPUT",pageResult);
        MyFileUtil.writeJSONObject(pathName,apiObject);
    }

    /**
     * * 用来显示日志
     * @param callSeq
     */
    private static void temporaryWriteEvent(List<MyMethod> callSeq){
        JSONArray jsonArray = new JSONArray();
        for(MyMethod myMethod:callSeq){
            if(myMethod.methodName!=null){
                jsonArray.add(getMyMethodJSON(myMethod));
            }else{
                JSONObject jsonObject = (JSONObject) myMethod.selfJson.clone();
                jsonObject.put("methodName","animator");
                jsonArray.add(jsonObject);
            }
        }
        MyFileWriter.writeEventJSONArray("log.txt",jsonArray);
    }

    /**
     *  得到子方法调用序列
     * @param myMethod
     * @return 子方法组成的JSONArray
     */
    private static JSONArray getChildMethodJSONArray(MyMethod myMethod){
        JSONArray jsonArray = new JSONArray();
        for(MyMethod child:myMethod.childs){
            jsonArray.add(getMyMethodJSON(child));
        }
        return jsonArray;
    }

    /**
     *
     * @param myMethod
     * @return 代表当前方法的JSONObject
     */
    private static JSONObject getMyMethodJSON(MyMethod myMethod){
        JSONObject jsonObject = (JSONObject) myMethod.selfJson.clone();
        JSONArray childArray = getChildMethodJSONArray(myMethod);
        jsonObject.put("childs",childArray);
        return jsonObject;

    }
}
