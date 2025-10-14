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
 * ProductCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class ProductCreate {

  private String productName;

  private BigDecimal price;

  private BigDecimal costPrice;

  private String category;

  private String productDescription;

  public ProductCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProductCreate(String productName, BigDecimal price, String category) {
    this.productName = productName;
    this.price = price;
    this.category = category;
  }

  public ProductCreate productName(String productName) {
    this.productName = productName;
    return this;
  }

  /**
   * Get productName
   * @return productName
   */
  @NotNull 
  @Schema(name = "product_name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public ProductCreate price(BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
   */
  @NotNull @Valid 
  @Schema(name = "price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("price")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public ProductCreate costPrice(BigDecimal costPrice) {
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

  public ProductCreate category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
   */
  @NotNull 
  @Schema(name = "category", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public ProductCreate productDescription(String productDescription) {
    this.productDescription = productDescription;
    return this;
  }

  /**
   * Get productDescription
   * @return productDescription
   */
  
  @Schema(name = "product_description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("product_description")
  public String getProductDescription() {
    return productDescription;
  }

  public void setProductDescription(String productDescription) {
    this.productDescription = productDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductCreate productCreate = (ProductCreate) o;
    return Objects.equals(this.productName, productCreate.productName) &&
        Objects.equals(this.price, productCreate.price) &&
        Objects.equals(this.costPrice, productCreate.costPrice) &&
        Objects.equals(this.category, productCreate.category) &&
        Objects.equals(this.productDescription, productCreate.productDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productName, price, costPrice, category, productDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductCreate {\n");
    sb.append("    productName: ").append(toIndentedString(productName)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    costPrice: ").append(toIndentedString(costPrice)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    productDescription: ").append(toIndentedString(productDescription)).append("\n");
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

