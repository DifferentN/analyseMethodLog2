package exhibit;

import FileUtil.MyFileUtil;
import FileUtil.MyFileWriter;
import analyseLog.MethodSequenceUtil;
import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import event.MyEvent;
import start.GenerateMyEventUtil;

import java.util.List;

public class ShowInfo {
    public static void transformEventFile(String srcPath,String targetPath){
        List<MyMethod> methodSeq = MethodSequenceUtil.getSequence(srcPath);
        List<MyEvent> eventSeq = GenerateMyEventUtil.generateMyEventList(methodSeq);
        JSONArray jsonArray = new JSONArray();
        for(MyEvent event: eventSeq){
             JSONObject eventJSON = new JSONObject();
             eventJSON.put(MyEvent.ACTIVITY_ID,event.getActivityId());
             eventJSON.put(MyEvent.METHOD_NAME,event.getMethodName());
             jsonArray.add(eventJSON);
        }
        MyFileWriter.writeEventJSONArray(targetPath,jsonArray);
    }
}
