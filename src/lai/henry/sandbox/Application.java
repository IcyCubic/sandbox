package lai.henry.sandbox;

import static lai.henry.sandbox.model.PEMTags.*;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lai.henry.sandbox.model.Results;


public class Application {

	// This info would likely be stored in a configuration file somewhere
    private static final String PUBLIC_KEY_FILENAME = "public.key";
    private static final String PRIVATE_KEY_FILENAME = "private.key";
    
	private static final String ENCRYPTION_TYPE = "RSA";
	private static final String SIGNATURE_FORMAT = "SHA256withRSA";
	private static final int BUFFER_LENGTH = 2048;
	
	private static final Base64.Encoder encoder = Base64.getEncoder();
	private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final EncryptionUtil encryptionUtil = new EncryptionUtil();
    
	/*
	Document your code, at a minimum defining parameter types and return values for any public methods
	Include Unit Test(s) with instructions on how a Continuous Integration system can execute your test(s)
	 */
	
	/*
	docker build . -t codechal
	docker run codechal "your@email.com"
	*/
	
	public static void main(String[] args) throws Exception {
		if ( args.length == 0 ) {
			print("Must invoke the application with a string to be processed");
		    System.exit(1);
		}
		String input = args[0];
		print("Input: " + input);
		
		KeyPair keyPair = obtainKeyPair();
		
		Signature signature = Signature.getInstance(SIGNATURE_FORMAT);
		String signatureString = signInput(input, keyPair.getPrivate(), signature);
		String publicKey = generatePublicKeyString(keyPair.getPublic());

		Results results = new Results(input, signatureString, publicKey);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		print(mapper.writeValueAsString(results));
		
		if (verifyResults(results, signature)) {
			print("Validated results");
		} else {
			print("Invalid Results!!");
		};
	}
	
	private static String generatePublicKeyString(PublicKey publicKey) {
		return PUBLIC_KEY_PREFIX + encoder.encodeToString(publicKey.getEncoded()) + PUBLIC_KEY_SUFFIX;
	}
	
	private static boolean verifyResults(Results results, Signature signature) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, IOException {
		String trimmedPubKey = results.getPubkey().replace(PUBLIC_KEY_PREFIX.toString(), "").replace(PUBLIC_KEY_SUFFIX.toString(), "");
		byte[] publicBytes = decoder.decode(trimmedPubKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_TYPE);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		
		signature.initVerify(pubKey);
		updateSignature(results.getMessage(), signature);
		
		return signature.verify(decoder.decode(results.getSignature()));
	}
	
	private static String signInput(String input, PrivateKey pivateKey, Signature signature) throws SignatureException, IOException, InvalidKeyException, NoSuchAlgorithmException {
		signature.initSign(pivateKey);
		updateSignature(input, signature);
		
	    byte[] signatureBytes = signature.sign();
	    
	    String signatureString = encoder.encodeToString(signatureBytes);
		return signatureString;
	}
	
	private static void updateSignature(String input, Signature signature) throws SignatureException, IOException {
		InputStream inputStream = null;
		try {
		    inputStream = new ByteArrayInputStream(input.getBytes()); // Depending on source of input, this would be a different input stream
		    byte[] buf = new byte[BUFFER_LENGTH];
		    int len;
		    while ((len = inputStream.read(buf)) != -1) {
		    	signature.update(buf, 0, len);
		    }
		} finally {
		    if ( inputStream != null ) {
		    	inputStream.close();
		    }
		}
	}
	
	private static KeyPair obtainKeyPair() throws Exception {
		KeyPair keyPair = loadKeyPairFromFiles();
		if (keyPair == null) {			
			keyPair = encryptionUtil.generateNewKeyPair();
			storeKeyPairInFiles(keyPair);
			print("Key Files not found; generated new keys");
		} 
		
		return keyPair;
	}
	
	private static KeyPair loadKeyPairFromFiles() throws InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory kf = KeyFactory.getInstance(ENCRYPTION_TYPE);
		
		// generate private key
		byte[] bytes = readFileBytes(PRIVATE_KEY_FILENAME);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytes);
		PrivateKey pvt = kf.generatePrivate(privateKeySpec);
		
		// generate public key
		bytes = readFileBytes(PUBLIC_KEY_FILENAME);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes);
		PublicKey pub = kf.generatePublic(publicKeySpec);
		
		System.out.println("Keys found in file, loaded");
		return new KeyPair(pub, pvt);
	}
	
	private static byte[] readFileBytes(String filePath) {
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

	private static void storeKeyPairInFiles(KeyPair keyPair) throws IOException {
		OutputStream out = null;
		try {
			print("Private key format: " + keyPair.getPrivate().getFormat());
		    out = new FileOutputStream(PRIVATE_KEY_FILENAME);
		    writeBinary(out, keyPair.getPrivate());
		    out.close();

		    print("Public key format: " + keyPair.getPublic().getFormat());
		    out = new FileOutputStream(PUBLIC_KEY_FILENAME);
		    writeBinary(out, keyPair.getPublic());
		} finally {
		    if ( out != null ) {
		    	out.close();
		    }
		}
	}

    static private void writeBinary(OutputStream out,Key key) throws IOException {
    	out.write(key.getEncoded());
    }
    
    static private void print(String message) {
    	System.out.println(message);
    }
}
