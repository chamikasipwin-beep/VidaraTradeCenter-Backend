package com.vidara.tradecenter.product.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    private String name;

    private String description;

    private String logoUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
