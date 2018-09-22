package lai.henry.sandbox;

import static lai.henry.sandbox.model.PEMTags.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lai.henry.sandbox.model.Results;

public class EncryptionUtil {

	// This info would likely be stored in a configuration file somewhere
	private static final String PUBLIC_KEY_FILENAME = "public.key";
	private static final String PRIVATE_KEY_FILENAME = "private.key";

	private static final String SIGNATURE_FORMAT = "SHA256withRSA";
	private static final String ENCRYPTION_TYPE = "RSA";
	private static final int BUFFER_LENGTH = 2048;
	private static final int KEY_SIZE = 2048; // typical key size for RSA encryption

	private static final Base64.Encoder Base64Encoder = Base64.getEncoder();
	private static final Base64.Decoder Base64Decoder = Base64.getDecoder();
	private static final IO io = new IO();

	private final Signature signature;

	public EncryptionUtil() throws NoSuchAlgorithmException {
		this.signature = Signature.getInstance(SIGNATURE_FORMAT);
	}

	public KeyPair obtainKeyPair() throws Exception {
		KeyPair keyPair = loadKeyPairFromFiles();
		if (keyPair == null) {
			io.print("Key Files not found; generating new keys");
			keyPair = this.generateNewKeyPair();
			storeKeyPairInFiles(keyPair);
		}

		return keyPair;
	}
	
	public String signInput(String input, PrivateKey pivateKey) throws SignatureException, IOException, InvalidKeyException, NoSuchAlgorithmException {
		signature.initSign(pivateKey);
		updateSignature(input);

		byte[] signatureBytes = signature.sign();

		String signatureString = Base64Encoder.encodeToString(signatureBytes);
		return signatureString;
	}

	public boolean verifyResults(Results results) throws InvalidKeyException, InvalidKeySpecException,
			NoSuchAlgorithmException, SignatureException, IOException {
		String trimmedPubKey = results.getPubkey().replace(PUBLIC_KEY_PREFIX.toString(), "").replace(PUBLIC_KEY_SUFFIX.toString(), "");
		byte[] publicBytes = Base64Decoder.decode(trimmedPubKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_TYPE);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		signature.initVerify(pubKey);
		this.updateSignature(results.getMessage());

		return signature.verify(Base64Decoder.decode(results.getSignature()));
	}
	
	private void updateSignature(String input) throws SignatureException, IOException {
		InputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(input.getBytes()); // Depending on source of input, this would be a different input stream
			byte[] buf = new byte[BUFFER_LENGTH];
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

	private KeyPair generateNewKeyPair() {
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
	
	private void storeKeyPairInFiles(KeyPair keyPair) throws IOException {
		OutputStream out = null;
		try {
			io.print("Private key format: " + keyPair.getPrivate().getFormat());
			String base64EncodedPrivateKey = Base64Encoder.encodeToString(keyPair.getPrivate().getEncoded());
			String pemFormatPrivateKey = generatePrivateKeyString(base64EncodedPrivateKey);
			io.writeEncodedKeyToFile(pemFormatPrivateKey, PRIVATE_KEY_FILENAME);

			io.print("Public key format: " + keyPair.getPublic().getFormat());
			String base64EncodedPublicKey = Base64Encoder.encodeToString(keyPair.getPublic().getEncoded());
			String pemFormatPublicKey = generatePublicKeyString(base64EncodedPublicKey);
			io.writeEncodedKeyToFile(pemFormatPublicKey, PUBLIC_KEY_FILENAME);
		} finally {
			if (out != null) { out.close(); }
		}
	}
	
	private KeyPair loadKeyPairFromFiles() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		KeyFactory kf = KeyFactory.getInstance(ENCRYPTION_TYPE);

		// generate private key
		String privatePemKey = io.getKey(PRIVATE_KEY_FILENAME);
		if (privatePemKey == null) { return null; }
		String trimmedPrivateKey = privatePemKey.replace(PRIVATE_KEY_PREFIX.toString(), "").replace(PRIVATE_KEY_SUFFIX.toString(), "");
		byte[] privateKeyBytes = Base64Decoder.decode(trimmedPrivateKey);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		PrivateKey pvt = kf.generatePrivate(privateKeySpec);

		// generate public key
		String publicPemKey = io.getKey(PUBLIC_KEY_FILENAME);
		if (publicPemKey == null) { return null; }
		String trimmedPublicKey = publicPemKey.replace(PUBLIC_KEY_PREFIX.toString(), "").replace(PUBLIC_KEY_SUFFIX.toString(), "");
		byte[] publicKeyBytes = Base64Decoder.decode(trimmedPublicKey);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		PublicKey pub = kf.generatePublic(publicKeySpec);

		System.out.println("Keys found in file, loaded");
		return new KeyPair(pub, pvt);
	}
	
	private String generatePublicKeyString(String encodedPublicKey) {
		return PUBLIC_KEY_PREFIX + encodedPublicKey + PUBLIC_KEY_SUFFIX;
	}
	
	private String generatePrivateKeyString(String encodedPrivateKey) {
		return PRIVATE_KEY_PREFIX + encodedPrivateKey + PRIVATE_KEY_SUFFIX;
	}
}
