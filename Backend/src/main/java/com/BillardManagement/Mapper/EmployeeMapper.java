package com.BillardManagement.Mapper;

import com.BillardManagement.DTO.Request.EmployeeRequest;
import com.BillardManagement.DTO.Response.EmployeeResponse;
import com.BillardManagement.Entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    // Chuyển Entity → Response
    public EmployeeResponse toResponse(Employee e) {
        EmployeeResponse dto = new EmployeeResponse();
        dto.setId(e.getId());
        dto.setEmployeeName(e.getEmployeeName());
        dto.setEmployeeType(e.getEmployeeType());
        dto.setPhoneNumber(e.getPhoneNumber());
        dto.setEmail(e.getEmail());
        dto.setAddress(e.getAddress());
        dto.setHourlyRate(e.getHourlyRate());
        dto.setSalary(e.getSalary());
        dto.setDateHired(e.getDateHired());
        dto.setBankNumber(e.getBankNumber());
        dto.setBankName(e.getBankName());
        dto.setIsActive(e.getIsActive());

        if (e.getClubID() != null) {
            dto.setClubId(e.getClubID().getId());
            dto.setClubName(e.getClubID().getClubName());
        }
        if (e.getCustomerID() != null) {
            dto.setCustomerId(e.getCustomerID().getId());
            dto.setCustomerName(e.getCustomerID().getCustomerName());
        }

        return dto;
    }

    // Chuyển Request → Entity
    public Employee toEntity(EmployeeRequest req) {
        Employee e = new Employee();
        e.setEmployeeName(req.getEmployeeName());
        e.setEmployeeType(req.getEmployeeType());
        e.setPhoneNumber(req.getPhoneNumber());
        e.setEmail(req.getEmail());
        e.setAddress(req.getAddress());
        e.setHourlyRate(req.getHourlyRate());
        e.setDateHired(req.getDateHired());
        e.setBankNumber(req.getBankNumber());
        e.setBankName(req.getBankName());
        e.setIsActive(req.getIsActive());
        return e;
    }
}
