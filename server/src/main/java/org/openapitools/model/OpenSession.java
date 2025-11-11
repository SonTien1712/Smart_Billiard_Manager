package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * OpenSession
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class OpenSession {

  private UUID shopId;

  private UUID tableId;

  public OpenSession() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OpenSession(UUID shopId, UUID tableId) {
    this.shopId = shopId;
    this.tableId = tableId;
  }

  public OpenSession shopId(UUID shopId) {
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

  public OpenSession tableId(UUID tableId) {
    this.tableId = tableId;
    return this;
  }

  /**
   * Get tableId
   * @return tableId
   */
  @NotNull @Valid 
  @Schema(name = "table_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("table_id")
  public UUID getTableId() {
    return tableId;
  }

  public void setTableId(UUID tableId) {
    this.tableId = tableId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenSession openSession = (OpenSession) o;
    return Objects.equals(this.shopId, openSession.shopId) &&
        Objects.equals(this.tableId, openSession.tableId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shopId, tableId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenSession {\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    tableId: ").append(toIndentedString(tableId)).append("\n");
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

