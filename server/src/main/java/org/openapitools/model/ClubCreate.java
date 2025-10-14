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
 * ClubCreate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
public class ClubCreate {

  private String clubName;

  private String address;

  private String phoneNumber;

  public ClubCreate() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ClubCreate(String clubName, String address, String phoneNumber) {
    this.clubName = clubName;
    this.address = address;
    this.phoneNumber = phoneNumber;
  }

  public ClubCreate clubName(String clubName) {
    this.clubName = clubName;
    return this;
  }

  /**
   * Get clubName
   * @return clubName
   */
  @NotNull 
  @Schema(name = "club_name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("club_name")
  public String getClubName() {
    return clubName;
  }

  public void setClubName(String clubName) {
    this.clubName = clubName;
  }

  public ClubCreate address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @NotNull 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public ClubCreate phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  @NotNull 
  @Schema(name = "phone_number", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ClubCreate clubCreate = (ClubCreate) o;
    return Objects.equals(this.clubName, clubCreate.clubName) &&
        Objects.equals(this.address, clubCreate.address) &&
        Objects.equals(this.phoneNumber, clubCreate.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clubName, address, phoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClubCreate {\n");
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

