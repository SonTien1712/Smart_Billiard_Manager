package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Shift
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class Shift {

  private Integer shiftId;

  private Integer employeeId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate shiftDate;

  private String startTime;

  private String endTime;

  private BigDecimal hoursWorked;

  private String shiftType;

  private String status;

  public Shift shiftId(Integer shiftId) {
    this.shiftId = shiftId;
    return this;
  }

  /**
   * Get shiftId
   * @return shiftId
   */
  
  @Schema(name = "shift_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shift_id")
  public Integer getShiftId() {
    return shiftId;
  }

  public void setShiftId(Integer shiftId) {
    this.shiftId = shiftId;
  }

  public Shift employeeId(Integer employeeId) {
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

  public Shift shiftDate(LocalDate shiftDate) {
    this.shiftDate = shiftDate;
    return this;
  }

  /**
   * Get shiftDate
   * @return shiftDate
   */
  @Valid 
  @Schema(name = "shift_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shift_date")
  public LocalDate getShiftDate() {
    return shiftDate;
  }

  public void setShiftDate(LocalDate shiftDate) {
    this.shiftDate = shiftDate;
  }

  public Shift startTime(String startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * Get startTime
   * @return startTime
   */
  
  @Schema(name = "start_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start_time")
  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public Shift endTime(String endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * Get endTime
   * @return endTime
   */
  
  @Schema(name = "end_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end_time")
  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Shift hoursWorked(BigDecimal hoursWorked) {
    this.hoursWorked = hoursWorked;
    return this;
  }

  /**
   * Get hoursWorked
   * @return hoursWorked
   */
  @Valid 
  @Schema(name = "hours_worked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hours_worked")
  public BigDecimal getHoursWorked() {
    return hoursWorked;
  }

  public void setHoursWorked(BigDecimal hoursWorked) {
    this.hoursWorked = hoursWorked;
  }

  public Shift shiftType(String shiftType) {
    this.shiftType = shiftType;
    return this;
  }

  /**
   * Get shiftType
   * @return shiftType
   */
  
  @Schema(name = "shift_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shift_type")
  public String getShiftType() {
    return shiftType;
  }

  public void setShiftType(String shiftType) {
    this.shiftType = shiftType;
  }

  public Shift status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Shift shift = (Shift) o;
    return Objects.equals(this.shiftId, shift.shiftId) &&
        Objects.equals(this.employeeId, shift.employeeId) &&
        Objects.equals(this.shiftDate, shift.shiftDate) &&
        Objects.equals(this.startTime, shift.startTime) &&
        Objects.equals(this.endTime, shift.endTime) &&
        Objects.equals(this.hoursWorked, shift.hoursWorked) &&
        Objects.equals(this.shiftType, shift.shiftType) &&
        Objects.equals(this.status, shift.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shiftId, employeeId, shiftDate, startTime, endTime, hoursWorked, shiftType, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Shift {\n");
    sb.append("    shiftId: ").append(toIndentedString(shiftId)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    shiftDate: ").append(toIndentedString(shiftDate)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    hoursWorked: ").append(toIndentedString(hoursWorked)).append("\n");
    sb.append("    shiftType: ").append(toIndentedString(shiftType)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

