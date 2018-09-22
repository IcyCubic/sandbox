package lai.henry.sandbox;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class IO {
	
	private final static ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
    public void print(String message) {
    	System.out.println(message);
    }
    
    public void print(Object obj) throws JsonProcessingException {
    	this.print(mapper.writeValueAsString(obj));
    }
	
	public void writeBinary(OutputStream out,Key key) throws IOException {
    	out.write(key.getEncoded());
    }
    
	public byte[] readFileBytes(String filePath) {
		Path path = Paths.get(filePath);
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(path);
		} catch (IOException e) {
			print("IO Exception; Probably missing Key files or initial run");
			return null;
		}
		return bytes;
	}
}
