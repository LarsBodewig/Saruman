package dev.bodewig.saruman.client;

public class DecryptionException extends Exception {
	private static final long serialVersionUID = 3499157163286583661L;

	public DecryptionException(Exception e) {
		super(e);
	}
}
