package event;

import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyEvent {
    public static final String DISPATCH = "dispatchTouchEvent",SETTEXT = "setText";
    public static final String ACTIVITY_ID = "ActivityID";
    public static final String PACKAGE_NAME = "PackageName";
    public static final String METHOD_NAME = "methodName";
    public static final String PARAMETER_TYPE = "parameterType";
    public static final String VIEW_PATH = "viewPath";
    public static final String VIEW_ID = "viewId";
    public static final String SNAPSHOT = "snapshot";
    public static final String PARAMETER_VALUE = "parameterValue";
    public static final String VIEW_X = "viewX",VIEW_Y="viewY";
    public static final String VIEW_WIDTH = "viewWidth";
    public static final String VIEW_HEIGHT = "viewHeight";
    public static final String VIEW_CLASS_NAME = "viewName";
    public static final String CHILDS = "childs";
    public static final String VIEW_CHILD_INDEX = "viewIndex";
    public static final String STRUCTURE = "structure";

    private String activityId,componentId,path,packageName;
    private String methodName;
    private List<MyParameter> parameters;
    private List<AnimatorNode> snapshot;
    private float viewX,viewY,width,height;
    private List<ViewInfo> structure;
    public MyEvent(String activityId,String componentId,String path,String methodName,float viewX,float viewY,float width,float height,String packageName){
        this.activityId = activityId;
        this.componentId = componentId;
        this.path = path;
        this.methodName = methodName;
        snapshot = new ArrayList<>();
        this.viewX = viewX;
        this.viewY = viewY;
        this.width = width;
        this.height = height;
        this.packageName = packageName;
    }

    public void setStructure(List<ViewInfo> structure) {
        this.structure = structure;
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

    public List<ViewInfo> getStructure() {
        return structure;
    }

    public float getViewX() {
        return viewX;
    }

    public float getViewY() {
        return viewY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
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
        jsonObject.put(VIEW_X,viewX);
        jsonObject.put(VIEW_Y,viewY);
        jsonObject.put(VIEW_WIDTH,width);
        jsonObject.put(VIEW_HEIGHT,height);
        jsonObject.put(PACKAGE_NAME,packageName);
        //添加参数类型,只有输入操作有参数，点击操作没有
        if(parameters!=null){
            for(MyParameter param:parameters){
                jsonObject.put("parameterType",param.type);
            }
        }
        JSONArray animatorViews = new JSONArray();
        for(AnimatorNode animatorNode:snapshot){
            animatorViews.add(animatorNode.toJSONObject());
        }
        jsonObject.put(SNAPSHOT,animatorViews);

        //add structure
        JSONArray structureJSON = new JSONArray();
        for(ViewInfo viewInfo:structure){
            JSONObject viewInfoJSON = viewInfo.toJSONObject();
            structureJSON.add(viewInfoJSON);
        }
        jsonObject.put(STRUCTURE,structureJSON);

        return jsonObject;
    }
}
