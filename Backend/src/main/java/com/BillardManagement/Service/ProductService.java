package com.BillardManagement.Service;

import com.BillardManagement.DTO.Request.ProductRequestDTO;
import com.BillardManagement.DTO.Response.ProductResponseDTO;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Product;
import com.BillardManagement.Exception.BusinessException;
import com.BillardManagement.Exception.ResourceNotFoundException;
import com.BillardManagement.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final BillardclubRepo clubRepository;
    private final CustomerRepo customerRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProductsByClub(Integer clubId, Boolean activeOnly) {
        log.info("Fetching products for club: {}, activeOnly: {}", clubId, activeOnly);

        List<Product> products = activeOnly != null && activeOnly
                ? productRepository.findByClubIdAndIsActiveTrue(clubId)
                : productRepository.findByClubId(clubId);

        return products.stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Integer id, Integer clubId) {
        log.info("Fetching product with id: {} for club: {}", id, clubId);

        Product product = productRepository.findByIdAndClubId(id, clubId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id + " for club: " + clubId));

        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request, Integer customerId) {
        log.info("Creating new product: {} for club: {}", request.getName(), request.getClubId());

        // Validate customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + customerId));

        if (!customer.getIsActive()) {
            throw new BusinessException("Customer account is inactive");
        }

        // Validate club
        Billardclub club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Club not found with id: " + request.getClubId()));

        if (!club.getCustomerID().equals(customerId)) {
            throw new BusinessException("Club does not belong to this customer");
        }

        if (!club.getIsActive()) {
            throw new BusinessException("Club is inactive");
        }

        // Build product entity
        Product product = buildProductEntity(request, customer, club);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());
        return ProductResponseDTO.fromEntity(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO request, Integer customerId) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        // Verify ownership
        if (!product.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("You don't have permission to update this product");
        }

        // Update club if changed
        if (!product.getClub().getId().equals(request.getClubId())) {
            Billardclub newClub = clubRepository.findById(request.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Club not found with id: " + request.getClubId()));

            if (!newClub.getCustomerID().equals(customerId)) {
                throw new BusinessException("New club does not belong to this customer");
            }
            product.setClub(newClub);
        }

        // Update product fields
        updateProductFields(product, request);
        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        return ProductResponseDTO.fromEntity(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Integer id, Integer customerId) {
        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        if (!product.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("You don't have permission to delete this product");
        }

        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    @Transactional
    public ProductResponseDTO toggleProductStatus(Integer id, Integer customerId) {
        log.info("Toggling status for product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        if (!product.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("You don't have permission to modify this product");
        }

        product.setIsActive(!product.getIsActive());
        Product updatedProduct = productRepository.save(product);

        log.info("Product status toggled successfully with id: {}", updatedProduct.getId());
        return ProductResponseDTO.fromEntity(updatedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(Integer clubId, String keyword) {
        log.info("Searching products for club: {} with keyword: {}", clubId, keyword);

        List<Product> products = productRepository.searchProducts(clubId, keyword);
        return products.stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    /**
     * Helper method để build Product entity từ request DTO
     * Thay thế ProductMapper.toEntity()
     */
    private Product buildProductEntity(ProductRequestDTO request, Customer customer, Billardclub club) {
        return Product.builder()
                .productName(request.getName())
                .price(request.getPrice())
                .costPrice(request.getCostPrice() != null ? request.getCostPrice() : BigDecimal.ZERO)
                .category(request.getCategory())
                .productDescription(request.getDescription())
                .productUrl(request.getProductUrl())
                .isActive(request.getActive() != null ? request.getActive() : true)
                .customer(customer)
                .club(club)
                .build();
    }

    /**
     * Helper method để update Product entity từ request DTO
     * Thay thế ProductMapper.updateEntity()
     */
    private void updateProductFields(Product product, ProductRequestDTO request) {
        product.setProductName(request.getName());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice() != null ? request.getCostPrice() : BigDecimal.ZERO);
        product.setCategory(request.getCategory());
        product.setProductDescription(request.getDescription());
        product.setProductUrl(request.getProductUrl());
        if (request.getActive() != null) {
            product.setIsActive(request.getActive());
        }
    }
}