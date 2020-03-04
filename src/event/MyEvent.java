package event;

import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyEvent {
    public static final String DISPATCH = "dispatchTouchEvent",SETTEXT = "setText";
    public static final String ACTIVITY_ID = "ActivityID";
    public static final String METHOD_NAME = "methodName";
    public static final String VIEW_PATH = "viewPath";
    public static final String VIEW_ID = "viewId";
    public static final String SNAPSHOT = "snapshot";
    public static final String PARAMETER_VALUE = "parameterValue";
    private String activityId,componentId,path;
    private String methodName;
    private List<MyParameter> parameters;
    private List<AnimatorNode> snapshot;
    public MyEvent(String activityId,String componentId,String path,String methodName){
        this.activityId = activityId;
        this.componentId = componentId;
        this.path = path;
        this.methodName = methodName;
        snapshot = new ArrayList<>();
    }

    public void setParameters(List<MyParameter> parameters) {
        this.parameters = parameters;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getPath() {
        return path;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<MyParameter> getParameters() {
        return parameters;
    }

    public boolean containsAnimatorView(String viewPath){
        for(AnimatorNode animatorNode:snapshot){
            if(viewPath.equals(animatorNode.getViewPath())){
                return true;
            }
        }
        return false;
    }
    public void addAnimatorVNode(String viewPath,String animatorType){
        for(AnimatorNode animatorNode:snapshot){
            if(viewPath.equals(animatorNode.getViewPath())){
                animatorNode.addAnimator(animatorType);
                return;
            }
        }
        AnimatorNode animatorNode = new AnimatorNode(viewPath);
        animatorNode.addAnimator(animatorType);
        snapshot.add(animatorNode);
    }
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ACTIVITY_ID,activityId);
        jsonObject.put(METHOD_NAME,methodName);
        jsonObject.put(VIEW_PATH,path);
        jsonObject.put(VIEW_ID,componentId);
        jsonObject.put(PARAMETER_VALUE,"");
        JSONArray animatorViews = new JSONArray();
        for(AnimatorNode animatorNode:snapshot){
            animatorViews.add(animatorNode.toJSONObject());
        }
        jsonObject.put(SNAPSHOT,animatorViews);
        return jsonObject;
    }
}
