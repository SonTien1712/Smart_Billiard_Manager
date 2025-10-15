package com.BillardManagement.DTO.Response;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeLoginResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private EmployeeUserView user;

    public static EmployeeLoginResponse success(String msg, String token, EmployeeUserView user) {
        return EmployeeLoginResponse.builder()
                .success(true).message(msg).accessToken(token).user(user).build();
    }
    public static EmployeeLoginResponse fail(String msg) {
        return EmployeeLoginResponse.builder()
                .success(false).message(msg).accessToken(null).user(null).build();
    }
<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes
