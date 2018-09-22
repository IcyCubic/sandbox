package lai.henry.sandbox;

import static lai.henry.sandbox.model.PEMTags.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
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
	
    public void writeEncodedKeyToFile(String Base64EncodedKey, String filename) throws IOException {
    	Writer out = new FileWriter(filename);
    	try {
    		out.write(Base64EncodedKey);
    	} finally {
    		if (out != null) out.close();
    	}
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
	
	public String getKey(String filename) throws IOException{
	    String pemKey = "";
	    BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			print("Key file not found");
			return null;
		}
	    String line;
	    while ((line = reader.readLine()) != null) {
	        pemKey += line + "\n";
	    }
	    reader.close();
	    return pemKey;
	}
	
	public String generatePublicKeyString(String encodedPublicKey) {
		return PUBLIC_KEY_PREFIX + encodedPublicKey + PUBLIC_KEY_SUFFIX;
	}
	
	public String generatePrivateKeyString(String encodedPrivateKey) {
		return PRIVATE_KEY_PREFIX + encodedPrivateKey + PRIVATE_KEY_SUFFIX;
	}
}
