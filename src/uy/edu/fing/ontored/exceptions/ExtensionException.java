package uy.edu.fing.ontored.exceptions;

public class ExtensionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExtensionException() {
		super();
	}

	public ExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExtensionException(String message) {
		super(message);
	}

	public ExtensionException(Throwable cause) {
		super(cause);
	}
	
}
