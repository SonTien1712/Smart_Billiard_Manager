package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * BillCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class BillCreate {

  private Integer tableId;

  private Integer employeeId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startTime;

  private Integer promotionId;

  public BillCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BillCreate(Integer tableId, Integer employeeId) {
    this.tableId = tableId;
    this.employeeId = employeeId;
  }

  public BillCreate tableId(Integer tableId) {
    this.tableId = tableId;
    return this;
  }

  /**
   * Get tableId
   * @return tableId
   */
  @NotNull 
  @Schema(name = "table_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("table_id")
  public Integer getTableId() {
    return tableId;
  }

  public void setTableId(Integer tableId) {
    this.tableId = tableId;
  }

  public BillCreate employeeId(Integer employeeId) {
    this.employeeId = employeeId;
    return this;
  }

  /**
   * Get employeeId
   * @return employeeId
   */
  @NotNull 
  @Schema(name = "employee_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("employee_id")
  public Integer getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
  }

  public BillCreate startTime(OffsetDateTime startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * Get startTime
   * @return startTime
   */
  @Valid 
  @Schema(name = "start_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start_time")
  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public BillCreate promotionId(Integer promotionId) {
    this.promotionId = promotionId;
    return this;
  }

  /**
   * Get promotionId
   * @return promotionId
   */
  
  @Schema(name = "promotion_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotion_id")
  public Integer getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(Integer promotionId) {
    this.promotionId = promotionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillCreate billCreate = (BillCreate) o;
    return Objects.equals(this.tableId, billCreate.tableId) &&
        Objects.equals(this.employeeId, billCreate.employeeId) &&
        Objects.equals(this.startTime, billCreate.startTime) &&
        Objects.equals(this.promotionId, billCreate.promotionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tableId, employeeId, startTime, promotionId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillCreate {\n");
    sb.append("    tableId: ").append(toIndentedString(tableId)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    promotionId: ").append(toIndentedString(promotionId)).append("\n");
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

