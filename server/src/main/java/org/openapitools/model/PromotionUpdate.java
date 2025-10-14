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
 * PromotionUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class PromotionUpdate {

  private String promotionName;

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

  private Boolean isActive;

  public PromotionUpdate promotionName(String promotionName) {
    this.promotionName = promotionName;
    return this;
  }

  /**
   * Get promotionName
   * @return promotionName
   */
  
  @Schema(name = "promotion_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotion_name")
  public String getPromotionName() {
    return promotionName;
  }

  public void setPromotionName(String promotionName) {
    this.promotionName = promotionName;
  }

  public PromotionUpdate discountType(DiscountTypeEnum discountType) {
    this.discountType = discountType;
    return this;
  }

  /**
   * Get discountType
   * @return discountType
   */
  
  @Schema(name = "discount_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discount_type")
  public DiscountTypeEnum getDiscountType() {
    return discountType;
  }

  public void setDiscountType(DiscountTypeEnum discountType) {
    this.discountType = discountType;
  }

  public PromotionUpdate discountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
    return this;
  }

  /**
   * Get discountValue
   * @return discountValue
   */
  @Valid 
  @Schema(name = "discount_value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discount_value")
  public BigDecimal getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
  }

  public PromotionUpdate isActive(Boolean isActive) {
    this.isActive = isActive;
    return this;
  }

  /**
   * Get isActive
   * @return isActive
   */
  
  @Schema(name = "is_active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_active")
  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionUpdate promotionUpdate = (PromotionUpdate) o;
    return Objects.equals(this.promotionName, promotionUpdate.promotionName) &&
        Objects.equals(this.discountType, promotionUpdate.discountType) &&
        Objects.equals(this.discountValue, promotionUpdate.discountValue) &&
        Objects.equals(this.isActive, promotionUpdate.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promotionName, discountType, discountValue, isActive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionUpdate {\n");
    sb.append("    promotionName: ").append(toIndentedString(promotionName)).append("\n");
    sb.append("    discountType: ").append(toIndentedString(discountType)).append("\n");
    sb.append("    discountValue: ").append(toIndentedString(discountValue)).append("\n");
    sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
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

