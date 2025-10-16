package com.BillardManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "billardclub")
public class Billardclub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClubID", nullable = false)
    private Integer id;


    @Column(name = "CustomerID", nullable = false)
    private Integer customerID;

    @Column(name = "ClubName", nullable = false)
    private String clubName;

    @Lob
    @Column(name = "Address", nullable = false)
    private String address;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

}
