package model.workModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import event.MyEvent;

import java.util.ArrayList;
import java.util.List;

public class WorkItem {
    public static final String  EVENT = "EVENT";
    public static final String NEXT_WORK = "nextWork";
    private MyEvent myEvent;
    private List<WorkItem> nextWorks;
    public WorkItem(MyEvent myEvent) {
        this.myEvent = myEvent;
        nextWorks = new ArrayList<>();
    }
    public void addNextWork(WorkItem workItem){
        nextWorks.add(workItem);
    }

    public MyEvent getMyEvent() {
        return myEvent;
    }

    public List<WorkItem> getNextWorks() {
        return nextWorks;
    }
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT,myEvent.toJSONObject());

        JSONArray nextWorkJSONArray = new JSONArray();
        for(WorkItem nextWorkItem:nextWorks){
            JSONObject nextWorkJSONObject = nextWorkItem.toJSONObject();
            nextWorkJSONArray.add(nextWorkJSONObject);
        }
        jsonObject.put(NEXT_WORK,nextWorkJSONArray);
        return jsonObject;
    }
}
