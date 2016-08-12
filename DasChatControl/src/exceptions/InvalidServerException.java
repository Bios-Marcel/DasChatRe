package exceptions;

public class InvalidServerException extends Exception {
	public InvalidServerException() {
		super();
	}

	public InvalidServerException(String message) {
		super(message);
	}
}
