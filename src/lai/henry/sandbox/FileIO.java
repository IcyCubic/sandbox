package lai.henry.sandbox;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class FileIO {
	
	private final static ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	public static File[] retrieveFiles(String directory){
		File dir = new File(directory);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		return files;
	}
	
//	@SuppressWarnings("unchecked")
//	public static <T> T fileToPOJO(String filePath, Class<T> type){
//
//		try {
//			return (T) mapper.readValue(new File(filePath), Class.forName(type.getName()));
//		} catch (ClassNotFoundException | IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fileToPOJO(File file, Class<T> type){
		
		try {
			return (T) mapper.readValue(file, Class.forName(type.getName()));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	public static void objectToFile(String filePath, Object content) {
//		try {
//			mapper.writeValue(new File(filePath), content);
//			
//			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(content);
//			System.out.println(jsonInString);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void objectToFile(File file, Object content) {
		try {
			mapper.writeValue(file, content);
			
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(content);
			System.out.println(jsonInString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
