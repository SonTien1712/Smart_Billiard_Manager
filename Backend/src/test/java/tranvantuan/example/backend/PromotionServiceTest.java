package tranvantuan.example.backend;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.DiscountType;
import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Exception.BusinessException;
import com.BillardManagement.Exception.ResourceNotFoundException;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.PromotionRepository;
import com.BillardManagement.Service.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private BilliardClubRepo clubRepository;

    @Mock
    private CustomerRepo customerRepository;

    @InjectMocks
    private PromotionService promotionService;

    private Promotion testPromotion;
    private Billardclub testClub;

    @BeforeEach
    void setUp() {
        testClub = new Billardclub();
        testClub.setId(1);

        testPromotion = new Promotion();
        testPromotion.setId(1);
        testPromotion.setClub(testClub);
        testPromotion.setPromotionName("Test Promotion");
        testPromotion.setPromotionCode("TEST2024");
        testPromotion.setDiscountType(DiscountType.PERCENTAGE);
        testPromotion.setDiscountValue(BigDecimal.valueOf(20));
        testPromotion.setStartDate(Instant.now());
        testPromotion.setEndDate(Instant.now().plus(30, ChronoUnit.DAYS));
        testPromotion.setUsageLimit(100);
        testPromotion.setUsedCount(0);
        testPromotion.setIsActive(true);
    }

    @Test
    void getPromotionById_Success() {
        when(promotionRepository.findById(1)).thenReturn(Optional.of(testPromotion));

        Promotion result = promotionService.getPromotionById(1);

        assertNotNull(result);
        assertEquals("TEST2024", result.getPromotionCode());
        verify(promotionRepository, times(1)).findById(1);
    }

    @Test
    void getPromotionById_NotFound() {
        when(promotionRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            promotionService.getPromotionById(999);
        });
    }

    @Test
    void createPromotion_Success() {
        when(clubRepository.existsById(1)).thenReturn(true);
        when(promotionRepository.existsByPromotionCode("TEST2024")).thenReturn(false);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        Promotion result = promotionService.createPromotion(testPromotion);

        assertNotNull(result);
        assertEquals("TEST2024", result.getPromotionCode());
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    void createPromotion_DuplicateCode() {
        when(clubRepository.existsById(1)).thenReturn(true);
        when(promotionRepository.existsByPromotionCode("TEST2024")).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            promotionService.createPromotion(testPromotion);
        });
    }

    @Test
    void createPromotion_InvalidDates() {
        testPromotion.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));
        when(clubRepository.existsById(1)).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            promotionService.createPromotion(testPromotion);
        });
    }

    @Test
    void createPromotion_InvalidDiscountValue() {
        testPromotion.setDiscountValue(BigDecimal.valueOf(150)); // > 100%
        when(clubRepository.existsById(1)).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            promotionService.createPromotion(testPromotion);
        });
    }

    @Test
    void updatePromotion_Success() {
        Promotion updateData = new Promotion();
        updateData.setPromotionName("Updated Name");
        updateData.setIsActive(false);

        when(promotionRepository.findById(1)).thenReturn(Optional.of(testPromotion));
        when(clubRepository.existsById(1)).thenReturn(true);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        Promotion result = promotionService.updatePromotion(1, updateData);

        assertNotNull(result);
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    void applyPromotion_Success() {
        when(promotionRepository.findValidPromotionByCodeAndClub(
                eq("TEST2024"), eq(1), any(Instant.class)))
                .thenReturn(Optional.of(testPromotion));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        Promotion result = promotionService.applyPromotion("TEST2024", 1);

        assertNotNull(result);
        assertEquals(1, result.getUsedCount());
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    void applyPromotion_UsageLimitReached() {
        testPromotion.setUsedCount(100); // reached limit
        when(promotionRepository.findValidPromotionByCodeAndClub(
                eq("TEST2024"), eq(1), any(Instant.class)))
                .thenReturn(Optional.of(testPromotion));

        assertThrows(BusinessException.class, () -> {
            promotionService.applyPromotion("TEST2024", 1);
        });
    }

    @Test
    void deletePromotion_Success() {
        when(promotionRepository.findById(1)).thenReturn(Optional.of(testPromotion));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        promotionService.deletePromotion(1);

        assertFalse(testPromotion.getIsActive());
        verify(promotionRepository, times(1)).save(testPromotion);
    }
}