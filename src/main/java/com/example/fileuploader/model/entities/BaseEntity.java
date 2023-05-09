package com.example.fileuploader.model.entities;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class BaseEntity implements Serializable {
    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Date executedAt;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private Date updateAt;

    public BaseEntity() {
    }

    public BaseEntity(String createdBy, Date executedAt, String updatedBy, Date updateAt) {
        this.createdBy = createdBy;
        this.executedAt = executedAt;
        this.updatedBy = updatedBy;
        this.updateAt = updateAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}
