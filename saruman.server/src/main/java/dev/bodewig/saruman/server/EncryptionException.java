package dev.bodewig.saruman.server;

public class EncryptionException extends Exception {
	private static final long serialVersionUID = -8292113864099380812L;

	public EncryptionException(Exception cause) {
		super(cause);
	}
}
