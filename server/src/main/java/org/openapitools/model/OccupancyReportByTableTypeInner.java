package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OccupancyReportByTableTypeInner
 */

@JsonTypeName("OccupancyReport_by_table_type_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class OccupancyReportByTableTypeInner {

  private String tableType;

  private BigDecimal occupancyRate;

  public OccupancyReportByTableTypeInner tableType(String tableType) {
    this.tableType = tableType;
    return this;
  }

  /**
   * Get tableType
   * @return tableType
   */
  
  @Schema(name = "table_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_type")
  public String getTableType() {
    return tableType;
  }

  public void setTableType(String tableType) {
    this.tableType = tableType;
  }

  public OccupancyReportByTableTypeInner occupancyRate(BigDecimal occupancyRate) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OccupancyReportByTableTypeInner occupancyReportByTableTypeInner = (OccupancyReportByTableTypeInner) o;
    return Objects.equals(this.tableType, occupancyReportByTableTypeInner.tableType) &&
        Objects.equals(this.occupancyRate, occupancyReportByTableTypeInner.occupancyRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tableType, occupancyRate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupancyReportByTableTypeInner {\n");
    sb.append("    tableType: ").append(toIndentedString(tableType)).append("\n");
    sb.append("    occupancyRate: ").append(toIndentedString(occupancyRate)).append("\n");
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

