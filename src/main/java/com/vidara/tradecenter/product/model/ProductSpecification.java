package com.vidara.tradecenter.product.model;

import com.vidara.tradecenter.common.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "product_specifications")
public class ProductSpecification extends BaseEntity {

    @Column(name = "spec_key", nullable = false)
    private String specKey;

    @Column(name = "spec_value", nullable = false)
    private String specValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ProductSpecification() {
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
