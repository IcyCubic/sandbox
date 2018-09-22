package lai.henry.sandbox;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import lai.henry.sandbox.model.Results;

public class Application {

	private static final IO io = new IO();

	private final EncryptionUtil encryptionUtil;
	/*
	 * TODO Unit Tests 
	 * Include Unit Test(s) with instructions on how a Continuous Integration system can execute your test(s)
	 */

	/*
	 * TODO Dockerfile docker build . -t codechal docker run codechal
	 * "your@email.com"
	 */

	public Application() throws NoSuchAlgorithmException {
		this.encryptionUtil = new EncryptionUtil();
	}

	/**
	 * This application takes a string argument and prints out a JSON response
	 * compliant to the schema specified in the documentation. The application
	 * generates a RSA public / private key pair to encode and sign the message if
	 * the key files are absent, otherwise the keys are retrieved from the files and
	 * reused.
	 *
	 * @param args The first argument is the message to be processed. There must be
	 *             one argument, arguments beyond the first are ignored.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			io.print("Must invoke the application with a string to be processed");
			System.exit(1);
		}
		String input = args[0];
		io.print("Input: " + input);

		Application app = new Application();
		KeyPair keyPair = app.encryptionUtil.obtainKeyPair();

		String signatureString = app.encryptionUtil.signInput(input, keyPair.getPrivate());
		String publicKey = app.encryptionUtil.generatePublicKeyString(keyPair.getPublic().getEncoded());

		Results results = new Results(input, signatureString, publicKey);
		io.print(results);

		if (app.encryptionUtil.verifyResults(results)) {
			io.print("Validated results");
		} else {
			io.print("Invalid Results!!");
		};
	}
}
