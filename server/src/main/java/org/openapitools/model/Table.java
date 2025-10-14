package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Table
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class Table {

  private Integer tableId;

  private Integer clubId;

  private String tableName;

  private String tableType;

  private BigDecimal hourlyRate;

  private String tableStatus;

  private String location;

  public Table tableId(Integer tableId) {
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

  public Table clubId(Integer clubId) {
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

  public Table tableName(String tableName) {
    this.tableName = tableName;
    return this;
  }

  /**
   * Get tableName
   * @return tableName
   */
  
  @Schema(name = "table_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_name")
  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public Table tableType(String tableType) {
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

  public Table hourlyRate(BigDecimal hourlyRate) {
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

  public Table tableStatus(String tableStatus) {
    this.tableStatus = tableStatus;
    return this;
  }

  /**
   * Get tableStatus
   * @return tableStatus
   */
  
  @Schema(name = "table_status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_status")
  public String getTableStatus() {
    return tableStatus;
  }

  public void setTableStatus(String tableStatus) {
    this.tableStatus = tableStatus;
  }

  public Table location(String location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
   */
  
  @Schema(name = "location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Table table = (Table) o;
    return Objects.equals(this.tableId, table.tableId) &&
        Objects.equals(this.clubId, table.clubId) &&
        Objects.equals(this.tableName, table.tableName) &&
        Objects.equals(this.tableType, table.tableType) &&
        Objects.equals(this.hourlyRate, table.hourlyRate) &&
        Objects.equals(this.tableStatus, table.tableStatus) &&
        Objects.equals(this.location, table.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tableId, clubId, tableName, tableType, hourlyRate, tableStatus, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Table {\n");
    sb.append("    tableId: ").append(toIndentedString(tableId)).append("\n");
    sb.append("    clubId: ").append(toIndentedString(clubId)).append("\n");
    sb.append("    tableName: ").append(toIndentedString(tableName)).append("\n");
    sb.append("    tableType: ").append(toIndentedString(tableType)).append("\n");
    sb.append("    hourlyRate: ").append(toIndentedString(hourlyRate)).append("\n");
    sb.append("    tableStatus: ").append(toIndentedString(tableStatus)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
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

