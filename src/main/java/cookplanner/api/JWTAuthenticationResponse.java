package cookplanner.api;

import cookplanner.domain.AccountRole;
import lombok.Data;

@Data
public class JWTAuthenticationResponse {

	private String accessToken;
	private String tokenType = "Bearer";
	private String username;
	private AccountRole role;
	
	public JWTAuthenticationResponse(String accessToken, String username, AccountRole role) {
		this.accessToken = accessToken;
		this.username = username;
		this.role = role;
	}
}
