package org.openapitools.api;

import org.openapitools.model.Bill;
import org.openapitools.model.BillCreate;
import org.openapitools.model.Club;
import org.openapitools.model.ClubCreate;
import org.openapitools.model.ClubUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.model.Employee;
import org.openapitools.model.EmployeeCreate;
import org.openapitools.model.OccupancyReport;
import java.time.OffsetDateTime;
import org.openapitools.model.Product;
import org.openapitools.model.ProductCreate;
import org.openapitools.model.Promotion;
import org.openapitools.model.PromotionCreate;
import org.openapitools.model.RevenueReport;
import org.openapitools.model.Table;
import org.openapitools.model.TableCreate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-14T14:32:21.168513498+07:00[Asia/Bangkok]", comments = "Generator version: 7.7.0")
@Controller
@RequestMapping("${openapi.billiardClubManagementSystem.base-path:/v1}")
public class ClubsApiController implements ClubsApi {

    private final NativeWebRequest request;

    @Autowired
    public ClubsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
