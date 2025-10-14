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
 * TableUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class TableUpdate {

  private String tableName;

  /**
   * Gets or Sets tableType
   */
  public enum TableTypeEnum {
    CAROM("Carom"),
    
    PH_NG("Phăng"),
    
    L_("Lỗ");

    private String value;

    TableTypeEnum(String value) {
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
    public static TableTypeEnum fromValue(String value) {
      for (TableTypeEnum b : TableTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TableTypeEnum tableType;

  private BigDecimal hourlyRate;

  /**
   * Gets or Sets tableStatus
   */
  public enum TableStatusEnum {
    AVAILABLE("Available"),
    
    IN_USE("InUse"),
    
    MAINTENANCE("Maintenance");

    private String value;

    TableStatusEnum(String value) {
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
    public static TableStatusEnum fromValue(String value) {
      for (TableStatusEnum b : TableStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TableStatusEnum tableStatus;

  private String location;

  public TableUpdate tableName(String tableName) {
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

  public TableUpdate tableType(TableTypeEnum tableType) {
    this.tableType = tableType;
    return this;
  }

  /**
   * Get tableType
   * @return tableType
   */
  
  @Schema(name = "table_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_type")
  public TableTypeEnum getTableType() {
    return tableType;
  }

  public void setTableType(TableTypeEnum tableType) {
    this.tableType = tableType;
  }

  public TableUpdate hourlyRate(BigDecimal hourlyRate) {
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

  public TableUpdate tableStatus(TableStatusEnum tableStatus) {
    this.tableStatus = tableStatus;
    return this;
  }

  /**
   * Get tableStatus
   * @return tableStatus
   */
  
  @Schema(name = "table_status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_status")
  public TableStatusEnum getTableStatus() {
    return tableStatus;
  }

  public void setTableStatus(TableStatusEnum tableStatus) {
    this.tableStatus = tableStatus;
  }

  public TableUpdate location(String location) {
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
    TableUpdate tableUpdate = (TableUpdate) o;
    return Objects.equals(this.tableName, tableUpdate.tableName) &&
        Objects.equals(this.tableType, tableUpdate.tableType) &&
        Objects.equals(this.hourlyRate, tableUpdate.hourlyRate) &&
        Objects.equals(this.tableStatus, tableUpdate.tableStatus) &&
        Objects.equals(this.location, tableUpdate.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tableName, tableType, hourlyRate, tableStatus, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TableUpdate {\n");
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

