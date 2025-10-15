package org.openapitools.api;

import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Service.PromotionService;
import org.openapitools.model.PromotionUpdate;
import org.openapitools.model.Promotion as PromotionModel; // tránh trùng tên entity
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Triển khai thực tế API PATCH /promotions/{promotionId}
 * Cập nhật thông tin khuyến mãi trong hệ thống bi-da.
 */
@Controller
@RequestMapping("${openapi.billiardClubManagementSystem.base-path:/v1}")
public class PromotionsApiController implements PromotionsApi {

    private final NativeWebRequest request;

    private final PromotionService promotionService;

    @Autowired
    public PromotionsApiController(NativeWebRequest request, PromotionService promotionService) {
        this.request = request;
        this.promotionService = promotionService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * API PATCH /promotions/{promotionId}
     * Cập nhật thông tin khuyến mãi
     */
    @Override
    public ResponseEntity<PromotionModel> promotionsPromotionIdPatch(
            @PathVariable("promotionId") Integer promotionId,
            @Valid @RequestBody PromotionUpdate promotionUpdate) {

        try {
            // B1: Lấy promotion hiện tại từ DB
            Optional<Promotion> optionalPromotion = promotionService.getPromotionById(promotionId);
            if (optionalPromotion.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Promotion existing = optionalPromotion.get();

            // B2: Áp dụng các giá trị mới từ PromotionUpdate (nếu có)
            if (promotionUpdate.getPromotionName() != null)
                existing.setPromotionName(promotionUpdate.getPromotionName());

            if (promotionUpdate.getPromotionCode() != null)
                existing.setPromotionCode(promotionUpdate.getPromotionCode());

            if (promotionUpdate.getDiscountType() != null)
                existing.setDiscountType(promotionUpdate.getDiscountType());

            if (promotionUpdate.getDiscountValue() != null)
                existing.setDiscountValue(promotionUpdate.getDiscountValue());

            if (promotionUpdate.getStartDate() != null)
                existing.setStartDate(promotionUpdate.getStartDate());

            if (promotionUpdate.getEndDate() != null)
                existing.setEndDate(promotionUpdate.getEndDate());

            if (promotionUpdate.getIsActive() != null)
                existing.setIsActive(promotionUpdate.getIsActive());

            // Nếu OpenAPI model có thêm field nào khác (ví dụ: min_amount, usage_limit, max_discount)
            // bạn thêm mapping tương ứng ở đây
            // if (promotionUpdate.getMinAmount() != null)
            //     existing.setMinAmount(promotionUpdate.getMinAmount());
            // ...

            // B3: Lưu vào DB
            Promotion updated = promotionService.save(existing);

            // B4: Map từ Entity -> OpenAPI model để trả về client
            PromotionModel response = new PromotionModel();
            response.setPromotionId(updated.getId());
            response.setPromotionName(updated.getPromotionName());
            response.setPromotionCode(updated.getPromotionCode());
            response.setDiscountType(updated.getDiscountType());
            response.setDiscountValue(updated.getDiscountValue());
            response.setStartDate(updated.getStartDate());
            response.setEndDate(updated.getEndDate());
            response.setIsActive(updated.getIsActive());

            // B5: Trả kết quả thành công
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // B6: Nếu lỗi server hoặc lỗi khác
            return ResponseEntity.internalServerError().build();
        }
    }
}
