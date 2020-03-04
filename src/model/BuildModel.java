package model;

import FileUtil.MyFileWriter;
import analyseLog.MethodSequenceUtil;
import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONArray;
import event.MyEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuildModel {
    public static void main(String args[]){
        List<MyMethod> callSeq = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/test/methodLog.txt");
//        for(MyMethod myMethod:callSeq){
//            if(myMethod.methodName!=null){
//                System.out.println(myMethod.methodName);
//            }else{
//                System.out.println("animator");
//            }
//        }
        List<MyEvent> events = GenerateMyEventUtil.generateMyEventList(callSeq);
        JSONArray jsonArray = new JSONArray();
        for(MyEvent myEvent:events){
            jsonArray.add(myEvent.toJSONObject());
        }
        MyFileWriter.writeEventJSONArray("execute.txt",jsonArray);
    }
}
