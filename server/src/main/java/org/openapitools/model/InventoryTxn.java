package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
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
 * InventoryTxn
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class InventoryTxn {

  private @Nullable UUID id;

  private @Nullable UUID shopId;

  private @Nullable UUID productId;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    IN("IN"),
    
    OUT("OUT"),
    
    ADJ("ADJ");

    private final String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable TypeEnum type;

  private @Nullable BigDecimal qty;

  private @Nullable BigDecimal cost;

  private @Nullable String reason;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime ts;

  public InventoryTxn id(@Nullable UUID id) {
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

  public InventoryTxn shopId(@Nullable UUID shopId) {
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

  public InventoryTxn productId(@Nullable UUID productId) {
    this.productId = productId;
    return this;
  }

  /**
   * Get productId
   * @return productId
   */
  @Valid 
  @Schema(name = "product_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("product_id")
  public @Nullable UUID getProductId() {
    return productId;
  }

  public void setProductId(@Nullable UUID productId) {
    this.productId = productId;
  }

  public InventoryTxn type(@Nullable TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable TypeEnum getType() {
    return type;
  }

  public void setType(@Nullable TypeEnum type) {
    this.type = type;
  }

  public InventoryTxn qty(@Nullable BigDecimal qty) {
    this.qty = qty;
    return this;
  }

  /**
   * Get qty
   * @return qty
   */
  @Valid 
  @Schema(name = "qty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("qty")
  public @Nullable BigDecimal getQty() {
    return qty;
  }

  public void setQty(@Nullable BigDecimal qty) {
    this.qty = qty;
  }

  public InventoryTxn cost(@Nullable BigDecimal cost) {
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

  public InventoryTxn reason(@Nullable String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
   */
  
  @Schema(name = "reason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public @Nullable String getReason() {
    return reason;
  }

  public void setReason(@Nullable String reason) {
    this.reason = reason;
  }

  public InventoryTxn ts(@Nullable OffsetDateTime ts) {
    this.ts = ts;
    return this;
  }

  /**
   * Get ts
   * @return ts
   */
  @Valid 
  @Schema(name = "ts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ts")
  public @Nullable OffsetDateTime getTs() {
    return ts;
  }

  public void setTs(@Nullable OffsetDateTime ts) {
    this.ts = ts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InventoryTxn inventoryTxn = (InventoryTxn) o;
    return Objects.equals(this.id, inventoryTxn.id) &&
        Objects.equals(this.shopId, inventoryTxn.shopId) &&
        Objects.equals(this.productId, inventoryTxn.productId) &&
        Objects.equals(this.type, inventoryTxn.type) &&
        Objects.equals(this.qty, inventoryTxn.qty) &&
        Objects.equals(this.cost, inventoryTxn.cost) &&
        Objects.equals(this.reason, inventoryTxn.reason) &&
        Objects.equals(this.ts, inventoryTxn.ts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, shopId, productId, type, qty, cost, reason, ts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InventoryTxn {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    qty: ").append(toIndentedString(qty)).append("\n");
    sb.append("    cost: ").append(toIndentedString(cost)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    ts: ").append(toIndentedString(ts)).append("\n");
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

