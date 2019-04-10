package cookplanner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Account niet gevonden")
public class AccountDoesNotExistException extends Exception{

	private static final long serialVersionUID = 1L;
	
	@Override
    public synchronized Throwable fillInStackTrace() {
        return this;
	}
}
