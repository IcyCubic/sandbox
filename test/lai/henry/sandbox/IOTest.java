package lai.henry.sandbox;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class IOTest {

	private final IO io = new IO();
	
	@Test
	public void testPrintObject() throws JsonProcessingException {
		Map<String, String> testObj = new HashMap<>();
		testObj.put("foo", "bar");
		io.print(testObj);
	}

	@Test(expected=JsonProcessingException.class)
	public void testPrintObjectFail() throws JsonProcessingException {
		Object obj = new Object();
		io.print(obj);
	}
	
	@Test
	public void testWriteEncodedKeyToFile() throws IOException {
		String testStr = "foo bar";
		String filename = "test.txt";
		io.writeEncodedKeyToFile(testStr, filename);
		
		File file = new File(filename);
		assertTrue(file.exists());
		file.delete();
	}
	
	@Test
	public void testGetKey() throws IOException {
		String contents = "-----BEGIN PUBLIC KEY-----\n" + 
				          "foobarfoobarfoobarfoobar\n" + 
				          "-----END PUBLIC KEY-----\n";
		String filename = "test.key";
		URL url = this.getClass().getResource("/" + filename);
		String result = io.getKey(url.getPath());
		assertTrue(result.equals(contents));
	}
}
