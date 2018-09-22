package lai.henry.sandbox.model;

/**
 * A POJO representing the generated results containing the original message,
 * the signature and the public key
 */
public class Results {
	private final String message;
	private final String signature;
	private final String pubkey;

	public Results(String message, String signature, String pubkey) {
		super();
		this.message = message;
		this.signature = signature;
		this.pubkey = pubkey;
	}

	public String getMessage() {
		return message;
	}

	public String getSignature() {
		return signature;
	}

	public String getPubkey() {
		return pubkey;
	}
}
