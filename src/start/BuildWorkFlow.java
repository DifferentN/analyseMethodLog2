package start;

import FileUtil.MyFileUtil;
import analyseLog.MethodSequenceUtil;
import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONObject;
import event.MyEvent;
import event.MyParameter;
import model.workModel.WorkItem;
import util.MatchUtil;

import java.util.ArrayList;
import java.util.List;

public class BuildWorkFlow {
    public static void main(String args[]){
        System.out.println("start time: "+System.currentTimeMillis());
        List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/newVersionAPI/lanrentingshu/searchNovel/methodLog.txt");
//        List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/newVersionAPI/lanrentingshu/searchNovel/methodLog-2.txt");
//        List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/newVersionAPI/lanrentingshu/searchNovel/methodLog-3.txt");
//        System.out.println(callSeq1.size());
        List<MyEvent> events1 = GenerateMyEventUtil.generateMyEventList(callSeq1);
//        List<MyEvent> events2 = GenerateMyEventUtil.generateMyEventList(callSeq2);
//        List<MyEvent> events3 = GenerateMyEventUtil.generateMyEventList(callSeq3);

        System.out.println(events1.size());
//        System.out.println(events2.size());
        WorkItem workFlow = createWorkFlow(events1);
//        updateWorkFlow(workFlow,events2);
//        updateWorkFlow(workFlow,events3);

        String userInput1[] = new String[]{"小说名称","都市"};
//        String userInput2[] = new String[]{"小说名称","大钦服"};
        List<String[]> userInputList = new ArrayList<>();
        userInputList.add(userInput1);
//        userInputList.add(userInput2);

        assignUserInputTag(workFlow,userInputList);

        JSONObject workFlowJSON = workFlow.toJSONObject();
        MyFileUtil.writeJSONObject("searchNovel.txt",workFlowJSON);
        System.out.println("end time: "+System.currentTimeMillis());
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
