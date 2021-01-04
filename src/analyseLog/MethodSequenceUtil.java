package analyseLog;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MethodSequenceUtil {
	private static final int MainThreadID = 2;
	public static List<MyMethod> getSequence(String fileName) {
		ArrayList<MyMethod> callSeq;
		ArrayList<String> allSeq = new ArrayList<>();
		File file = new File(fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
//			FileReader fileReader = new FileReader(file);
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line = null;
			while((line=reader.readLine())!=null) {
				allSeq.add(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		callSeq = analyseSequence(allSeq);
		//测试阶段暂时未添加
//		callSeq = adjustSetTextCommitText(callSeq);
		return callSeq;
	}

	private static ArrayList<MyMethod> analyseSequence(ArrayList<String> allSeq) {
		// TODO Auto-generated method stub
		List<MyMethod> stack = new ArrayList<>();
		ArrayList<MyMethod> callSeq = new ArrayList<>();
		int beforeNum = 0,afterNum = 0;
		for(String methodName :allSeq) {
			if(methodName.startsWith("before: ")) {
				beforeNum++;
				String input = methodName.substring("before: ".length(), methodName.length());
//                System.out.println("before"+input.substring(0,30));
				if(!checkThreadId(input,MainThreadID)) {
					continue;
				}
				MyMethod method = new MyMethod(input);
				method.setInput(input);
				stack.add(method);
			}else if(methodName.startsWith("after: ")){
				afterNum++;
				String output = methodName.substring("after: ".length(), methodName.length());
//				System.out.println("after: "+output.substring(0,30));
				if(!checkThreadId(output,MainThreadID)) {
					continue;
				}
				MyMethod curM = stack.remove(stack.size()-1);
				curM.setOutput(output);
				if(stack.size()==0) {
					callSeq.add(curM);
					continue;
				}
				MyMethod preM = stack.get(stack.size()-1);
				preM.addChild(curM);
				curM.parent = preM;
			}else {
				if(stack.size()==0){
					MyMethod curM = new MyMethod();
					curM.selfJson = JSONObject.parseObject(methodName);
					callSeq.add(curM);
				}
			}
		}

		for(MyMethod method :stack){
			System.out.println(method.name);
		}
//		System.out.println(beforeNum+" "+afterNum);
		return callSeq;
	}
	private static boolean checkThreadId(String jsonString,int threadId) {
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		int id = jsonObject.getIntValue("threadId");
		if(id==threadId) {
			return true;
		}
		return false;
	}

	/**
	 * 将myMethodList中的commitText的位置替换为setText
	 * @param myMethodList
	 * @return
	 */
	private static ArrayList<MyMethod> adjustSetTextCommitText(List<MyMethod> myMethodList){
		ArrayList<MyMethod> res = new ArrayList<>();
		MyMethod myMethod = null,temp = null;
		for(int i=0;i<myMethodList.size();i++){
			myMethod = myMethodList.get(i);

			if(myMethod.methodName.equals("commitText")&&myMethod.methodCaller.contains("android.view.inputmethod.BaseInputConnection")){
				List<MyMethod> childs = myMethod.childs;
				while (!childs.isEmpty()){
					MyMethod child = childs.remove(0);
					if(child.methodName.equals("setText")&&child.methodCaller.contains("MyTextWatcher")){
						res.add(child);
						break;
					}
					childs.addAll(child.childs);
				}
			}else{
				res.add(myMethod);
			}
		}
		return res;
	}
}
