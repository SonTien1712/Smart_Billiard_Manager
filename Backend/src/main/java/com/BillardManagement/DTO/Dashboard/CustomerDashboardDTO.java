package com.BillardManagement.DTO.Dashboard;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerDashboardDTO {
    private Integer customerId;
    private List<ClubDashboardDTO> clubs;

    // getters/setters, constructor
}
