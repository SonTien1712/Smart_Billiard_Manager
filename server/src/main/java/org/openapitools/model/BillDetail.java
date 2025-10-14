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
 * BillDetail
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class BillDetail {

  private Integer billDetailId;

  private Integer billId;

  private Integer productId;

  private Integer quantity;

  private BigDecimal unitPrice;

  private BigDecimal subTotal;

  public BillDetail billDetailId(Integer billDetailId) {
    this.billDetailId = billDetailId;
    return this;
  }

  /**
   * Get billDetailId
   * @return billDetailId
   */
  
  @Schema(name = "bill_detail_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bill_detail_id")
  public Integer getBillDetailId() {
    return billDetailId;
  }

  public void setBillDetailId(Integer billDetailId) {
    this.billDetailId = billDetailId;
  }

  public BillDetail billId(Integer billId) {
    this.billId = billId;
    return this;
  }

  /**
   * Get billId
   * @return billId
   */
  
  @Schema(name = "bill_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bill_id")
  public Integer getBillId() {
    return billId;
  }

  public void setBillId(Integer billId) {
    this.billId = billId;
  }

  public BillDetail productId(Integer productId) {
    this.productId = productId;
    return this;
  }

  /**
   * Get productId
   * @return productId
   */
  
  @Schema(name = "product_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("product_id")
  public Integer getProductId() {
    return productId;
  }

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public BillDetail quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity
   */
  
  @Schema(name = "quantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BillDetail unitPrice(BigDecimal unitPrice) {
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
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BillDetail subTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
    return this;
  }

  /**
   * Get subTotal
   * @return subTotal
   */
  @Valid 
  @Schema(name = "sub_total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sub_total")
  public BigDecimal getSubTotal() {
    return subTotal;
  }

  public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillDetail billDetail = (BillDetail) o;
    return Objects.equals(this.billDetailId, billDetail.billDetailId) &&
        Objects.equals(this.billId, billDetail.billId) &&
        Objects.equals(this.productId, billDetail.productId) &&
        Objects.equals(this.quantity, billDetail.quantity) &&
        Objects.equals(this.unitPrice, billDetail.unitPrice) &&
        Objects.equals(this.subTotal, billDetail.subTotal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billDetailId, billId, productId, quantity, unitPrice, subTotal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillDetail {\n");
    sb.append("    billDetailId: ").append(toIndentedString(billDetailId)).append("\n");
    sb.append("    billId: ").append(toIndentedString(billId)).append("\n");
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    unitPrice: ").append(toIndentedString(unitPrice)).append("\n");
    sb.append("    subTotal: ").append(toIndentedString(subTotal)).append("\n");
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

