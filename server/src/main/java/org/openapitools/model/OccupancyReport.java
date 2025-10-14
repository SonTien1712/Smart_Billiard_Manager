package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.OccupancyReportByTableTypeInner;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OccupancyReport
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class OccupancyReport {

  private BigDecimal totalHoursAvailable;

  private BigDecimal totalHoursUsed;

  private BigDecimal occupancyRate;

  @Valid
  private List<@Valid OccupancyReportByTableTypeInner> byTableType = new ArrayList<>();

  public OccupancyReport totalHoursAvailable(BigDecimal totalHoursAvailable) {
    this.totalHoursAvailable = totalHoursAvailable;
    return this;
  }

  /**
   * Get totalHoursAvailable
   * @return totalHoursAvailable
   */
  @Valid 
  @Schema(name = "total_hours_available", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_hours_available")
  public BigDecimal getTotalHoursAvailable() {
    return totalHoursAvailable;
  }

  public void setTotalHoursAvailable(BigDecimal totalHoursAvailable) {
    this.totalHoursAvailable = totalHoursAvailable;
  }

  public OccupancyReport totalHoursUsed(BigDecimal totalHoursUsed) {
    this.totalHoursUsed = totalHoursUsed;
    return this;
  }

  /**
   * Get totalHoursUsed
   * @return totalHoursUsed
   */
  @Valid 
  @Schema(name = "total_hours_used", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_hours_used")
  public BigDecimal getTotalHoursUsed() {
    return totalHoursUsed;
  }

  public void setTotalHoursUsed(BigDecimal totalHoursUsed) {
    this.totalHoursUsed = totalHoursUsed;
  }

  public OccupancyReport occupancyRate(BigDecimal occupancyRate) {
    this.occupancyRate = occupancyRate;
    return this;
  }

  /**
   * Get occupancyRate
   * @return occupancyRate
   */
  @Valid 
  @Schema(name = "occupancy_rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("occupancy_rate")
  public BigDecimal getOccupancyRate() {
    return occupancyRate;
  }

  public void setOccupancyRate(BigDecimal occupancyRate) {
    this.occupancyRate = occupancyRate;
  }

  public OccupancyReport byTableType(List<@Valid OccupancyReportByTableTypeInner> byTableType) {
    this.byTableType = byTableType;
    return this;
  }

  public OccupancyReport addByTableTypeItem(OccupancyReportByTableTypeInner byTableTypeItem) {
    if (this.byTableType == null) {
      this.byTableType = new ArrayList<>();
    }
    this.byTableType.add(byTableTypeItem);
    return this;
  }

  /**
   * Get byTableType
   * @return byTableType
   */
  @Valid 
  @Schema(name = "by_table_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("by_table_type")
  public List<@Valid OccupancyReportByTableTypeInner> getByTableType() {
    return byTableType;
  }

  public void setByTableType(List<@Valid OccupancyReportByTableTypeInner> byTableType) {
    this.byTableType = byTableType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OccupancyReport occupancyReport = (OccupancyReport) o;
    return Objects.equals(this.totalHoursAvailable, occupancyReport.totalHoursAvailable) &&
        Objects.equals(this.totalHoursUsed, occupancyReport.totalHoursUsed) &&
        Objects.equals(this.occupancyRate, occupancyReport.occupancyRate) &&
        Objects.equals(this.byTableType, occupancyReport.byTableType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalHoursAvailable, totalHoursUsed, occupancyRate, byTableType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupancyReport {\n");
    sb.append("    totalHoursAvailable: ").append(toIndentedString(totalHoursAvailable)).append("\n");
    sb.append("    totalHoursUsed: ").append(toIndentedString(totalHoursUsed)).append("\n");
    sb.append("    occupancyRate: ").append(toIndentedString(occupancyRate)).append("\n");
    sb.append("    byTableType: ").append(toIndentedString(byTableType)).append("\n");
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

