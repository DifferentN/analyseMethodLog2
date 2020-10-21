package analyseLog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyMethod {
	public String name,methodName,methodCaller,packageName;
	private JSONArray inputJSON;
	private JSONObject outputJSON;
	public MyMethod parent;
	public List<MyMethod> childs;
	public JSONObject selfJson;
	public MyMethod(){}
	public MyMethod(String jsonString) {
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		selfJson = jsonObject;
		methodCaller = jsonObject.getString("callerClassName");
		name = jsonString;
		methodName = jsonObject.getString("methodName");
		packageName = jsonObject.getString("packageName");
//		this.name = jsonObject.getString("callerClassName")+"/"+jsonObject.getString("methodName");
		childs = new ArrayList<>();
		if(jsonObject.get("threadId")!=null) {
//			System.out.println(jsonObject.get("threadId"));
		}else {
			System.out.println("方法出错");
		}
	}
	public void addChild(MyMethod m) {
		childs.add(m);
	}

	public void setInput(String methodStr) {
		JSONObject methodJSON = JSONObject.parseObject(methodStr);
		inputJSON = methodJSON.getJSONArray("methodParameter");
	}

	public void setOutput(String methodStr) {
		JSONObject methodJSON = JSONObject.parseObject(methodStr);
		outputJSON = methodJSON.getJSONObject("methodResult");
		
		String caller = methodJSON.getString("callerClassName");
		String name = methodJSON.getString("methodName");
		if(caller.compareTo(methodCaller)!=0||name.compareTo(methodName)!=0) {
			System.out.println("拼接出错");
		}
	}

	public JSONArray getInputJSON() {
		return inputJSON;
	}

	public JSONObject getOutputJSON() {
		return outputJSON;
	}

	public String getPackageName() {
		return packageName;
	}
}
