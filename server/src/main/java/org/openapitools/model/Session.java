package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Session
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class Session {

  private @Nullable UUID id;

  private @Nullable UUID shopId;

  private @Nullable UUID tableId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime startAt;

  private @Nullable Integer pauseTotalS;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private JsonNullable<OffsetDateTime> endAt = JsonNullable.<OffsetDateTime>undefined();

  private @Nullable BigDecimal computedFee;

  /**
   * Gets or Sets state
   */
  public enum StateEnum {
    OPEN("OPEN"),
    
    PAUSED("PAUSED"),
    
    CLOSED("CLOSED");

    private final String value;

    StateEnum(String value) {
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
    public static StateEnum fromValue(String value) {
      for (StateEnum b : StateEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable StateEnum state;

  public Session id(@Nullable UUID id) {
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

  public Session shopId(@Nullable UUID shopId) {
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

  public Session tableId(@Nullable UUID tableId) {
    this.tableId = tableId;
    return this;
  }

  /**
   * Get tableId
   * @return tableId
   */
  @Valid 
  @Schema(name = "table_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("table_id")
  public @Nullable UUID getTableId() {
    return tableId;
  }

  public void setTableId(@Nullable UUID tableId) {
    this.tableId = tableId;
  }

  public Session startAt(@Nullable OffsetDateTime startAt) {
    this.startAt = startAt;
    return this;
  }

  /**
   * Get startAt
   * @return startAt
   */
  @Valid 
  @Schema(name = "start_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start_at")
  public @Nullable OffsetDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(@Nullable OffsetDateTime startAt) {
    this.startAt = startAt;
  }

  public Session pauseTotalS(@Nullable Integer pauseTotalS) {
    this.pauseTotalS = pauseTotalS;
    return this;
  }

  /**
   * Get pauseTotalS
   * @return pauseTotalS
   */
  
  @Schema(name = "pause_total_s", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pause_total_s")
  public @Nullable Integer getPauseTotalS() {
    return pauseTotalS;
  }

  public void setPauseTotalS(@Nullable Integer pauseTotalS) {
    this.pauseTotalS = pauseTotalS;
  }

  public Session endAt(OffsetDateTime endAt) {
    this.endAt = JsonNullable.of(endAt);
    return this;
  }

  /**
   * Get endAt
   * @return endAt
   */
  @Valid 
  @Schema(name = "end_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end_at")
  public JsonNullable<OffsetDateTime> getEndAt() {
    return endAt;
  }

  public void setEndAt(JsonNullable<OffsetDateTime> endAt) {
    this.endAt = endAt;
  }

  public Session computedFee(@Nullable BigDecimal computedFee) {
    this.computedFee = computedFee;
    return this;
  }

  /**
   * Get computedFee
   * @return computedFee
   */
  @Valid 
  @Schema(name = "computed_fee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("computed_fee")
  public @Nullable BigDecimal getComputedFee() {
    return computedFee;
  }

  public void setComputedFee(@Nullable BigDecimal computedFee) {
    this.computedFee = computedFee;
  }

  public Session state(@Nullable StateEnum state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
   */
  
  @Schema(name = "state", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("state")
  public @Nullable StateEnum getState() {
    return state;
  }

  public void setState(@Nullable StateEnum state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Session session = (Session) o;
    return Objects.equals(this.id, session.id) &&
        Objects.equals(this.shopId, session.shopId) &&
        Objects.equals(this.tableId, session.tableId) &&
        Objects.equals(this.startAt, session.startAt) &&
        Objects.equals(this.pauseTotalS, session.pauseTotalS) &&
        equalsNullable(this.endAt, session.endAt) &&
        Objects.equals(this.computedFee, session.computedFee) &&
        Objects.equals(this.state, session.state);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, shopId, tableId, startAt, pauseTotalS, hashCodeNullable(endAt), computedFee, state);
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[]{a.get()}) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Session {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    tableId: ").append(toIndentedString(tableId)).append("\n");
    sb.append("    startAt: ").append(toIndentedString(startAt)).append("\n");
    sb.append("    pauseTotalS: ").append(toIndentedString(pauseTotalS)).append("\n");
    sb.append("    endAt: ").append(toIndentedString(endAt)).append("\n");
    sb.append("    computedFee: ").append(toIndentedString(computedFee)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

