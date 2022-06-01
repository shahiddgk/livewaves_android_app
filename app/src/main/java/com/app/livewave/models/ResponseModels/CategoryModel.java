
package com.app.livewave.models.ResponseModels;

import java.util.ArrayList;
import java.util.List;
 import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

 public class CategoryModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("subcategories")
    @Expose
    private List<SubcategoryModel> subcategoryModelList = null;

    private boolean isSelected = false;

     public boolean isSelected() {
         return isSelected;
     }
     public void setSelected(boolean selected) {
         isSelected = selected;
     }
     public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title  == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status  == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleted() {
        return deleted  == null ? "" : deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getCreatedAt() {
        return createdAt  == null ? "" : createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt  == null ? "" : updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<SubcategoryModel> getSubcategoryModelList() {
        return subcategoryModelList  == null ? new ArrayList<>() : subcategoryModelList ;
    }

    public void setSubcategoryModelList(List<SubcategoryModel> subcategoryModelList) {
        this.subcategoryModelList = subcategoryModelList;
    }

}
