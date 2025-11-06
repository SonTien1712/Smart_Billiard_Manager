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
 * CreatePaymentIntent
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class CreatePaymentIntent {

  private UUID shopId;

  private UUID billId;

  private BigDecimal amount;

  /**
   * Gets or Sets provider
   */
  public enum ProviderEnum {
    MOMO("momo"),
    
    ZALOPAY("zalopay"),
    
    BANKQR("bankqr");

    private final String value;

    ProviderEnum(String value) {
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
    public static ProviderEnum fromValue(String value) {
      for (ProviderEnum b : ProviderEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ProviderEnum provider;

  public CreatePaymentIntent() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreatePaymentIntent(UUID shopId, UUID billId, BigDecimal amount, ProviderEnum provider) {
    this.shopId = shopId;
    this.billId = billId;
    this.amount = amount;
    this.provider = provider;
  }

  public CreatePaymentIntent shopId(UUID shopId) {
    this.shopId = shopId;
    return this;
  }

  /**
   * Get shopId
   * @return shopId
   */
  @NotNull @Valid 
  @Schema(name = "shop_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("shop_id")
  public UUID getShopId() {
    return shopId;
  }

  public void setShopId(UUID shopId) {
    this.shopId = shopId;
  }

  public CreatePaymentIntent billId(UUID billId) {
    this.billId = billId;
    return this;
  }

  /**
   * Get billId
   * @return billId
   */
  @NotNull @Valid 
  @Schema(name = "bill_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bill_id")
  public UUID getBillId() {
    return billId;
  }

  public void setBillId(UUID billId) {
    this.billId = billId;
  }

  public CreatePaymentIntent amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
   */
  @NotNull @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public CreatePaymentIntent provider(ProviderEnum provider) {
    this.provider = provider;
    return this;
  }

  /**
   * Get provider
   * @return provider
   */
  @NotNull 
  @Schema(name = "provider", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("provider")
  public ProviderEnum getProvider() {
    return provider;
  }

  public void setProvider(ProviderEnum provider) {
    this.provider = provider;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePaymentIntent createPaymentIntent = (CreatePaymentIntent) o;
    return Objects.equals(this.shopId, createPaymentIntent.shopId) &&
        Objects.equals(this.billId, createPaymentIntent.billId) &&
        Objects.equals(this.amount, createPaymentIntent.amount) &&
        Objects.equals(this.provider, createPaymentIntent.provider);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, billId, amount, provider);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePaymentIntent {\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    billId: ").append(toIndentedString(billId)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
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

