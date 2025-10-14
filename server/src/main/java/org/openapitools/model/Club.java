package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Club
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class Club {

  private Integer clubId;

  private Integer customerId;

  private String clubName;

  private String address;

  private String phoneNumber;

  public Club clubId(Integer clubId) {
    this.clubId = clubId;
    return this;
  }

  /**
   * Get clubId
   * @return clubId
   */
  
  @Schema(name = "club_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("club_id")
  public Integer getClubId() {
    return clubId;
  }

  public void setClubId(Integer clubId) {
    this.clubId = clubId;
  }

  public Club customerId(Integer customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
   */
  
  @Schema(name = "customer_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customer_id")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public Club clubName(String clubName) {
    this.clubName = clubName;
    return this;
  }

  /**
   * Get clubName
   * @return clubName
   */
  
  @Schema(name = "club_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("club_name")
  public String getClubName() {
    return clubName;
  }

  public void setClubName(String clubName) {
    this.clubName = clubName;
  }

  public Club address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Club phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  
  @Schema(name = "phone_number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phone_number")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Club club = (Club) o;
    return Objects.equals(this.clubId, club.clubId) &&
        Objects.equals(this.customerId, club.customerId) &&
        Objects.equals(this.clubName, club.clubName) &&
        Objects.equals(this.address, club.address) &&
        Objects.equals(this.phoneNumber, club.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clubId, customerId, clubName, address, phoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Club {\n");
    sb.append("    clubId: ").append(toIndentedString(clubId)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    clubName: ").append(toIndentedString(clubName)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
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

