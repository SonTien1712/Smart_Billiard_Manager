package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.RevenueReportTotalsByDayInner;
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
 * RevenueReport
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class RevenueReport {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime from;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime to;

  @Valid
  private List<@Valid RevenueReportTotalsByDayInner> totalsByDay = new ArrayList<>();

  public RevenueReport from(@Nullable OffsetDateTime from) {
    this.from = from;
    return this;
  }

  /**
   * Get from
   * @return from
   */
  @Valid 
  @Schema(name = "from", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("from")
  public @Nullable OffsetDateTime getFrom() {
    return from;
  }

  public void setFrom(@Nullable OffsetDateTime from) {
    this.from = from;
  }

  public RevenueReport to(@Nullable OffsetDateTime to) {
    this.to = to;
    return this;
  }

  /**
   * Get to
   * @return to
   */
  @Valid 
  @Schema(name = "to", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("to")
  public @Nullable OffsetDateTime getTo() {
    return to;
  }

  public void setTo(@Nullable OffsetDateTime to) {
    this.to = to;
  }

  public RevenueReport totalsByDay(List<@Valid RevenueReportTotalsByDayInner> totalsByDay) {
    this.totalsByDay = totalsByDay;
    return this;
  }

  public RevenueReport addTotalsByDayItem(RevenueReportTotalsByDayInner totalsByDayItem) {
    if (this.totalsByDay == null) {
      this.totalsByDay = new ArrayList<>();
    }
    this.totalsByDay.add(totalsByDayItem);
    return this;
  }

  /**
   * Get totalsByDay
   * @return totalsByDay
   */
  @Valid 
  @Schema(name = "totals_by_day", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totals_by_day")
  public List<@Valid RevenueReportTotalsByDayInner> getTotalsByDay() {
    return totalsByDay;
  }

  public void setTotalsByDay(List<@Valid RevenueReportTotalsByDayInner> totalsByDay) {
    this.totalsByDay = totalsByDay;
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
    return Objects.equals(this.from, revenueReport.from) &&
        Objects.equals(this.to, revenueReport.to) &&
        Objects.equals(this.totalsByDay, revenueReport.totalsByDay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, totalsByDay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RevenueReport {\n");
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
    sb.append("    totalsByDay: ").append(toIndentedString(totalsByDay)).append("\n");
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

