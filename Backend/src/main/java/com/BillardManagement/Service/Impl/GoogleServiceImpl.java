package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.GoogleRequest;
import com.BillardManagement.DTO.Response.LoginResponse;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Service.CustomerService;
import com.BillardManagement.Service.GoogleService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Service
public class GoogleServiceImpl implements GoogleService {

    private final CustomerService customerService = new CustomerServiceImpl();
    private final WebClient webClient = WebClient.create();

    private String clientId = "585309011001-3d2a3mpvaea4ffr1vqrjqbfgaqdobode.apps.googleusercontent.com";

    private String clientSecret = "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";

    // GIS Code flow dùng "postmessage"
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String REDIRECT_URI = "postmessage";

    @Override
    public LoginResponse handleGoogleAuth(GoogleRequest req) {
        String idTokenStr = String.valueOf(exchangeCodeForIdToken(req.getCode()));

        // 2) Verify id_token
        GoogleIdToken.Payload payload = verifyIdToken(idTokenStr);

        String sub = payload.getSubject();
        String email = (String) payload.get("email");
        boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
        String name = (String) payload.get("name");

        // 3) Upsert user
        Customer user = customerService.upsertGoogleUser(sub, email, name, emailVerified);

        // 4) Sinh token & trả LoginResponse
        String accessToken = "TOKEN_CUSTOMER";

        return LoginResponse.builder()
                .success(true)
                .message("Login with Google success")
                .accessToken(accessToken)
                .user(user)
                .build();
    }

    private GoogleTokenResponse exchangeCodeForIdToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("client_id", clientId);       // log ra để chắc dùng đúng client
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", "postmessage");

        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .exchangeToMono(resp -> {
                    if (resp.statusCode().is2xxSuccessful()) {
                        return resp.bodyToMono(GoogleTokenResponse.class);
                    }
                    // Đọc body lỗi từ Google để biết chính xác nguyên nhân
                    return resp.bodyToMono(String.class).flatMap(body -> {
                        String msg = "Google token error " + resp.statusCode()
                                + " :: " + body;
                        return Mono.error(new IllegalArgumentException(msg));
                    });
                })
                .block();
    }


    private GoogleIdToken.Payload verifyIdToken(String idTokenStr) {
        try {
            var transport = new NetHttpTransport();
            var jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenStr);
            if (idToken == null) throw new SecurityException("Invalid Google ID token");
            return idToken.getPayload();
        } catch (Exception e) {
            throw new SecurityException("Verify Google token failed");
        }
    }

    // record cho JSON mapping
    public record GoogleTokenResponse(
            String access_token, Long expires_in, String id_token,
            String scope, String token_type, String refresh_token
    ) {}
}