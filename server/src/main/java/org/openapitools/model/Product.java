package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Product
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class Product {

  private @Nullable UUID id;

  private @Nullable UUID shopId;

  private @Nullable String sku;

  private @Nullable String name;

  private @Nullable String category;

  private @Nullable String uom;

  private @Nullable BigDecimal price;

  private @Nullable BigDecimal cost;

  private @Nullable BigDecimal stockQty;

  private @Nullable BigDecimal minStock;

  private @Nullable BigDecimal taxRate;

  private @Nullable Boolean active;

  public Product id(@Nullable UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @Valid 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable UUID getId() {
    return id;
  }

  public void setId(@Nullable UUID id) {
    this.id = id;
  }

  public Product shopId(@Nullable UUID shopId) {
    this.shopId = shopId;
    return this;
  }

  /**
   * Get shopId
   * @return shopId
   */
  @Valid 
  @Schema(name = "shop_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shop_id")
  public @Nullable UUID getShopId() {
    return shopId;
  }

  public void setShopId(@Nullable UUID shopId) {
    this.shopId = shopId;
  }

  public Product sku(@Nullable String sku) {
    this.sku = sku;
    return this;
  }

  /**
   * Get sku
   * @return sku
   */
  
  @Schema(name = "sku", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sku")
  public @Nullable String getSku() {
    return sku;
  }

  public void setSku(@Nullable String sku) {
    this.sku = sku;
  }

  public Product name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public Product category(@Nullable String category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   * @return category
   */
  
  @Schema(name = "category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("category")
  public @Nullable String getCategory() {
    return category;
  }

  public void setCategory(@Nullable String category) {
    this.category = category;
  }

  public Product uom(@Nullable String uom) {
    this.uom = uom;
    return this;
  }

  /**
   * Get uom
   * @return uom
   */
  
  @Schema(name = "uom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uom")
  public @Nullable String getUom() {
    return uom;
  }

  public void setUom(@Nullable String uom) {
    this.uom = uom;
  }

  public Product price(@Nullable BigDecimal price) {
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
  public @Nullable BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@Nullable BigDecimal price) {
    this.price = price;
  }

  public Product cost(@Nullable BigDecimal cost) {
    this.cost = cost;
    return this;
  }

  /**
   * Get cost
   * @return cost
   */
  @Valid 
  @Schema(name = "cost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cost")
  public @Nullable BigDecimal getCost() {
    return cost;
  }

  public void setCost(@Nullable BigDecimal cost) {
    this.cost = cost;
  }

  public Product stockQty(@Nullable BigDecimal stockQty) {
    this.stockQty = stockQty;
    return this;
  }

  /**
   * Get stockQty
   * @return stockQty
   */
  @Valid 
  @Schema(name = "stock_qty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stock_qty")
  public @Nullable BigDecimal getStockQty() {
    return stockQty;
  }

  public void setStockQty(@Nullable BigDecimal stockQty) {
    this.stockQty = stockQty;
  }

  public Product minStock(@Nullable BigDecimal minStock) {
    this.minStock = minStock;
    return this;
  }

  /**
   * Get minStock
   * @return minStock
   */
  @Valid 
  @Schema(name = "min_stock", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min_stock")
  public @Nullable BigDecimal getMinStock() {
    return minStock;
  }

  public void setMinStock(@Nullable BigDecimal minStock) {
    this.minStock = minStock;
  }

  public Product taxRate(@Nullable BigDecimal taxRate) {
    this.taxRate = taxRate;
    return this;
  }

  /**
   * Get taxRate
   * @return taxRate
   */
  @Valid 
  @Schema(name = "tax_rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tax_rate")
  public @Nullable BigDecimal getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(@Nullable BigDecimal taxRate) {
    this.taxRate = taxRate;
  }

  public Product active(@Nullable Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  
  @Schema(name = "active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public @Nullable Boolean getActive() {
    return active;
  }

  public void setActive(@Nullable Boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Product product = (Product) o;
    return Objects.equals(this.id, product.id) &&
        Objects.equals(this.shopId, product.shopId) &&
        Objects.equals(this.sku, product.sku) &&
        Objects.equals(this.name, product.name) &&
        Objects.equals(this.category, product.category) &&
        Objects.equals(this.uom, product.uom) &&
        Objects.equals(this.price, product.price) &&
        Objects.equals(this.cost, product.cost) &&
        Objects.equals(this.stockQty, product.stockQty) &&
        Objects.equals(this.minStock, product.minStock) &&
        Objects.equals(this.taxRate, product.taxRate) &&
        Objects.equals(this.active, product.active);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, shopId, sku, name, category, uom, price, cost, stockQty, minStock, taxRate, active);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Product {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    sku: ").append(toIndentedString(sku)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    uom: ").append(toIndentedString(uom)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    cost: ").append(toIndentedString(cost)).append("\n");
    sb.append("    stockQty: ").append(toIndentedString(stockQty)).append("\n");
    sb.append("    minStock: ").append(toIndentedString(minStock)).append("\n");
    sb.append("    taxRate: ").append(toIndentedString(taxRate)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
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

