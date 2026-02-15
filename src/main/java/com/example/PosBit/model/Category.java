//package com.example.PosBit.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//
//import java.util.List;
//
//@Entity
//@Table(name="category")
//public class Category {
//
//    @Id
//    private String categoryId;
//    private String categoryName;
//
//    private String categoryDescription;
//
//    private String status;
//
//    @OneToMany(mappedBy = "category")
//    @JsonIgnore
//    private List<Product> products;
//
//    public Category() {}
//
//    public Category(String categoryId, String categoryName, String categoryDescription, String status) {
//        super();
//        this.categoryId = categoryId;
//        this.categoryName = categoryName;
//        this.categoryDescription = categoryDescription;
//        this.status = status;
//    }
//
//    public String getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(String categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = categoryName;
//    }
//
//    public String getCategoryDescription() {
//        return categoryDescription;
//    }
//
//    public void setCategoryDescription(String categoryDescription) {
//        this.categoryDescription = categoryDescription;
//    }
//
//    public String getActive() {
//        return status;
//    }
//
//    public void setActive(String status) {
//        this.status = status;
//    }
//}



/**
 * This is version of medel using lombok
 */

package com.example.PosBit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "category")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    private String categoryId;

    private String categoryName;

    private String categoryDescription;

    private String status;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}
