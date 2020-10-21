package FileUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class MyFileUtil {
	private String path;
	private File file;
	private FileWriter fileWriter;
	public MyFileUtil(String path) {
		this.path = path;
		file = new File(path);
		if(!file.exists()) {
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(file.exists()) {
			try {
				fileWriter = new FileWriter(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void write(String line) {
		if(line==null||fileWriter==null) {
			return;
		}
		try {
			fileWriter.write(line+"\n");
			fileWriter.flush();
			System.out.println(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void  close() {
		if(fileWriter!=null) {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void writeEventJSONArray(String path, JSONArray jsonArray){
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
			BufferedWriter writer = new BufferedWriter(outputStreamWriter);
			writer.write(jsonArray.toJSONString());

			fileOutputStream.flush();
			outputStreamWriter.flush();
			writer.flush();

			fileOutputStream.close();
			outputStreamWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static JSONArray readJSONArray(String path){
		File file = new File(path);
		if(!file.exists()){
			System.out.println("JSONArray file not exist "+path);
			return null;
		}
		JSONArray res = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
			BufferedReader  reader = new BufferedReader(inputStreamReader);
			StringBuilder strBuilder = new StringBuilder();
			String line = null;
			while((line=reader.readLine())!=null){
				strBuilder.append(line);
			}
			res = JSONArray.parseArray(strBuilder.toString());

			fileInputStream.close();
			inputStreamReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static JSONObject readJSONObject(String path){
		File file = new File(path);
		if(!file.exists()){
			System.out.println("JSONObject file not exist "+path);
			return null;
		}
		JSONObject res = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuilder strBuilder = new StringBuilder();
			String line = null;
			while((line=reader.readLine())!=null){
				strBuilder.append(line);
			}
			res = JSONObject.parseObject(strBuilder.toString());
			fileInputStream.close();
			inputStreamReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static void writeJSONObject(String path, JSONObject jsonObject){
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
			BufferedWriter writer = new BufferedWriter(outputStreamWriter);
			String content = jsonObject.toJSONString();
			writer.write(content);

			fileOutputStream.flush();
			outputStreamWriter.flush();
			writer.flush();
			fileOutputStream.close();
			outputStreamWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
