package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * Bill
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class Bill {

  private Integer billId;

  private Integer clubId;

  private Integer tableId;

  private Integer employeeId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endTime;

  private BigDecimal totalHours;

  private BigDecimal totalTableCost;

  private BigDecimal totalProductCost;

  private BigDecimal discountAmount;

  private BigDecimal finalAmount;

  private String paymentMethod;

  /**
   * Gets or Sets billStatus
   */
  public enum BillStatusEnum {
    PAID("Paid"),
    
    UNPAID("Unpaid"),
    
    CANCELLED("Cancelled");

    private String value;

    BillStatusEnum(String value) {
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
    public static BillStatusEnum fromValue(String value) {
      for (BillStatusEnum b : BillStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private BillStatusEnum billStatus;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdDate;

  public Bill billId(Integer billId) {
    this.billId = billId;
    return this;
  }

  /**
   * Get billId
   * @return billId
   */
  
  @Schema(name = "bill_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bill_id")
  public Integer getBillId() {
    return billId;
  }

  public void setBillId(Integer billId) {
    this.billId = billId;
  }

  public Bill clubId(Integer clubId) {
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

  public Bill tableId(Integer tableId) {
    this.tableId = tableId;
    return this;
  }

  /**
   * Get tableId
   * @return tableId
   */
  
  @Schema(name = "table_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_id")
  public Integer getTableId() {
    return tableId;
  }

  public void setTableId(Integer tableId) {
    this.tableId = tableId;
  }

  public Bill employeeId(Integer employeeId) {
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

  public Bill startTime(OffsetDateTime startTime) {
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

  public Bill endTime(OffsetDateTime endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * Get endTime
   * @return endTime
   */
  @Valid 
  @Schema(name = "end_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end_time")
  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
  }

  public Bill totalHours(BigDecimal totalHours) {
    this.totalHours = totalHours;
    return this;
  }

  /**
   * Get totalHours
   * @return totalHours
   */
  @Valid 
  @Schema(name = "total_hours", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_hours")
  public BigDecimal getTotalHours() {
    return totalHours;
  }

  public void setTotalHours(BigDecimal totalHours) {
    this.totalHours = totalHours;
  }

  public Bill totalTableCost(BigDecimal totalTableCost) {
    this.totalTableCost = totalTableCost;
    return this;
  }

  /**
   * Get totalTableCost
   * @return totalTableCost
   */
  @Valid 
  @Schema(name = "total_table_cost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_table_cost")
  public BigDecimal getTotalTableCost() {
    return totalTableCost;
  }

  public void setTotalTableCost(BigDecimal totalTableCost) {
    this.totalTableCost = totalTableCost;
  }

  public Bill totalProductCost(BigDecimal totalProductCost) {
    this.totalProductCost = totalProductCost;
    return this;
  }

  /**
   * Get totalProductCost
   * @return totalProductCost
   */
  @Valid 
  @Schema(name = "total_product_cost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_product_cost")
  public BigDecimal getTotalProductCost() {
    return totalProductCost;
  }

  public void setTotalProductCost(BigDecimal totalProductCost) {
    this.totalProductCost = totalProductCost;
  }

  public Bill discountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
    return this;
  }

  /**
   * Get discountAmount
   * @return discountAmount
   */
  @Valid 
  @Schema(name = "discount_amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discount_amount")
  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public Bill finalAmount(BigDecimal finalAmount) {
    this.finalAmount = finalAmount;
    return this;
  }

  /**
   * Get finalAmount
   * @return finalAmount
   */
  @Valid 
  @Schema(name = "final_amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("final_amount")
  public BigDecimal getFinalAmount() {
    return finalAmount;
  }

  public void setFinalAmount(BigDecimal finalAmount) {
    this.finalAmount = finalAmount;
  }

  public Bill paymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
    return this;
  }

  /**
   * Get paymentMethod
   * @return paymentMethod
   */
  
  @Schema(name = "payment_method", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payment_method")
  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Bill billStatus(BillStatusEnum billStatus) {
    this.billStatus = billStatus;
    return this;
  }

  /**
   * Get billStatus
   * @return billStatus
   */
  
  @Schema(name = "bill_status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bill_status")
  public BillStatusEnum getBillStatus() {
    return billStatus;
  }

  public void setBillStatus(BillStatusEnum billStatus) {
    this.billStatus = billStatus;
  }

  public Bill createdDate(OffsetDateTime createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  /**
   * Get createdDate
   * @return createdDate
   */
  @Valid 
  @Schema(name = "created_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created_date")
  public OffsetDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(OffsetDateTime createdDate) {
    this.createdDate = createdDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Bill bill = (Bill) o;
    return Objects.equals(this.billId, bill.billId) &&
        Objects.equals(this.clubId, bill.clubId) &&
        Objects.equals(this.tableId, bill.tableId) &&
        Objects.equals(this.employeeId, bill.employeeId) &&
        Objects.equals(this.startTime, bill.startTime) &&
        Objects.equals(this.endTime, bill.endTime) &&
        Objects.equals(this.totalHours, bill.totalHours) &&
        Objects.equals(this.totalTableCost, bill.totalTableCost) &&
        Objects.equals(this.totalProductCost, bill.totalProductCost) &&
        Objects.equals(this.discountAmount, bill.discountAmount) &&
        Objects.equals(this.finalAmount, bill.finalAmount) &&
        Objects.equals(this.paymentMethod, bill.paymentMethod) &&
        Objects.equals(this.billStatus, bill.billStatus) &&
        Objects.equals(this.createdDate, bill.createdDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billId, clubId, tableId, employeeId, startTime, endTime, totalHours, totalTableCost, totalProductCost, discountAmount, finalAmount, paymentMethod, billStatus, createdDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Bill {\n");
    sb.append("    billId: ").append(toIndentedString(billId)).append("\n");
    sb.append("    clubId: ").append(toIndentedString(clubId)).append("\n");
    sb.append("    tableId: ").append(toIndentedString(tableId)).append("\n");
    sb.append("    employeeId: ").append(toIndentedString(employeeId)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    totalHours: ").append(toIndentedString(totalHours)).append("\n");
    sb.append("    totalTableCost: ").append(toIndentedString(totalTableCost)).append("\n");
    sb.append("    totalProductCost: ").append(toIndentedString(totalProductCost)).append("\n");
    sb.append("    discountAmount: ").append(toIndentedString(discountAmount)).append("\n");
    sb.append("    finalAmount: ").append(toIndentedString(finalAmount)).append("\n");
    sb.append("    paymentMethod: ").append(toIndentedString(paymentMethod)).append("\n");
    sb.append("    billStatus: ").append(toIndentedString(billStatus)).append("\n");
    sb.append("    createdDate: ").append(toIndentedString(createdDate)).append("\n");
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

