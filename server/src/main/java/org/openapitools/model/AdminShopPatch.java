package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AdminShopPatch
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class AdminShopPatch {

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    ACTIVE("active"),
    
    LOCKED("locked"),
    
    ARCHIVED("archived");

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

  /**
   * Gets or Sets plan
   */
  public enum PlanEnum {
    FREE("FREE"),
    
    PREMIUM("PREMIUM"),
    
    SUSPENDED("SUSPENDED");

    private final String value;

    PlanEnum(String value) {
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
    public static PlanEnum fromValue(String value) {
      for (PlanEnum b : PlanEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PlanEnum plan;

  @Valid
  private Map<String, Object> quotaJson = new HashMap<>();

  public AdminShopPatch status(@Nullable StatusEnum status) {
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

  public AdminShopPatch plan(@Nullable PlanEnum plan) {
    this.plan = plan;
    return this;
  }

  /**
   * Get plan
   * @return plan
   */
  
  @Schema(name = "plan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("plan")
  public @Nullable PlanEnum getPlan() {
    return plan;
  }

  public void setPlan(@Nullable PlanEnum plan) {
    this.plan = plan;
  }

  public AdminShopPatch quotaJson(Map<String, Object> quotaJson) {
    this.quotaJson = quotaJson;
    return this;
  }

  public AdminShopPatch putQuotaJsonItem(String key, Object quotaJsonItem) {
    if (this.quotaJson == null) {
      this.quotaJson = new HashMap<>();
    }
    this.quotaJson.put(key, quotaJsonItem);
    return this;
  }

  /**
   * Get quotaJson
   * @return quotaJson
   */
  
  @Schema(name = "quota_json", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quota_json")
  public Map<String, Object> getQuotaJson() {
    return quotaJson;
  }

  public void setQuotaJson(Map<String, Object> quotaJson) {
    this.quotaJson = quotaJson;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdminShopPatch adminShopPatch = (AdminShopPatch) o;
    return Objects.equals(this.status, adminShopPatch.status) &&
        Objects.equals(this.plan, adminShopPatch.plan) &&
        Objects.equals(this.quotaJson, adminShopPatch.quotaJson);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, plan, quotaJson);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdminShopPatch {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
    sb.append("    quotaJson: ").append(toIndentedString(quotaJson)).append("\n");
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

