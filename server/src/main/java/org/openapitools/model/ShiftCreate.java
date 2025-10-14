package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * ShiftCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class ShiftCreate {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate shiftDate;

  private String startTime;

  private String endTime;

  /**
   * Gets or Sets shiftType
   */
  public enum ShiftTypeEnum {
    S_NG("Sáng"),
    
    CHI_U("Chiều"),
    
    T_I("Tối"),
    
    _M("Đêm");

    private String value;

    ShiftTypeEnum(String value) {
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
    public static ShiftTypeEnum fromValue(String value) {
      for (ShiftTypeEnum b : ShiftTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ShiftTypeEnum shiftType;

  public ShiftCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ShiftCreate(LocalDate shiftDate, ShiftTypeEnum shiftType) {
    this.shiftDate = shiftDate;
    this.shiftType = shiftType;
  }

  public ShiftCreate shiftDate(LocalDate shiftDate) {
    this.shiftDate = shiftDate;
    return this;
  }

  /**
   * Get shiftDate
   * @return shiftDate
   */
  @NotNull @Valid 
  @Schema(name = "shift_date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("shift_date")
  public LocalDate getShiftDate() {
    return shiftDate;
  }

  public void setShiftDate(LocalDate shiftDate) {
    this.shiftDate = shiftDate;
  }

  public ShiftCreate startTime(String startTime) {
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

  public ShiftCreate endTime(String endTime) {
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

  public ShiftCreate shiftType(ShiftTypeEnum shiftType) {
    this.shiftType = shiftType;
    return this;
  }

  /**
   * Get shiftType
   * @return shiftType
   */
  @NotNull 
  @Schema(name = "shift_type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("shift_type")
  public ShiftTypeEnum getShiftType() {
    return shiftType;
  }

  public void setShiftType(ShiftTypeEnum shiftType) {
    this.shiftType = shiftType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShiftCreate shiftCreate = (ShiftCreate) o;
    return Objects.equals(this.shiftDate, shiftCreate.shiftDate) &&
        Objects.equals(this.startTime, shiftCreate.startTime) &&
        Objects.equals(this.endTime, shiftCreate.endTime) &&
        Objects.equals(this.shiftType, shiftCreate.shiftType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shiftDate, startTime, endTime, shiftType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShiftCreate {\n");
    sb.append("    shiftDate: ").append(toIndentedString(shiftDate)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
    sb.append("    shiftType: ").append(toIndentedString(shiftType)).append("\n");
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

