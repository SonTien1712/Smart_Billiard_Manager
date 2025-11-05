package com.BillardManagement.Service;

import com.BillardManagement.DTO.Request.ProductRequestDTO;
import com.BillardManagement.DTO.Response.ProductResponseDTO; // Import DTO đã sửa
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Product;
import com.BillardManagement.Exception.BusinessException;
import com.BillardManagement.Exception.ResourceNotFoundException;
// import com.billardmanagement.mapper.ProductMapper; // <-- XÓA BỎ
import com.BillardManagement.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // Import thêm
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final BillardclubRepo clubRepository;
    private final CustomerRepo customerRepository;
    // private final ProductMapper productMapper; // <-- XÓA BỎ

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProductsByClub(Integer clubId, Boolean activeOnly) {
        log.info("Fetching products for club: {}, activeOnly: {}", clubId, activeOnly);

        List<Product> products = activeOnly != null && activeOnly
                ? productRepository.findByClubIdAndIsActiveTrue(clubId) // từ File 5
                : productRepository.findByClubId(clubId); // từ File 5

        return products.stream()
                // .map(productMapper::toResponseDTO) // <-- THAY THẾ
                .map(ProductResponseDTO::fromEntity) // <-- BẰNG DÒNG NÀY
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Integer id, Integer clubId) {
        log.info("Fetching product with id: {} for club: {}", id, clubId);

        Product product = productRepository.findByIdAndClubId(id, clubId) // từ File 5
                .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                        "Product not found with id: " + id + " for club: " + clubId));

        // return productMapper.toResponseDTO(product); // <-- THAY THẾ
        return ProductResponseDTO.fromEntity(product); // <-- BẰNG DÒNG NÀY
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request, Integer customerId) {
        log.info("Creating new product: {} for club: {}", request.getName(), request.getClubId());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                        "Customer not found with id: " + customerId));

        if (!customer.getIsActive()) {
            throw new BusinessException("Customer account is inactive"); // từ File 7
        }

        Billardclub club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                        "Club not found with id: " + request.getClubId()));

        if (!club.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("Club does not belong to this customer"); // từ File 7
        }

        if (!club.getIsActive()) {
            throw new BusinessException("Club is inactive"); // từ File 7
        }

        // Product product = productMapper.toEntity(request, customer, club); // <-- THAY THẾ
        // Logic từ ProductMapper.toEntity (File 4) được đưa vào đây
        Product product = Product.builder()
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

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());
        // return productMapper.toResponseDTO(savedProduct); // <-- THAY THẾ
        return ProductResponseDTO.fromEntity(savedProduct); // <-- BẰNG DÒNG NÀY
    }

    @Transactional
    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO request, Integer customerId) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                        "Product not found with id: " + id));

        if (!product.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("You don't have permission to update this product"); // từ File 7
        }

        if (!product.getClub().getId().equals(request.getClubId())) {
            Billardclub newClub = clubRepository.findById(request.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                            "Club not found with id: " + request.getClubId()));

            if (!newClub.getCustomer().getId().equals(customerId)) {
                throw new BusinessException("New club does not belong to this customer"); // từ File 7
            }
            product.setClub(newClub);
        }

        // productMapper.updateEntity(product, request); // <-- THAY THẾ
        // Logic từ ProductMapper.updateEntity (File 4) được đưa vào đây
        product.setProductName(request.getName());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice() != null ? request.getCostPrice() : BigDecimal.ZERO);
        product.setCategory(request.getCategory());
        product.setProductDescription(request.getDescription());
        product.setProductUrl(request.getProductUrl());
        if (request.getActive() != null) {
            product.setIsActive(request.getActive());
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        // return productMapper.toResponseDTO(updatedProduct); // <-- THAY THẾ
        return ProductResponseDTO.fromEntity(updatedProduct); // <-- BẰNG DÒNG NÀY
    }

    @Transactional
    public void deleteProduct(Integer id, Integer customerId) {
        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                        "Product not found with id: " + id));

        if (!product.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("You don't have permission to delete this product"); // từ File 7
        }

        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    @Transactional
    public ProductResponseDTO toggleProductStatus(Integer id, Integer customerId) {
        log.info("Toggling status for product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( // từ File 6
                        "Product not found with id: " + id));

        if (!product.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("You don't have permission to modify this product"); // từ File 7
        }

        product.setIsActive(!product.getIsActive());
        Product updatedProduct = productRepository.save(product);

        log.info("Product status toggled successfully with id: {}", updatedProduct.getId());
        // return productMapper.toResponseDTO(updatedProduct); // <-- THAY THẾ
        return ProductResponseDTO.fromEntity(updatedProduct); // <-- BẰNG DÒNG NÀY
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(Integer clubId, String keyword) {
        log.info("Searching products for club: {} with keyword: {}", clubId, keyword);

        List<Product> products = productRepository.searchProducts(clubId, keyword); // từ File 5
        return products.stream()
                // .map(productMapper::toResponseDTO) // <-- THAY THẾ
                .map(ProductResponseDTO::fromEntity) // <-- BẰNG DÒNG NÀY
                .collect(Collectors.toList());
    }
}}