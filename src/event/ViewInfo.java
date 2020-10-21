package event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class ViewInfo {
    private float x,y;
    private float width,height;
    private int viewIndex;
    private String viewPath,viewName;
    private List<ViewInfo> childs;

    public ViewInfo(float x, float y, float width, float height, String xpath, String viewName,int index) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.viewPath = xpath;
        this.viewName = viewName;
        this.viewIndex = index;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setViewPath(String xpath) {
        this.viewPath = xpath;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public void setChilds(List<ViewInfo> childs) {
        this.childs = childs;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getViewPath() {
        return viewPath;
    }

    public String getViewName() {
        return viewName;
    }

    public List<ViewInfo> getChilds() {
        return childs;
    }

    public int getViewIndex() {
        return viewIndex;
    }
    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MyEvent.VIEW_X,x);
        jsonObject.put(MyEvent.VIEW_Y,y);
        jsonObject.put(MyEvent.VIEW_WIDTH,width);
        jsonObject.put(MyEvent.VIEW_HEIGHT,height);
        jsonObject.put(MyEvent.VIEW_CLASS_NAME,viewName);
        jsonObject.put(MyEvent.VIEW_CHILD_INDEX,viewIndex);


        if(childs!=null){
            JSONArray jsonArray = new JSONArray();
            for(ViewInfo childViewInfo:childs){
                JSONObject childJSON = childViewInfo.toJSONObject();
                jsonArray.add(childJSON);
            }
            jsonObject.put(MyEvent.CHILDS,jsonArray);
        }


        return jsonObject;
    }
}
