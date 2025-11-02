package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RevenueReportTotalsByDayInner
 */

@JsonTypeName("RevenueReport_totals_by_day_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class RevenueReportTotalsByDayInner {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate day;

  private @Nullable BigDecimal revenue;

  public RevenueReportTotalsByDayInner day(@Nullable LocalDate day) {
    this.day = day;
    return this;
  }

  /**
   * Get day
   * @return day
   */
  @Valid 
  @Schema(name = "day", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("day")
  public @Nullable LocalDate getDay() {
    return day;
  }

  public void setDay(@Nullable LocalDate day) {
    this.day = day;
  }

  public RevenueReportTotalsByDayInner revenue(@Nullable BigDecimal revenue) {
    this.revenue = revenue;
    return this;
  }

  /**
   * Get revenue
   * @return revenue
   */
  @Valid 
  @Schema(name = "revenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revenue")
  public @Nullable BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(@Nullable BigDecimal revenue) {
    this.revenue = revenue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RevenueReportTotalsByDayInner revenueReportTotalsByDayInner = (RevenueReportTotalsByDayInner) o;
    return Objects.equals(this.day, revenueReportTotalsByDayInner.day) &&
        Objects.equals(this.revenue, revenueReportTotalsByDayInner.revenue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, revenue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RevenueReportTotalsByDayInner {\n");
    sb.append("    day: ").append(toIndentedString(day)).append("\n");
    sb.append("    revenue: ").append(toIndentedString(revenue)).append("\n");
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

