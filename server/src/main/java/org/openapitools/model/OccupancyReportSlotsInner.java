package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OccupancyReportSlotsInner
 */

@JsonTypeName("OccupancyReport_slots_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class OccupancyReportSlotsInner {

  private @Nullable String slot;

  private @Nullable BigDecimal occupancyPct;

  public OccupancyReportSlotsInner slot(@Nullable String slot) {
    this.slot = slot;
    return this;
  }

  /**
   * Get slot
   * @return slot
   */
  
  @Schema(name = "slot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("slot")
  public @Nullable String getSlot() {
    return slot;
  }

  public void setSlot(@Nullable String slot) {
    this.slot = slot;
  }

  public OccupancyReportSlotsInner occupancyPct(@Nullable BigDecimal occupancyPct) {
    this.occupancyPct = occupancyPct;
    return this;
  }

  /**
   * Get occupancyPct
   * @return occupancyPct
   */
  @Valid 
  @Schema(name = "occupancy_pct", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("occupancy_pct")
  public @Nullable BigDecimal getOccupancyPct() {
    return occupancyPct;
  }

  public void setOccupancyPct(@Nullable BigDecimal occupancyPct) {
    this.occupancyPct = occupancyPct;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OccupancyReportSlotsInner occupancyReportSlotsInner = (OccupancyReportSlotsInner) o;
    return Objects.equals(this.slot, occupancyReportSlotsInner.slot) &&
        Objects.equals(this.occupancyPct, occupancyReportSlotsInner.occupancyPct);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slot, occupancyPct);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OccupancyReportSlotsInner {\n");
    sb.append("    slot: ").append(toIndentedString(slot)).append("\n");
    sb.append("    occupancyPct: ").append(toIndentedString(occupancyPct)).append("\n");
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

