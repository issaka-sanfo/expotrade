package com.isanf.expotrade.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityProbeController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    private static final UUID USER_ID = UUID.fromString("8ac0d1dd-83c8-4e78-8fb1-eab109b165d3");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void businessEndpointRejectsAnonymousRequest() throws Exception {
        mockMvc.perform(get("/api/v1/security-probe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void businessEndpointAcceptsValidJwtAndReadsUserIdentityFromSubject() throws Exception {
        mockMvc.perform(get("/api/v1/security-probe")
                        .with(jwt().jwt(jwt -> jwt.subject(USER_ID.toString()))))
                .andExpect(status().isOk())
                .andExpect(content().string(USER_ID.toString()));
    }

    @Test
    void businessEndpointRejectsInvalidToken() throws Exception {
        when(jwtDecoder.decode("invalid-token")).thenThrow(new BadJwtException("Invalid token"));

        mockMvc.perform(get("/api/v1/security-probe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void technicalEndpointsAreNotBlockedByAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

}

@RestController
class SecurityProbeController {

    @GetMapping("/api/v1/security-probe")
    String probe(@AuthenticationPrincipal Jwt jwt) {
        return AuthenticatedUser.id(jwt).toString();
    }

    @GetMapping("/actuator/health")
    String health() {
        return "UP";
    }

    @GetMapping("/v3/api-docs")
    String apiDocs() {
        return "{}";
    }
}
