package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
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
 * PromotionCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class PromotionCreate {

  private String promotionName;

  private String promotionCode;

  /**
   * Gets or Sets discountType
   */
  public enum DiscountTypeEnum {
    PERCENTAGE("Percentage"),
    
    FIXED_AMOUNT("FixedAmount");

    private String value;

    DiscountTypeEnum(String value) {
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
    public static DiscountTypeEnum fromValue(String value) {
      for (DiscountTypeEnum b : DiscountTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private DiscountTypeEnum discountType;

  private BigDecimal discountValue;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private BigDecimal minPlayTime;

  private BigDecimal minAmount;

  private BigDecimal maxDiscount;

  private Integer usageLimit;

  private String description;

  public PromotionCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PromotionCreate(String promotionName, String promotionCode, DiscountTypeEnum discountType, BigDecimal discountValue) {
    this.promotionName = promotionName;
    this.promotionCode = promotionCode;
    this.discountType = discountType;
    this.discountValue = discountValue;
  }

  public PromotionCreate promotionName(String promotionName) {
    this.promotionName = promotionName;
    return this;
  }

  /**
   * Get promotionName
   * @return promotionName
   */
  @NotNull 
  @Schema(name = "promotion_name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promotion_name")
  public String getPromotionName() {
    return promotionName;
  }

  public void setPromotionName(String promotionName) {
    this.promotionName = promotionName;
  }

  public PromotionCreate promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  @NotNull 
  @Schema(name = "promotion_code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promotion_code")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
  }

  public PromotionCreate discountType(DiscountTypeEnum discountType) {
    this.discountType = discountType;
    return this;
  }

  /**
   * Get discountType
   * @return discountType
   */
  @NotNull 
  @Schema(name = "discount_type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("discount_type")
  public DiscountTypeEnum getDiscountType() {
    return discountType;
  }

  public void setDiscountType(DiscountTypeEnum discountType) {
    this.discountType = discountType;
  }

  public PromotionCreate discountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
    return this;
  }

  /**
   * Get discountValue
   * @return discountValue
   */
  @NotNull @Valid 
  @Schema(name = "discount_value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("discount_value")
  public BigDecimal getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
  }

  public PromotionCreate startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  @Valid 
  @Schema(name = "start_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start_date")
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  public PromotionCreate endDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  @Valid 
  @Schema(name = "end_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end_date")
  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
  }

  public PromotionCreate minPlayTime(BigDecimal minPlayTime) {
    this.minPlayTime = minPlayTime;
    return this;
  }

  /**
   * Get minPlayTime
   * @return minPlayTime
   */
  @Valid 
  @Schema(name = "min_play_time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min_play_time")
  public BigDecimal getMinPlayTime() {
    return minPlayTime;
  }

  public void setMinPlayTime(BigDecimal minPlayTime) {
    this.minPlayTime = minPlayTime;
  }

  public PromotionCreate minAmount(BigDecimal minAmount) {
    this.minAmount = minAmount;
    return this;
  }

  /**
   * Get minAmount
   * @return minAmount
   */
  @Valid 
  @Schema(name = "min_amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min_amount")
  public BigDecimal getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(BigDecimal minAmount) {
    this.minAmount = minAmount;
  }

  public PromotionCreate maxDiscount(BigDecimal maxDiscount) {
    this.maxDiscount = maxDiscount;
    return this;
  }

  /**
   * Get maxDiscount
   * @return maxDiscount
   */
  @Valid 
  @Schema(name = "max_discount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max_discount")
  public BigDecimal getMaxDiscount() {
    return maxDiscount;
  }

  public void setMaxDiscount(BigDecimal maxDiscount) {
    this.maxDiscount = maxDiscount;
  }

  public PromotionCreate usageLimit(Integer usageLimit) {
    this.usageLimit = usageLimit;
    return this;
  }

  /**
   * Get usageLimit
   * @return usageLimit
   */
  
  @Schema(name = "usage_limit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("usage_limit")
  public Integer getUsageLimit() {
    return usageLimit;
  }

  public void setUsageLimit(Integer usageLimit) {
    this.usageLimit = usageLimit;
  }

  public PromotionCreate description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionCreate promotionCreate = (PromotionCreate) o;
    return Objects.equals(this.promotionName, promotionCreate.promotionName) &&
        Objects.equals(this.promotionCode, promotionCreate.promotionCode) &&
        Objects.equals(this.discountType, promotionCreate.discountType) &&
        Objects.equals(this.discountValue, promotionCreate.discountValue) &&
        Objects.equals(this.startDate, promotionCreate.startDate) &&
        Objects.equals(this.endDate, promotionCreate.endDate) &&
        Objects.equals(this.minPlayTime, promotionCreate.minPlayTime) &&
        Objects.equals(this.minAmount, promotionCreate.minAmount) &&
        Objects.equals(this.maxDiscount, promotionCreate.maxDiscount) &&
        Objects.equals(this.usageLimit, promotionCreate.usageLimit) &&
        Objects.equals(this.description, promotionCreate.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promotionName, promotionCode, discountType, discountValue, startDate, endDate, minPlayTime, minAmount, maxDiscount, usageLimit, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionCreate {\n");
    sb.append("    promotionName: ").append(toIndentedString(promotionName)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    discountType: ").append(toIndentedString(discountType)).append("\n");
    sb.append("    discountValue: ").append(toIndentedString(discountValue)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    minPlayTime: ").append(toIndentedString(minPlayTime)).append("\n");
    sb.append("    minAmount: ").append(toIndentedString(minAmount)).append("\n");
    sb.append("    maxDiscount: ").append(toIndentedString(maxDiscount)).append("\n");
    sb.append("    usageLimit: ").append(toIndentedString(usageLimit)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

