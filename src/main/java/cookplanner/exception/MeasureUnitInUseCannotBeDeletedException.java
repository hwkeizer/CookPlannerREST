package cookplanner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
		value=HttpStatus.METHOD_NOT_ALLOWED, 
		reason="Maateenheid wordt gebruik in één of meerdere recepten en kan niet worden verwijderd")
public class MeasureUnitInUseCannotBeDeletedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	@Override
    public synchronized Throwable fillInStackTrace() {
        return this;
	}
}
