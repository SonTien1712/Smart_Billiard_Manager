package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.OccupancyReportSlotsInner;
import org.springframework.lang.Nullable;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class OccupancyReport {

  @Valid
  private List<@Valid OccupancyReportSlotsInner> slots = new ArrayList<>();

  public OccupancyReport slots(List<@Valid OccupancyReportSlotsInner> slots) {
    this.slots = slots;
    return this;
  }

  public OccupancyReport addSlotsItem(OccupancyReportSlotsInner slotsItem) {
    if (this.slots == null) {
      this.slots = new ArrayList<>();
    }
    this.slots.add(slotsItem);
    return this;
  }

  /**
   * Get slots
   * @return slots
   */
  @Valid 
  @Schema(name = "slots", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("slots")
  public List<@Valid OccupancyReportSlotsInner> getSlots() {
    return slots;
  }

  public void setSlots(List<@Valid OccupancyReportSlotsInner> slots) {
    this.slots = slots;
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
    return Objects.equals(this.slots, occupancyReport.slots);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slots);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupancyReport {\n");
    sb.append("    slots: ").append(toIndentedString(slots)).append("\n");
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

