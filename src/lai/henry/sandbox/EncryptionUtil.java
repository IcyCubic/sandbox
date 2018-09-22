package lai.henry.sandbox;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {
	
	private static final String ENCRYPTION_TYPE = "RSA";
	private static final int KEY_SIZE = 2048; // typical key size for RSA encryption
	
    static private Base64.Encoder encoder = Base64.getEncoder();

	public void encryptAndSign(String payload) {
		
	}
	
	// byte[] buf = key.getEncoded();
	public String base64Encode(byte[] data) {
		return encoder.encodeToString(data);
	}
	
	public String base64Decode(String payload) {
		return "";
	}
	
	public KeyPair generateNewKeyPair() {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance(ENCRYPTION_TYPE);
		} catch (NoSuchAlgorithmException e) { // Would let this bubble up if there is an exception mapper in use
			e.printStackTrace();
			return null; 
		}
		keyGen.initialize(KEY_SIZE);
		
		return keyGen.generateKeyPair();
	}
}
