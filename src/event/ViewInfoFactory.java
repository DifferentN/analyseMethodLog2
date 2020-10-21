package event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewInfoFactory {


    public static ViewInfo createViewInfo(JSONObject viewInfoJSON){
        float x = viewInfoJSON.getFloat(MyEvent.VIEW_X);
        float y = viewInfoJSON.getFloat(MyEvent.VIEW_Y);
        float width = viewInfoJSON.getFloat(MyEvent.VIEW_WIDTH);
        float height = viewInfoJSON.getFloat(MyEvent.VIEW_HEIGHT);
        String xpath = viewInfoJSON.getString(MyEvent.VIEW_PATH);
        String viewName = viewInfoJSON.getString(MyEvent.VIEW_CLASS_NAME);
        int viewIndex = viewInfoJSON.getIntValue(MyEvent.VIEW_CHILD_INDEX);
        ViewInfo viewInfo = new ViewInfo(x,y,width,height,xpath,viewName,viewIndex);
        //create and add childs ViewInfo
        JSONArray  childs = viewInfoJSON.getJSONArray(MyEvent.CHILDS);
        //has child view
        if(childs!=null){
            List<ViewInfo> childViewInfos = new ArrayList<>();
            for(int i=0;i<childs.size();i++){
                JSONObject child = childs.getJSONObject(i);
                childViewInfos.add(createViewInfo(child));
            }
            viewInfo.setChilds(childViewInfos);
        }
        return viewInfo;
    }
}
