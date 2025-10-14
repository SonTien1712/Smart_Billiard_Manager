package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * ShiftUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class ShiftUpdate {

  private String startTime;

  private String endTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime actualStartTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime actualEndTime;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    SCHEDULED("Scheduled"),
    
    IN_PROGRESS("InProgress"),
    
    COMPLETED("Completed"),
    
    CANCELLED("Cancelled");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  public ShiftUpdate startTime(String startTime) {
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

  public ShiftUpdate endTime(String endTime) {
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

  public ShiftUpdate actualStartTime(OffsetDateTime actualStartTime) {
    this.actualStartTime = actualStartTime;
    return this;
  }

  /**
   * Get actualStartTime
   * @return actualStartTime
   */
  @Valid 
  @Schema(name = "actual_start_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actual_start_time")
  public OffsetDateTime getActualStartTime() {
    return actualStartTime;
  }

  public void setActualStartTime(OffsetDateTime actualStartTime) {
    this.actualStartTime = actualStartTime;
  }

  public ShiftUpdate actualEndTime(OffsetDateTime actualEndTime) {
    this.actualEndTime = actualEndTime;
    return this;
  }

  /**
   * Get actualEndTime
   * @return actualEndTime
   */
  @Valid 
  @Schema(name = "actual_end_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actual_end_time")
  public OffsetDateTime getActualEndTime() {
    return actualEndTime;
  }

  public void setActualEndTime(OffsetDateTime actualEndTime) {
    this.actualEndTime = actualEndTime;
  }

  public ShiftUpdate status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
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
    ShiftUpdate shiftUpdate = (ShiftUpdate) o;
    return Objects.equals(this.startTime, shiftUpdate.startTime) &&
        Objects.equals(this.endTime, shiftUpdate.endTime) &&
        Objects.equals(this.actualStartTime, shiftUpdate.actualStartTime) &&
        Objects.equals(this.actualEndTime, shiftUpdate.actualEndTime) &&
        Objects.equals(this.status, shiftUpdate.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, endTime, actualStartTime, actualEndTime, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShiftUpdate {\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    actualStartTime: ").append(toIndentedString(actualStartTime)).append("\n");
    sb.append("    actualEndTime: ").append(toIndentedString(actualEndTime)).append("\n");
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

