package lai.henry.sandbox;

import static lai.henry.sandbox.model.PEMTags.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.junit.Test;

public class EncryptionUtilTest {

	private final EncryptionUtil util;
	
	public EncryptionUtilTest() throws NoSuchAlgorithmException {
		this.util = new EncryptionUtil();
	}
	
	@Test
	public void testGeneratePublicKeyString() {
		Base64.Decoder decoder = Base64.getDecoder();
		String testString = "foobarFOOBAR";
		byte[] testBytes = decoder.decode(testString);

		String expectedResults = PUBLIC_KEY_PREFIX + testString + PUBLIC_KEY_SUFFIX;
		String result = util.generatePublicKeyString(testBytes);
		
		assertTrue(result.equals(expectedResults));
	}

	@Test
	public void testObtainKeyPair() throws Exception {
		KeyPair result = util.obtainKeyPair();
		assertTrue(result != null);
		assertTrue(result.getPrivate() != null);
		assertTrue(result.getPublic() != null);
		
		deleteKeysAfterTest();
	}
	
	@Test
	public void testSignInput() throws Exception {
		String testInput = "Hello Ipsum";
		KeyPair keyPair = util.obtainKeyPair();
		String signature = util.signInput(testInput, keyPair.getPrivate());
		assertTrue(verifyResults(testInput, signature, keyPair.getPublic()));
		
		deleteKeysAfterTest();
	}
	
	private boolean verifyResults(String input, String signedString, PublicKey publicKey) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, IOException {
		Signature signature = Signature.getInstance(EncryptionUtil.SIGNATURE_FORMAT);
		
		byte[] publicBytes = publicKey.getEncoded();
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(EncryptionUtil.ENCRYPTION_TYPE);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		signature.initVerify(pubKey);
		this.updateSignature(input, signature);

		return signature.verify(Base64.getDecoder().decode(signedString));
	}
	
	private void updateSignature(String input, Signature signature) throws SignatureException, IOException {
		InputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(input.getBytes()); 
			byte[] buf = new byte[2048];
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				signature.update(buf, 0, len);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	private void deleteKeysAfterTest() {
		File file = new File(EncryptionUtil.PRIVATE_KEY_FILENAME);		
		file.delete();
		file = new File(EncryptionUtil.PUBLIC_KEY_FILENAME);
		file.delete();
		System.out.println("Test keys deleted");
	}
}
