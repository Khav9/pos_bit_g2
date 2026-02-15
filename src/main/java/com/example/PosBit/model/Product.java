//package com.example.PosBit.model;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//
//import java.util.Date;
//
//@Entity
//@Table(name="product")
//public class Product {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer productId;
//    @Column(nullable = false, length = 300)
//    @NotBlank(message = "Please enter product name!")
//    private String productName;
//    private String productDescription;
//    @NotNull(message = "Price is required")
//    @Min(value = 0, message = "Price must be >= 0")
//    private double productPrice;
//    @Min(value = 0, message = "Quantity must be greater than 0")
//    private Integer productQuantity = 0;
//    @JsonProperty("isActive")
//    private Boolean isActive = true;
//    private Date createdDate;
//    private Date modifiedDate;
//
//    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;
//
//
//    public Product() {}
//
//    public Product(Integer productId, String productName, String productDescription, double productPrice, Integer productQuantity, Category category)
//    {
//        super();
//        this.productId = productId;
//        this.productName = productName;
//        this.productDescription = productDescription;
//        this.productPrice = productPrice;
//        this.productQuantity = productQuantity;
//        this.isActive = true;
//        this.createdDate = new Date();
//        this.modifiedDate = new Date();
//        this.category = category;
//    }
//
//    public Integer getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Integer productId) {
//        this.productId = productId;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getProductDescription() {
//        return productDescription;
//    }
//
//    public void setProductDescription(String productDescription) {
//        this.productDescription = productDescription;
//    }
//
//    public double getProductPrice() {
//        return productPrice;
//    }
//
//    public void setProductPrice(double productPrice) {
//        this.productPrice = productPrice;
//    }
//
//    public Integer getProductQuantity() {
//        return productQuantity;
//    }
//
//    public void setProductQuantity(Integer productQuantity) {
//        this.productQuantity = productQuantity;
//    }
//
//    public Boolean getActive() {
//        return isActive;
//    }
//
//    public void setActive(Boolean active) {
//        isActive = active;
//    }
//
//    public Date getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(Date createdDate) {
//        this.createdDate = createdDate;
//    }
//
//    public Date getModifiedDate() {
//        return modifiedDate;
//    }
//
//    public void setModifiedDate(Date modifiedDate) {
//        this.modifiedDate = modifiedDate;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
//
//}

package com.example.PosBit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "category")
@EqualsAndHashCode(exclude = "category")
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(nullable = false, length = 300)
    @NotBlank(message = "Please enter product name!")
    private String productName;

    private String productDescription;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be >= 0")
    private double productPrice;

    @Min(value = 0, message = "Quantity must be >= 0")
    private Integer productQuantity = 0;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    private Date createdDate;
    private Date modifiedDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
