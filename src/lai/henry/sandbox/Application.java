package lai.henry.sandbox;

import static lai.henry.sandbox.model.PEMTags.*;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import lai.henry.sandbox.model.Results;


public class Application {

	private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final IO io = new IO();

    private final EncryptionUtil encryptionUtil;
	/*
	Document your code, at a minimum defining parameter types and return values for any public methods
	Include Unit Test(s) with instructions on how a Continuous Integration system can execute your test(s)
	 */
	
	/*
	docker build . -t codechal
	docker run codechal "your@email.com"
	*/
	
	public Application() throws NoSuchAlgorithmException {
		this.encryptionUtil = new EncryptionUtil();
	}

	public static void main(String[] args) throws Exception {
		if ( args.length == 0 ) {
			io.print("Must invoke the application with a string to be processed");
		    System.exit(1);
		}
		String input = args[0];
		io.print("Input: " + input);

		Application app = new Application();
		KeyPair keyPair = app.encryptionUtil.obtainKeyPair();
		
		String signatureString = app.encryptionUtil.signInput(input, keyPair.getPrivate());
		String publicKey = generatePublicKeyString(keyPair.getPublic().getEncoded());

		Results results = new Results(input, signatureString, publicKey);
		io.print(results);
		
		if (app.encryptionUtil.verifyResults(results)) {
			io.print("Validated results");
		} else {
			io.print("Invalid Results!!");
		};
	}
	
	private static String generatePublicKeyString(byte[] bytes) {
		return PUBLIC_KEY_PREFIX + encoder.encodeToString(bytes) + PUBLIC_KEY_SUFFIX;
	}
}
