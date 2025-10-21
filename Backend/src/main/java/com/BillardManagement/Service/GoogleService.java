package com.BillardManagement.Service;

import com.BillardManagement.DTO.Request.GoogleRequest;
import com.BillardManagement.DTO.Response.LoginResponse;

public interface GoogleService {
    LoginResponse handleGoogleAuth(GoogleRequest request);
}
