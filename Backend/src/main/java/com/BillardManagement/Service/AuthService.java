package com.BillardManagement.Service;

import com.BillardManagement.DTO.Request.LoginRequest;
import com.BillardManagement.DTO.Response.EmployeeLoginResponse;
import com.BillardManagement.DTO.Response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

}