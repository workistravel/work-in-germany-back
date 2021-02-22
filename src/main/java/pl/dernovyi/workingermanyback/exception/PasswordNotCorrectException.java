package pl.dernovyi.workingermanyback.exception;

public class PasswordNotCorrectException extends Exception {
    public PasswordNotCorrectException(String message) {
        super(message);
    }
}
