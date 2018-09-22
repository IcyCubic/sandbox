package lai.henry.sandbox.model;

/*
 * POJO representing the computed results of the input string
 * */
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
