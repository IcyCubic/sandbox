package lai.henry.sandbox.model;

public enum PEMTags {
	PUBLIC_KEY_PREFIX("-----BEGIN PUBLIC KEY-----\n"),
	PUBLIC_KEY_SUFFIX("\n-----END PUBLIC KEY-----\n"),
	PRIVATE_KEY_PREFIX("-----BEGIN PUBLIC KEY-----\n"),
	PRIVATE_KEY_SUFFI("\n-----END PUBLIC KEY-----\n");

    private String value;

    PEMTags(final String value) {
        this.value = value;
    }
    
	public String getValue() {
		return this.value;
	}
	
	@Override
    public String toString() {
        return this.getValue();
    }
}
