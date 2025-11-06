package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * SessionsSessionIdPatchRequest
 */

@JsonTypeName("_sessions__sessionId__patch_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class SessionsSessionIdPatchRequest {

  /**
   * Gets or Sets op
   */
  public enum OpEnum {
    PAUSE("pause"),
    
    RESUME("resume"),
    
    TRANSFER("transfer"),
    
    CLOSE("close");

    private final String value;

    OpEnum(String value) {
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
    public static OpEnum fromValue(String value) {
      for (OpEnum b : OpEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable OpEnum op;

  private @Nullable UUID toTableId;

  public SessionsSessionIdPatchRequest op(@Nullable OpEnum op) {
    this.op = op;
    return this;
  }

  /**
   * Get op
   * @return op
   */
  
  @Schema(name = "op", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("op")
  public @Nullable OpEnum getOp() {
    return op;
  }

  public void setOp(@Nullable OpEnum op) {
    this.op = op;
  }

  public SessionsSessionIdPatchRequest toTableId(@Nullable UUID toTableId) {
    this.toTableId = toTableId;
    return this;
  }

  /**
   * Get toTableId
   * @return toTableId
   */
  @Valid 
  @Schema(name = "to_table_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("to_table_id")
  public @Nullable UUID getToTableId() {
    return toTableId;
  }

  public void setToTableId(@Nullable UUID toTableId) {
    this.toTableId = toTableId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SessionsSessionIdPatchRequest sessionsSessionIdPatchRequest = (SessionsSessionIdPatchRequest) o;
    return Objects.equals(this.op, sessionsSessionIdPatchRequest.op) &&
        Objects.equals(this.toTableId, sessionsSessionIdPatchRequest.toTableId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(op, toTableId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SessionsSessionIdPatchRequest {\n");
    sb.append("    op: ").append(toIndentedString(op)).append("\n");
    sb.append("    toTableId: ").append(toIndentedString(toTableId)).append("\n");
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

