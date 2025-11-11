package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PricingQuoteGet200Response
 */

@JsonTypeName("_pricing_quote_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class PricingQuoteGet200Response {

  private @Nullable BigDecimal unitPrice;

  private @Nullable Integer roundingBlock;

  public PricingQuoteGet200Response unitPrice(@Nullable BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  /**
   * Get unitPrice
   * @return unitPrice
   */
  @Valid 
  @Schema(name = "unit_price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unit_price")
  public @Nullable BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(@Nullable BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public PricingQuoteGet200Response roundingBlock(@Nullable Integer roundingBlock) {
    this.roundingBlock = roundingBlock;
    return this;
  }

  /**
   * Get roundingBlock
   * @return roundingBlock
   */
  
  @Schema(name = "rounding_block", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rounding_block")
  public @Nullable Integer getRoundingBlock() {
    return roundingBlock;
  }

  public void setRoundingBlock(@Nullable Integer roundingBlock) {
    this.roundingBlock = roundingBlock;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PricingQuoteGet200Response pricingQuoteGet200Response = (PricingQuoteGet200Response) o;
    return Objects.equals(this.unitPrice, pricingQuoteGet200Response.unitPrice) &&
        Objects.equals(this.roundingBlock, pricingQuoteGet200Response.roundingBlock);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unitPrice, roundingBlock);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PricingQuoteGet200Response {\n");
    sb.append("    unitPrice: ").append(toIndentedString(unitPrice)).append("\n");
    sb.append("    roundingBlock: ").append(toIndentedString(roundingBlock)).append("\n");
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

