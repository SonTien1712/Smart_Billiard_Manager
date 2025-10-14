package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Employee
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class Employee {

  private Integer employeeId;

  private Integer clubId;

  private String employeeName;

  private String employeeType;

  private String phoneNumber;

  private String email;

  private String address;

  private BigDecimal salary;

  private BigDecimal hourlyRate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime dateHired;

  private Boolean isActive;

  public Employee employeeId(Integer employeeId) {
    this.employeeId = employeeId;
    return this;
  }

  /**
   * Get employeeId
   * @return employeeId
   */
  
  @Schema(name = "employee_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employee_id")
  public Integer getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
  }

  public Employee clubId(Integer clubId) {
    this.clubId = clubId;
    return this;
  }

  /**
   * Get clubId
   * @return clubId
   */
  
  @Schema(name = "club_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("club_id")
  public Integer getClubId() {
    return clubId;
  }

  public void setClubId(Integer clubId) {
    this.clubId = clubId;
  }

  public Employee employeeName(String employeeName) {
    this.employeeName = employeeName;
    return this;
  }

  /**
   * Get employeeName
   * @return employeeName
   */
  
  @Schema(name = "employee_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employee_name")
  public String getEmployeeName() {
    return employeeName;
  }

  public void setEmployeeName(String employeeName) {
    this.employeeName = employeeName;
  }

  public Employee employeeType(String employeeType) {
    this.employeeType = employeeType;
    return this;
  }

  /**
   * Get employeeType
   * @return employeeType
   */
  
  @Schema(name = "employee_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employee_type")
  public String getEmployeeType() {
    return employeeType;
  }

  public void setEmployeeType(String employeeType) {
    this.employeeType = employeeType;
  }

  public Employee phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  
  @Schema(name = "phone_number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phone_number")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Employee email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Employee address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Employee salary(BigDecimal salary) {
    this.salary = salary;
    return this;
  }

  /**
   * Get salary
   * @return salary
   */
  @Valid 
  @Schema(name = "salary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("salary")
  public BigDecimal getSalary() {
    return salary;
  }

  public void setSalary(BigDecimal salary) {
    this.salary = salary;
  }

  public Employee hourlyRate(BigDecimal hourlyRate) {
    this.hourlyRate = hourlyRate;
    return this;
  }

  /**
   * Get hourlyRate
   * @return hourlyRate
   */
  @Valid 
  @Schema(name = "hourly_rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hourly_rate")
  public BigDecimal getHourlyRate() {
    return hourlyRate;
  }

  public void setHourlyRate(BigDecimal hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  public Employee dateHired(OffsetDateTime dateHired) {
    this.dateHired = dateHired;
    return this;
  }

  /**
   * Get dateHired
   * @return dateHired
   */
  @Valid 
  @Schema(name = "date_hired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date_hired")
  public OffsetDateTime getDateHired() {
    return dateHired;
  }

  public void setDateHired(OffsetDateTime dateHired) {
    this.dateHired = dateHired;
  }

  public Employee isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Get isActive
   * @return isActive
   */
  
  @Schema(name = "is_active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_active")
  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Employee employee = (Employee) o;
    return Objects.equals(this.employeeId, employee.employeeId) &&
        Objects.equals(this.clubId, employee.clubId) &&
        Objects.equals(this.employeeName, employee.employeeName) &&
        Objects.equals(this.employeeType, employee.employeeType) &&
        Objects.equals(this.phoneNumber, employee.phoneNumber) &&
        Objects.equals(this.email, employee.email) &&
        Objects.equals(this.address, employee.address) &&
        Objects.equals(this.salary, employee.salary) &&
        Objects.equals(this.hourlyRate, employee.hourlyRate) &&
        Objects.equals(this.dateHired, employee.dateHired) &&
        Objects.equals(this.isActive, employee.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(employeeId, clubId, employeeName, employeeType, phoneNumber, email, address, salary, hourlyRate, dateHired, isActive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Employee {\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    clubId: ").append(toIndentedString(clubId)).append("\n");
    sb.append("    employeeName: ").append(toIndentedString(employeeName)).append("\n");
    sb.append("    employeeType: ").append(toIndentedString(employeeType)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    salary: ").append(toIndentedString(salary)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
    sb.append("    dateHired: ").append(toIndentedString(dateHired)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

