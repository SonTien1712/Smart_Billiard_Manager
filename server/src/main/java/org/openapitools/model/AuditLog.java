package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * AuditLog
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class AuditLog {

  private @Nullable UUID id;

  private @Nullable UUID actorUserId;

  private JsonNullable<UUID> shopId = JsonNullable.<UUID>undefined();

  private @Nullable String action;

  private @Nullable String entity;

  private JsonNullable<UUID> entityId = JsonNullable.<UUID>undefined();

  private @Nullable Object metaJson;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime ts;

  public AuditLog id(@Nullable UUID id) {
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

  public AuditLog actorUserId(@Nullable UUID actorUserId) {
    this.actorUserId = actorUserId;
    return this;
  }

  /**
   * Get actorUserId
   * @return actorUserId
   */
  @Valid 
  @Schema(name = "actor_user_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actor_user_id")
  public @Nullable UUID getActorUserId() {
    return actorUserId;
  }

  public void setActorUserId(@Nullable UUID actorUserId) {
    this.actorUserId = actorUserId;
  }

  public AuditLog shopId(UUID shopId) {
    this.shopId = JsonNullable.of(shopId);
    return this;
  }

  /**
   * Get shopId
   * @return shopId
   */
  @Valid 
  @Schema(name = "shop_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shop_id")
  public JsonNullable<UUID> getShopId() {
    return shopId;
  }

  public void setShopId(JsonNullable<UUID> shopId) {
    this.shopId = shopId;
  }

  public AuditLog action(@Nullable String action) {
    this.action = action;
    return this;
  }

  /**
   * Get action
   * @return action
   */
  
  @Schema(name = "action", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("action")
  public @Nullable String getAction() {
    return action;
  }

  public void setAction(@Nullable String action) {
    this.action = action;
  }

  public AuditLog entity(@Nullable String entity) {
    this.entity = entity;
    return this;
  }

  /**
   * Get entity
   * @return entity
   */
  
  @Schema(name = "entity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("entity")
  public @Nullable String getEntity() {
    return entity;
  }

  public void setEntity(@Nullable String entity) {
    this.entity = entity;
  }

  public AuditLog entityId(UUID entityId) {
    this.entityId = JsonNullable.of(entityId);
    return this;
  }

  /**
   * Get entityId
   * @return entityId
   */
  @Valid 
  @Schema(name = "entity_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("entity_id")
  public JsonNullable<UUID> getEntityId() {
    return entityId;
  }

  public void setEntityId(JsonNullable<UUID> entityId) {
    this.entityId = entityId;
  }

  public AuditLog metaJson(@Nullable Object metaJson) {
    this.metaJson = metaJson;
    return this;
  }

  /**
   * Get metaJson
   * @return metaJson
   */
  
  @Schema(name = "meta_json", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("meta_json")
  public @Nullable Object getMetaJson() {
    return metaJson;
  }

  public void setMetaJson(@Nullable Object metaJson) {
    this.metaJson = metaJson;
  }

  public AuditLog ts(@Nullable OffsetDateTime ts) {
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
    AuditLog auditLog = (AuditLog) o;
    return Objects.equals(this.id, auditLog.id) &&
        Objects.equals(this.actorUserId, auditLog.actorUserId) &&
        equalsNullable(this.shopId, auditLog.shopId) &&
        Objects.equals(this.action, auditLog.action) &&
        Objects.equals(this.entity, auditLog.entity) &&
        equalsNullable(this.entityId, auditLog.entityId) &&
        Objects.equals(this.metaJson, auditLog.metaJson) &&
        Objects.equals(this.ts, auditLog.ts);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, actorUserId, hashCodeNullable(shopId), action, entity, hashCodeNullable(entityId), metaJson, ts);
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
    sb.append("class AuditLog {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    actorUserId: ").append(toIndentedString(actorUserId)).append("\n");
    sb.append("    shopId: ").append(toIndentedString(shopId)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    entity: ").append(toIndentedString(entity)).append("\n");
    sb.append("    entityId: ").append(toIndentedString(entityId)).append("\n");
    sb.append("    metaJson: ").append(toIndentedString(metaJson)).append("\n");
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

