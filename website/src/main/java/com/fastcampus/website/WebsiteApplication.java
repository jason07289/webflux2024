package com.fastcampus.website;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
@Controller
public class WebsiteApplication {
	RestTemplate restTemplate = new RestTemplate();
	public static void main(String[] args) {
		SpringApplication.run(WebsiteApplication.class, args);
	}

	@GetMapping("/")
	public String index(@RequestParam(name = "queue", defaultValue = "default") String queue,
						@RequestParam(name = "resource_id") Long resourceId,
						HttpServletRequest request) {
		var cookies = request.getCookies();
		var cookieName = "resource-queue-%s-token".formatted(queue);
		var token = "";
		if (cookies != null) {
			Optional<Cookie> cookie = Arrays.stream(cookies).filter(i -> i.getName().equalsIgnoreCase(cookieName)).findFirst();
			token = cookie.orElse(new Cookie(cookieName,"")).getValue();
		}

		URI uri = UriComponentsBuilder
				.fromHttpUrl("http://127.0.0.1:9010")
				.path("/api/v1/queue/allowed")
				.queryParam("queue", queue)
				.queryParam("resource_id", resourceId)
				.queryParam("token", token)
				.encode()
				.build()
				.toUri();
		ResponseEntity<AllowedResourceResponse> response = restTemplate.getForEntity(uri, AllowedResourceResponse.class);
		if (response.getBody() == null || !response.getBody().allowed()) {
			return "redirect:http://127.0.0.1:9010/waiting-room?resource_id=%d&redirect_url=%s"
					.formatted(resourceId,
							"http://127.0.0.1:9000?resource_id=%d".formatted(resourceId)
					);
		}

		return "index";
	}
	private record AllowedResourceResponse(Boolean allowed) {

	}
}
