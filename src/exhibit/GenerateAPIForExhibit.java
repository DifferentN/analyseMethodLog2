package exhibit;

import FileUtil.MyFileUtil;
import analyseLog.MethodSequenceUtil;
import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONObject;
import event.MyEvent;
import event.MyParameter;
import model.workModel.WorkItem;
import start.GenerateMyEventUtil;
import util.MatchUtil;

import java.util.ArrayList;
import java.util.List;

public class GenerateAPIForExhibit {
    private List<String[]> userInputs;
    private List<String> pathList;
    public GenerateAPIForExhibit(){
        userInputs = new ArrayList<>();
        pathList = new ArrayList<>();
    }
    public void addInstanceInfo(String path,String input,String output){
        pathList.add(path);
        if(input!=null) {
            String str[] = input.split("/");
            for (String item : str) {
                String params[] = item.split(":");
                userInputs.add(params);
            }
        }
    }
    public String executeGenerateAPI(){
        String targetPath = "execute.json";
        WorkItem workFlow = null;
        for(String path:pathList){
            List<MyMethod> methodSeq = MethodSequenceUtil.getSequence(path);
            List<MyEvent> eventSeq = GenerateMyEventUtil.generateMyEventList(methodSeq);
            if(workFlow == null){
                workFlow = createWorkFlow(eventSeq);
            }else{
                updateWorkFlow(workFlow,eventSeq);
            }
        }
        assignUserInputTag(workFlow,userInputs);
        JSONObject workFlowJSON = workFlow.toJSONObject();
        MyFileUtil.writeJSONObject(targetPath,workFlowJSON);
        return targetPath;
    }

    public static void addAPILink(String path,String apiName,String paramName){
        JSONObject workFlow = MyFileUtil.readJSONObject(path);
        String targetPath = "C:/Users/17916/Desktop/"+apiName+".json";
        MyFileUtil.writeJSONObject(targetPath,workFlow);
    }

    private static void assignUserInputTag(WorkItem workItem,List<String[]> tagValues){
        if(workItem == null){
            return ;
        }
        MyEvent curEvent = workItem.getMyEvent();
        if(curEvent.getMethodName().equals(MyEvent.SETTEXT)){
            for(MyParameter myParameter:curEvent.getParameters()){
                //在第一个用户实例中寻找标签
                for(String item[]:tagValues){
                    //用户给的值相同于用户实例中的值相同，分配标签
//                    System.out.println(myParameter.value);
                    if(item[1].equals(myParameter.value)){
                        myParameter.type = item[0];
                        break;
                    }
                }
            }
        }

        //给剩下的工作流分配标签
        List<WorkItem> nextWorkItems = workItem.getNextWorks();
        for(WorkItem nextWorkItem:nextWorkItems){
            assignUserInputTag(nextWorkItem,tagValues);
        }
    }

    public static WorkItem createWorkFlow(List<MyEvent> eventList){
        if(eventList==null||eventList.size()==0){
            return null;
        }
        MyEvent myEvent = eventList.get(0);
        WorkItem head = new WorkItem(myEvent);
        int pos =1;
        WorkItem pre = head;
        for(;pos<eventList.size();pos++){
            myEvent = eventList.get(pos);
            WorkItem workItem = new WorkItem(myEvent);
            pre.addNextWork(workItem);
            pre = workItem;
        }
        return head;
    }
    /**
     * update workFlow by eventList
     * @param head
     * @param eventList
     */
    public static void updateWorkFlow(WorkItem head, List<MyEvent> eventList){
        if(eventList==null){
            return;
        }
        checkWorkItem(head,eventList,0);
    }

    /**
     * 检查curWorkItem是否与eventList的pos位置的元素相同
     * @param curWorkItem
     * @param eventList
     * @param pos
     * @return
     */
    private static boolean checkWorkItem(WorkItem curWorkItem,List<MyEvent> eventList,int pos){
        if(eventList==null||pos>=eventList.size()){
            return true;
        }
        MyEvent myEvent = eventList.get(pos);
        MyEvent curEvent = curWorkItem.getMyEvent();
        if( MatchUtil.obtainMyEventSimilarity(myEvent,curEvent) == MatchUtil.NOT_SAME_STATE ){
            System.out.println("not Same");
            return false;
        }
        List<WorkItem> nextWorkItems = curWorkItem.getNextWorks();
        boolean flag = false;
        for(WorkItem nextWorkItem:nextWorkItems){
            if( checkWorkItem(nextWorkItem,eventList,pos+1) ){
                //pos+1位置的任务元素与接下来的某个任务相同不需要添加
                flag = true;
                break;
            }
        }

        //add left part eventList to workFlow
        if(!flag){
            List<MyEvent> leftEvents = eventList.subList(pos+1,eventList.size());
            if( leftEvents!=null && leftEvents.size()>0 ){
                addEventListToWorkFlow(curWorkItem,leftEvents);
            }
        }
        return true;
    }

    /**
     * 在工作流中添加新的操作事件序列
     * @param workItem
     * @param eventList
     * @return
     */
    private static void addEventListToWorkFlow(WorkItem workItem, List<MyEvent> eventList){
        WorkItem nextWorkItem =  createWorkFlow(eventList);
        workItem.addNextWork(nextWorkItem);
    }
}
