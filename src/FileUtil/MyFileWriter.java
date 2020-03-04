package FileUtil;

import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {
	private String path;
	private File file;
	private FileWriter fileWriter;
	public MyFileWriter(String path) {
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
			FileWriter writer = new FileWriter(file);
			writer.write(jsonArray.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
