package cookplanner.api;

import lombok.Data;

@Data
public class ApiResponse<T> {

	private int status;
	private String message;
	private T result;
}
