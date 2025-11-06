package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * Bill
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class Bill {

  private @Nullable UUID id;

  private @Nullable UUID shopId;

  private @Nullable String code;

  private @Nullable BigDecimal sessionTotal;

  private @Nullable BigDecimal itemTotal;

  private @Nullable BigDecimal discount;

  private @Nullable Object paymentsJson;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    OPEN("OPEN"),
    
    PAID("PAID"),
    
    VOID("VOID");

    private final String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable StatusEnum status;

  public Bill id(@Nullable UUID id) {
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

  public Bill shopId(@Nullable UUID shopId) {
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

  public Bill code(@Nullable String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public @Nullable String getCode() {
    return code;
  }

  public void setCode(@Nullable String code) {
    this.code = code;
  }

  public Bill sessionTotal(@Nullable BigDecimal sessionTotal) {
    this.sessionTotal = sessionTotal;
    return this;
  }

  /**
   * Get sessionTotal
   * @return sessionTotal
   */
  @Valid 
  @Schema(name = "session_total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("session_total")
  public @Nullable BigDecimal getSessionTotal() {
    return sessionTotal;
  }

  public void setSessionTotal(@Nullable BigDecimal sessionTotal) {
    this.sessionTotal = sessionTotal;
  }

  public Bill itemTotal(@Nullable BigDecimal itemTotal) {
    this.itemTotal = itemTotal;
    return this;
  }

  /**
   * Get itemTotal
   * @return itemTotal
   */
  @Valid 
  @Schema(name = "item_total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("item_total")
  public @Nullable BigDecimal getItemTotal() {
    return itemTotal;
  }

  public void setItemTotal(@Nullable BigDecimal itemTotal) {
    this.itemTotal = itemTotal;
  }

  public Bill discount(@Nullable BigDecimal discount) {
    this.discount = discount;
    return this;
  }

  /**
   * Get discount
   * @return discount
   */
  @Valid 
  @Schema(name = "discount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discount")
  public @Nullable BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(@Nullable BigDecimal discount) {
    this.discount = discount;
  }

  public Bill paymentsJson(@Nullable Object paymentsJson) {
    this.paymentsJson = paymentsJson;
    return this;
  }

  /**
   * Get paymentsJson
   * @return paymentsJson
   */
  
  @Schema(name = "payments_json", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payments_json")
  public @Nullable Object getPaymentsJson() {
    return paymentsJson;
  }

  public void setPaymentsJson(@Nullable Object paymentsJson) {
    this.paymentsJson = paymentsJson;
  }

  public Bill status(@Nullable StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable StatusEnum getStatus() {
    return status;
  }

  public void setStatus(@Nullable StatusEnum status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Bill bill = (Bill) o;
    return Objects.equals(this.id, bill.id) &&
        Objects.equals(this.shopId, bill.shopId) &&
        Objects.equals(this.code, bill.code) &&
        Objects.equals(this.sessionTotal, bill.sessionTotal) &&
        Objects.equals(this.itemTotal, bill.itemTotal) &&
        Objects.equals(this.discount, bill.discount) &&
        Objects.equals(this.paymentsJson, bill.paymentsJson) &&
        Objects.equals(this.status, bill.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, shopId, code, sessionTotal, itemTotal, discount, paymentsJson, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Bill {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    sessionTotal: ").append(toIndentedString(sessionTotal)).append("\n");
    sb.append("    itemTotal: ").append(toIndentedString(itemTotal)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    paymentsJson: ").append(toIndentedString(paymentsJson)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

