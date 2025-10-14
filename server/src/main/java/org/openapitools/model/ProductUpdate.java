package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ProductUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class ProductUpdate {

  private String productName;

  private BigDecimal price;

  private BigDecimal costPrice;

  private String category;

  private Boolean isActive;

  public ProductUpdate productName(String productName) {
    this.productName = productName;
    return this;
  }

  /**
   * Get productName
   * @return productName
   */
  
  @Schema(name = "product_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public ProductUpdate price(BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
   */
  @Valid 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public ProductUpdate costPrice(BigDecimal costPrice) {
    this.costPrice = costPrice;
    return this;
  }

  /**
   * Get costPrice
   * @return costPrice
   */
  @Valid 
  @Schema(name = "cost_price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cost_price")
  public BigDecimal getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(BigDecimal costPrice) {
    this.costPrice = costPrice;
  }

  public ProductUpdate category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
   */
  
  @Schema(name = "category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public ProductUpdate isActive(Boolean isActive) {
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
    ProductUpdate productUpdate = (ProductUpdate) o;
    return Objects.equals(this.productName, productUpdate.productName) &&
        Objects.equals(this.price, productUpdate.price) &&
        Objects.equals(this.costPrice, productUpdate.costPrice) &&
        Objects.equals(this.category, productUpdate.category) &&
        Objects.equals(this.isActive, productUpdate.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productName, price, costPrice, category, isActive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductUpdate {\n");
    sb.append("    productName: ").append(toIndentedString(productName)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    costPrice: ").append(toIndentedString(costPrice)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
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

