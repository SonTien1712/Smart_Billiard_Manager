package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EmployeeCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class EmployeeCreate {

  private String employeeName;

  /**
   * Gets or Sets employeeType
   */
  public enum EmployeeTypeEnum {
    FULL_TIME("FullTime"),
    
    PART_TIME("PartTime");

    private String value;

    EmployeeTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EmployeeTypeEnum fromValue(String value) {
      for (EmployeeTypeEnum b : EmployeeTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private EmployeeTypeEnum employeeType;

  private String phoneNumber;

  private String email;

  private String address;

  private BigDecimal salary;

  private BigDecimal hourlyRate;

  private String bankNumber;

  private String bankName;

  public EmployeeCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EmployeeCreate(String employeeName, EmployeeTypeEnum employeeType, String phoneNumber, String email) {
    this.employeeName = employeeName;
    this.employeeType = employeeType;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

  public EmployeeCreate employeeName(String employeeName) {
    this.employeeName = employeeName;
    return this;
  }

  /**
   * Get employeeName
   * @return employeeName
   */
  @NotNull 
  @Schema(name = "employee_name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("employee_name")
  public String getEmployeeName() {
    return employeeName;
  }

  public void setEmployeeName(String employeeName) {
    this.employeeName = employeeName;
  }

  public EmployeeCreate employeeType(EmployeeTypeEnum employeeType) {
    this.employeeType = employeeType;
    return this;
  }

  /**
   * Get employeeType
   * @return employeeType
   */
  @NotNull 
  @Schema(name = "employee_type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("employee_type")
  public EmployeeTypeEnum getEmployeeType() {
    return employeeType;
  }

  public void setEmployeeType(EmployeeTypeEnum employeeType) {
    this.employeeType = employeeType;
  }

  public EmployeeCreate phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  @NotNull 
  @Schema(name = "phone_number", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("phone_number")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public EmployeeCreate email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @NotNull @javax.validation.constraints.Email 
  @Schema(name = "email", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public EmployeeCreate address(String address) {
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

  public EmployeeCreate salary(BigDecimal salary) {
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

  public EmployeeCreate hourlyRate(BigDecimal hourlyRate) {
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

  public EmployeeCreate bankNumber(String bankNumber) {
    this.bankNumber = bankNumber;
    return this;
  }

  /**
   * Get bankNumber
   * @return bankNumber
   */
  
  @Schema(name = "bank_number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bank_number")
  public String getBankNumber() {
    return bankNumber;
  }

  public void setBankNumber(String bankNumber) {
    this.bankNumber = bankNumber;
  }

  public EmployeeCreate bankName(String bankName) {
    this.bankName = bankName;
    return this;
  }

  /**
   * Get bankName
   * @return bankName
   */
  
  @Schema(name = "bank_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bank_name")
  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeCreate employeeCreate = (EmployeeCreate) o;
    return Objects.equals(this.employeeName, employeeCreate.employeeName) &&
        Objects.equals(this.employeeType, employeeCreate.employeeType) &&
        Objects.equals(this.phoneNumber, employeeCreate.phoneNumber) &&
        Objects.equals(this.email, employeeCreate.email) &&
        Objects.equals(this.address, employeeCreate.address) &&
        Objects.equals(this.salary, employeeCreate.salary) &&
        Objects.equals(this.hourlyRate, employeeCreate.hourlyRate) &&
        Objects.equals(this.bankNumber, employeeCreate.bankNumber) &&
        Objects.equals(this.bankName, employeeCreate.bankName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(employeeName, employeeType, phoneNumber, email, address, salary, hourlyRate, bankNumber, bankName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmployeeCreate {\n");
    sb.append("    employeeName: ").append(toIndentedString(employeeName)).append("\n");
    sb.append("    employeeType: ").append(toIndentedString(employeeType)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    salary: ").append(toIndentedString(salary)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
    sb.append("    bankNumber: ").append(toIndentedString(bankNumber)).append("\n");
    sb.append("    bankName: ").append(toIndentedString(bankName)).append("\n");
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

