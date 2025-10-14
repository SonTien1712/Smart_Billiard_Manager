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
 * RevenueReport
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class RevenueReport {

  private BigDecimal totalRevenue;

  private BigDecimal tableRevenue;

  private BigDecimal productRevenue;

  private BigDecimal totalDiscount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime periodFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime periodTo;

  public RevenueReport totalRevenue(BigDecimal totalRevenue) {
    this.totalRevenue = totalRevenue;
    return this;
  }

  /**
   * Get totalRevenue
   * @return totalRevenue
   */
  @Valid 
  @Schema(name = "total_revenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_revenue")
  public BigDecimal getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(BigDecimal totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  public RevenueReport tableRevenue(BigDecimal tableRevenue) {
    this.tableRevenue = tableRevenue;
    return this;
  }

  /**
   * Get tableRevenue
   * @return tableRevenue
   */
  @Valid 
  @Schema(name = "table_revenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_revenue")
  public BigDecimal getTableRevenue() {
    return tableRevenue;
  }

  public void setTableRevenue(BigDecimal tableRevenue) {
    this.tableRevenue = tableRevenue;
  }

  public RevenueReport productRevenue(BigDecimal productRevenue) {
    this.productRevenue = productRevenue;
    return this;
  }

  /**
   * Get productRevenue
   * @return productRevenue
   */
  @Valid 
  @Schema(name = "product_revenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("product_revenue")
  public BigDecimal getProductRevenue() {
    return productRevenue;
  }

  public void setProductRevenue(BigDecimal productRevenue) {
    this.productRevenue = productRevenue;
  }

  public RevenueReport totalDiscount(BigDecimal totalDiscount) {
    this.totalDiscount = totalDiscount;
    return this;
  }

  /**
   * Get totalDiscount
   * @return totalDiscount
   */
  @Valid 
  @Schema(name = "total_discount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_discount")
  public BigDecimal getTotalDiscount() {
    return totalDiscount;
  }

  public void setTotalDiscount(BigDecimal totalDiscount) {
    this.totalDiscount = totalDiscount;
  }

  public RevenueReport periodFrom(OffsetDateTime periodFrom) {
    this.periodFrom = periodFrom;
    return this;
  }

  /**
   * Get periodFrom
   * @return periodFrom
   */
  @Valid 
  @Schema(name = "period_from", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("period_from")
  public OffsetDateTime getPeriodFrom() {
    return periodFrom;
  }

  public void setPeriodFrom(OffsetDateTime periodFrom) {
    this.periodFrom = periodFrom;
  }

  public RevenueReport periodTo(OffsetDateTime periodTo) {
    this.periodTo = periodTo;
    return this;
  }

  /**
   * Get periodTo
   * @return periodTo
   */
  @Valid 
  @Schema(name = "period_to", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("period_to")
  public OffsetDateTime getPeriodTo() {
    return periodTo;
  }

  public void setPeriodTo(OffsetDateTime periodTo) {
    this.periodTo = periodTo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RevenueReport revenueReport = (RevenueReport) o;
    return Objects.equals(this.totalRevenue, revenueReport.totalRevenue) &&
        Objects.equals(this.tableRevenue, revenueReport.tableRevenue) &&
        Objects.equals(this.productRevenue, revenueReport.productRevenue) &&
        Objects.equals(this.totalDiscount, revenueReport.totalDiscount) &&
        Objects.equals(this.periodFrom, revenueReport.periodFrom) &&
        Objects.equals(this.periodTo, revenueReport.periodTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalRevenue, tableRevenue, productRevenue, totalDiscount, periodFrom, periodTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RevenueReport {\n");
    sb.append("    totalRevenue: ").append(toIndentedString(totalRevenue)).append("\n");
    sb.append("    tableRevenue: ").append(toIndentedString(tableRevenue)).append("\n");
    sb.append("    productRevenue: ").append(toIndentedString(productRevenue)).append("\n");
    sb.append("    totalDiscount: ").append(toIndentedString(totalDiscount)).append("\n");
    sb.append("    periodFrom: ").append(toIndentedString(periodFrom)).append("\n");
    sb.append("    periodTo: ").append(toIndentedString(periodTo)).append("\n");
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

