package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * Promotion
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class Promotion {

  private Integer promotionId;

  private Integer clubId;

  private String promotionName;

  private String promotionCode;

  private String discountType;

  private BigDecimal discountValue;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime endDate;

  private Boolean isActive;

  public Promotion promotionId(Integer promotionId) {
    this.promotionId = promotionId;
    return this;
  }

  /**
   * Get promotionId
   * @return promotionId
   */
  
  @Schema(name = "promotion_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotion_id")
  public Integer getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(Integer promotionId) {
    this.promotionId = promotionId;
  }

  public Promotion clubId(Integer clubId) {
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

  public Promotion promotionName(String promotionName) {
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

  public Promotion promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  
  @Schema(name = "promotion_code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotion_code")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
  }

  public Promotion discountType(String discountType) {
    this.discountType = discountType;
    return this;
  }

  /**
   * Get discountType
   * @return discountType
   */
  
  @Schema(name = "discount_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discount_type")
  public String getDiscountType() {
    return discountType;
  }

  public void setDiscountType(String discountType) {
    this.discountType = discountType;
  }

  public Promotion discountValue(BigDecimal discountValue) {
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

  public Promotion startDate(OffsetDateTime startDate) {
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

  public Promotion endDate(OffsetDateTime endDate) {
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

  public Promotion isActive(Boolean isActive) {
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
    Promotion promotion = (Promotion) o;
    return Objects.equals(this.promotionId, promotion.promotionId) &&
        Objects.equals(this.clubId, promotion.clubId) &&
        Objects.equals(this.promotionName, promotion.promotionName) &&
        Objects.equals(this.promotionCode, promotion.promotionCode) &&
        Objects.equals(this.discountType, promotion.discountType) &&
        Objects.equals(this.discountValue, promotion.discountValue) &&
        Objects.equals(this.startDate, promotion.startDate) &&
        Objects.equals(this.endDate, promotion.endDate) &&
        Objects.equals(this.isActive, promotion.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promotionId, clubId, promotionName, promotionCode, discountType, discountValue, startDate, endDate, isActive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Promotion {\n");
    sb.append("    promotionId: ").append(toIndentedString(promotionId)).append("\n");
    sb.append("    clubId: ").append(toIndentedString(clubId)).append("\n");
    sb.append("    promotionName: ").append(toIndentedString(promotionName)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    discountType: ").append(toIndentedString(discountType)).append("\n");
    sb.append("    discountValue: ").append(toIndentedString(discountValue)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
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

