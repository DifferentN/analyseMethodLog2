package start;

import analyseLog.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import event.MyEvent;
import event.MyParameter;
import event.ViewInfo;
import event.ViewInfoFactory;

import java.util.ArrayList;
import java.util.List;

import static event.MyEvent.DISPATCH;
import static event.MyEvent.SETTEXT;

public class GenerateMyEventUtil {
    public static List<MyEvent> generateMyEventList(List<MyMethod> callSeq){
        List<MyMethod> list = new ArrayList<>();
        List<MyEvent> events = new ArrayList<>();
        MyEvent myEvent = null;
        int pos = 0;
        while(pos<callSeq.size()){
            MyMethod curMethod = callSeq.get(pos);
            if(curMethod.methodName==null){
                list.add(curMethod);
                pos++;
                continue;
            }else if(curMethod.methodName.equals(DISPATCH)){
                // 如果是view类型的dispatchTouchEvent 跳过，不应该出现，应该只有Activity的dispatchTouchEvent
                if(isActivityDispatchEvent(curMethod.selfJson)){
                    pos++;
                    continue;
                }
                myEvent = initDispatchEventByMyMethod(curMethod);
            }else if(curMethod.methodName.equals(SETTEXT)){
                myEvent = initSetTextEventByMyMethod(curMethod);

                JSONArray params = curMethod.selfJson.getJSONArray("methodParameter");
                String text = params.getJSONObject(0).getString("parameterValue");
                MyParameter parameter = new MyParameter("String",text);
                //设置setText的参数
                List<MyParameter> parameterList = new ArrayList<MyParameter>();
                parameterList.add(parameter);
                myEvent.setParameters(parameterList);

                while(pos<callSeq.size()&&callSeq.get(pos).methodName!=null&&callSeq.get(pos).methodName.equals(SETTEXT)){
                    pos++;
                }
                pos--;
            }else{
                System.out.println("myEvent error");
            }
            addAnimator(list,myEvent);
            events.add(myEvent);
            list.clear();
            int nextPos = findNextStartPosition(callSeq,pos);
            if(nextPos<0){
                break;
            }else pos = nextPos;
            pos++;
        }
        return events;
    }
    private static void addAnimator(List<MyMethod> list,MyEvent myEvent){
        for(MyMethod myMethod:list){
            String viewPath = getPathFromAnimatorJSON(myMethod.selfJson);
            if(myEvent.getPath().contains(viewPath)&&viewPath.length()>0){
                String animatorType = getAnimatorTypeFormJSON(myMethod.selfJson);
                myEvent.addAnimatorVNode(viewPath,animatorType);
            }
        }
    }
    private static String getPathFromAnimatorJSON(JSONObject jsonObject){
        return jsonObject.getString("viewPath");
    }
    private static String getAnimatorTypeFormJSON(JSONObject jsonObject){
        return jsonObject.getString("animatorType");
    }
    private static int findNextStartPosition(List<MyMethod> callSeq,int curPos){
        for(int i=curPos;i<callSeq.size();i++){
            MyMethod curMethod = callSeq.get(i);
            if(curMethod.methodName!=null){
                if(curMethod.methodName.equals(DISPATCH)&&
                        checkEventAction(curMethod)==1){
                    return i;
                }else if(curMethod.methodName.equals(SETTEXT)){
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * 对于dispatchTouchEvent方法获取发生在最底层组件的点击MyMethod
     */
    private static MyMethod getViewAboutMyMethod(MyMethod myMethod){
        List<MyMethod> queue = new ArrayList<>();
        queue.add(myMethod);
        MyMethod target = null;
        MyMethod cur = null;
        while(!queue.isEmpty()){
            cur = queue.remove(0);
            if(cur.selfJson.getBooleanValue("ViewFlag")&&cur.methodName.equals(DISPATCH)){
                if(target==null){
                    target = cur;
                }else if(getPath(cur.selfJson).length()>getPath(target.selfJson).length()){
                    target = cur;
                }
//                target = cur;
            }
            for(MyMethod next:cur.childs){
                queue.add(next);
            }
        }
        return target;
    }
    /**
     *
     * @param myMethod 是一个dispatchTouchEvent方法
     * @return 点击时间的类型 0：按下 2：滑动 1：释放 ;返回-1表示出错
     */
    private static int checkEventAction(MyMethod myMethod){
        List<MyMethod> queue = new ArrayList<>();
        queue.add(myMethod);
        MyMethod cur = null;
        JSONArray params;
        int action = -1;
        while(!queue.isEmpty()){
            cur = queue.remove(0);
            if(!cur.methodName.equals(DISPATCH)){
                continue;
            }
            params = cur.selfJson.getJSONArray("methodParameter");
            Object paramValue = params.getJSONObject(0).get("parameterValue");
            if(paramValue instanceof JSONObject){
                action = ((JSONObject)paramValue).getIntValue("action");
                return action;
            }
            for(int i=0;i<cur.childs.size();i++){
                queue.add(cur.childs.get(i));
            }
        }
        return -1;
    }
    private static MyEvent initSetTextEventByMyMethod(MyMethod myMethod){
        JSONObject json = myMethod.selfJson;
        MyEvent myEvent = new MyEvent(getActivityId(json),getComponentId(json),getPath(json),SETTEXT,getX(json),getY(json),getWidth(json),getHeight(json),myMethod.getPackageName());
        myEvent.setStructure(getStructure(json));
        return myEvent;
    }
    private static MyEvent initDispatchEventByMyMethod(MyMethod myMethod){
//        System.out.println(myMethod.selfJson.getBooleanValue("ViewFlag"));
        MyMethod myMethodTouchView = getViewAboutMyMethod(myMethod);
        JSONObject json = myMethodTouchView.selfJson;
        MyEvent myEvent = new MyEvent(getActivityId(json),getComponentId(json),getPath(json),DISPATCH,getX(json),getY(json),getWidth(json),getHeight(json),myMethod.getPackageName());
        //use activity json
        myEvent.setStructure(getStructure(myMethod.selfJson));
        return myEvent;
    }
    private static String getActivityId(JSONObject jsonObject){
        return jsonObject.getString("ActivityID");
    }
    private static String getComponentId(JSONObject jsonObject){
        if (jsonObject==null){
            System.out.println("jsonObject is null");
        }
        return jsonObject.getJSONObject("viewInfo").getString("viewId");
    }
    private static String getPath(JSONObject jsonObject){
        return jsonObject.getJSONObject("viewInfo").getString("viewPath");
    }
    private static float getX(JSONObject jsonObject){
        return jsonObject.getJSONObject("viewInfo").getFloatValue("viewX");
    }
    private static float getY(JSONObject jsonObject){
        return jsonObject.getJSONObject("viewInfo").getFloatValue("viewY");
    }
    private static int getWidth(JSONObject jsonObject){
        return  jsonObject.getJSONObject("viewInfo").getIntValue("viewWidth");
    }
    private static int getHeight(JSONObject jsonObject){
        return jsonObject.getJSONObject("viewInfo").getIntValue("viewHeight");
    }
    private static List<ViewInfo> getStructure(JSONObject jsonObject){
//        System.out.println(jsonObject.toJSONString());
        JSONArray jsonArray = jsonObject.getJSONArray("structure");
        List<ViewInfo> listViewInfo = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            ViewInfo viewInfo = ViewInfoFactory.createViewInfo(jsonArray.getJSONObject(i));
            listViewInfo.add(viewInfo);
        }
        return listViewInfo;
    }
    private static boolean isActivityDispatchEvent(JSONObject jsonObject){
        boolean viewFlag = jsonObject.getBooleanValue("ViewFlag");
        if(viewFlag){
            return true;
        }
        return false;
    }

}
