package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentSession
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-02T12:30:08.264664900+07:00[Asia/Saigon]", comments = "Generator version: 7.17.0")
public class PaymentSession {

  private @Nullable String sessionId;

  private @Nullable String checkoutUrl;

  public PaymentSession sessionId(@Nullable String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
   */
  
  @Schema(name = "session_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("session_id")
  public @Nullable String getSessionId() {
    return sessionId;
  }

  public void setSessionId(@Nullable String sessionId) {
    this.sessionId = sessionId;
  }

  public PaymentSession checkoutUrl(@Nullable String checkoutUrl) {
    this.checkoutUrl = checkoutUrl;
    return this;
  }

  /**
   * Get checkoutUrl
   * @return checkoutUrl
   */
  
  @Schema(name = "checkout_url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkout_url")
  public @Nullable String getCheckoutUrl() {
    return checkoutUrl;
  }

  public void setCheckoutUrl(@Nullable String checkoutUrl) {
    this.checkoutUrl = checkoutUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentSession paymentSession = (PaymentSession) o;
    return Objects.equals(this.sessionId, paymentSession.sessionId) &&
        Objects.equals(this.checkoutUrl, paymentSession.checkoutUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionId, checkoutUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentSession {\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    checkoutUrl: ").append(toIndentedString(checkoutUrl)).append("\n");
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

