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
	
	/**
	 * Prints a string through System.out.println.
	 *
	 * @param  message	the String to be printed
	 */
    public void print(String message) {
    	System.out.println(message);
    }

    /**
     * Renders an object into a JSON string if possible and prints the result
     * in an indented output.  
     *
     * @param  obj	the object to be rendered and printed
     * @throws JsonProcessingException if the object cannot be rendered as a JSON string
     */
    public void print(Object obj) throws JsonProcessingException {
    	// This is just for convience of converting a POJO into JSON 
    	this.print(mapper.writeValueAsString(obj));
    }
	
    /**
     * Writes the specified file to disk
     *
     * @param  content	the intended contents of the file
     * @param  filename	the system-dependent filename 
     * @throws IOException
     */
    public void writeEncodedKeyToFile(String content, String filename) throws IOException {
    	Writer out = new FileWriter(filename);
    	try {
    		out.write(content);
    	} finally {
    		if (out != null) out.close();
    	}
    }
    
    /**
     * Retrieves the text content of the specified file
     *
     * @param  filename	the system-dependent filename 
     * @throws IOException
     */
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
