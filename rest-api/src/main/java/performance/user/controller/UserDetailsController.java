package performance.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import performance.user.service.UserDetailsService;
import performance.user.model.UserDetails;

/**
 * Created on 28/December/2020 By Author Eresh, Gorantla
 **/
@RestController
@RequestMapping("/api")
public class UserDetailsController {

	final UserDetailsService userDetailsService;

	public UserDetailsController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/rest/users")
	public UserDetails generateUserDetails() {
		System.out.println("got request");
		return this.userDetailsService.generateUsers();
	}
}
