package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.model.StockValueReportByCategoryInner;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * StockValueReport
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class StockValueReport {

  private @Nullable BigDecimal totalValue;

  @Valid
  private List<@Valid StockValueReportByCategoryInner> byCategory = new ArrayList<>();

  public StockValueReport totalValue(@Nullable BigDecimal totalValue) {
    this.totalValue = totalValue;
    return this;
  }

  /**
   * Get totalValue
   * @return totalValue
   */
  @Valid 
  @Schema(name = "total_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_value")
  public @Nullable BigDecimal getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(@Nullable BigDecimal totalValue) {
    this.totalValue = totalValue;
  }

  public StockValueReport byCategory(List<@Valid StockValueReportByCategoryInner> byCategory) {
    this.byCategory = byCategory;
    return this;
  }

  public StockValueReport addByCategoryItem(StockValueReportByCategoryInner byCategoryItem) {
    if (this.byCategory == null) {
      this.byCategory = new ArrayList<>();
    }
    this.byCategory.add(byCategoryItem);
    return this;
  }

  /**
   * Get byCategory
   * @return byCategory
   */
  @Valid 
  @Schema(name = "by_category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("by_category")
  public List<@Valid StockValueReportByCategoryInner> getByCategory() {
    return byCategory;
  }

  public void setByCategory(List<@Valid StockValueReportByCategoryInner> byCategory) {
    this.byCategory = byCategory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockValueReport stockValueReport = (StockValueReport) o;
    return Objects.equals(this.totalValue, stockValueReport.totalValue) &&
        Objects.equals(this.byCategory, stockValueReport.byCategory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalValue, byCategory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockValueReport {\n");
    sb.append("    totalValue: ").append(toIndentedString(totalValue)).append("\n");
    sb.append("    byCategory: ").append(toIndentedString(byCategory)).append("\n");
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

