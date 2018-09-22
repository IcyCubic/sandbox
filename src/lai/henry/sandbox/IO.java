package lai.henry.sandbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

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
	
    public void writeEncodedKeyToFile(String Base64EncodedKey, String filename) throws IOException {
    	Writer out = new FileWriter(filename);
    	try {
    		out.write(Base64EncodedKey);
    	} finally {
    		if (out != null) out.close();
    	}
    }

	public String getKey(String filename) throws IOException{
	    String pemKey = "";
	    BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			return null;
		}
	    String line;
	    while ((line = reader.readLine()) != null) {
	        pemKey += line + "\n";
	    }
	    reader.close();
	    return pemKey;
	}
}
