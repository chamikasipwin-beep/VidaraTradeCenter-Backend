package com.vidara.tradecenter.product.dto.response;

import com.vidara.tradecenter.product.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full detail response for a single product.
 * Used on public detail pages, admin edit pages, and create/update responses.
 */
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String slug;
    private String sku;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private String primaryImageUrl;
    private CategoryResponse category;
    private BrandResponse brand;
    private ProductStatus status;
    private Integer stock;

    // Detail-only fields
    private String description;
    private BigDecimal weight;
    private String dimensions;
    private List<ImageResponse> images;
    private List<SpecificationResponse> specifications;
    private List<String> tags;

    public ProductDetailResponse() {
    }

    // ── Inner classes ─────────────────────────────────────────────────

    public static class ImageResponse {
        private Long id;
        private String imageUrl;
        private String altText;
        private Integer sortOrder;
        private Boolean isPrimary;

        public ImageResponse() {
        }

        public ImageResponse(Long id, String imageUrl, String altText,
                Integer sortOrder, Boolean isPrimary) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.altText = altText;
            this.sortOrder = sortOrder;
            this.isPrimary = isPrimary;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getAltText() {
            return altText;
        }

        public void setAltText(String altText) {
            this.altText = altText;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }
    }

    public static class SpecificationResponse {
        private Long id;
        private String specKey;
        private String specValue;

        public SpecificationResponse() {
        }

        public SpecificationResponse(Long id, String specKey, String specValue) {
            this.id = id;
            this.specKey = specKey;
            this.specValue = specValue;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSpecKey() {
            return specKey;
        }

        public void setSpecKey(String specKey) {
            this.specKey = specKey;
        }

        public String getSpecValue() {
            return specValue;
        }

        public void setSpecValue(String specValue) {
            this.specValue = specValue;
        }
    }

    // ── Getters & Setters ─────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public BrandResponse getBrand() {
        return brand;
    }

    public void setBrand(BrandResponse brand) {
        this.brand = brand;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public List<ImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }

    public List<SpecificationResponse> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationResponse> specifications) {
        this.specifications = specifications;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
