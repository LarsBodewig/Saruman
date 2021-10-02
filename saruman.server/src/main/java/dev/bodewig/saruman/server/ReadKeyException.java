package dev.bodewig.saruman.server;

public class ReadKeyException extends Exception {
	private static final long serialVersionUID = -8170185763813364083L;

	public ReadKeyException(Exception e) {
		super(e);
	}
}
