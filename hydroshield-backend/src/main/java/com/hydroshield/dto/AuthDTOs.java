package com.hydroshield.dto;

import lombok.*;

public class AuthDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicRegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String confirmPassword;
        private String phoneNumber;
        private String locality;
        private String district;
        private String state;
        private String pinCode;
        private String waterSource; // Borewell, Open Well, Municipal/Tap, Other
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResearcherRegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String confirmPassword;
        private String phoneNumber;
        private String organization;
        private String designation;
        private String researchArea;
        private String district;
        private String state;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String token;
        private Long userId;
        private String name;
        private String email;
        private String role;
        private String status;
        private String message;
    }
}
