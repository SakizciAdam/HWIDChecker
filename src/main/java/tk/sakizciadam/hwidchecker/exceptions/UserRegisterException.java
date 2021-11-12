package tk.sakizciadam.hwidchecker.exceptions;

public class UserRegisterException
        extends RuntimeException {
    public UserRegisterException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
