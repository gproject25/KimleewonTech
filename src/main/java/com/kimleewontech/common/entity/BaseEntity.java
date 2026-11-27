package com.kimleewontech.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@CreatedBy
	@Column(nullable = false, updatable = false)
	private Long createdBy;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@LastModifiedBy
	@Column(nullable = false)
	private Long updatedBy;

	@Column
	private LocalDateTime deletedAt;

	@Column
	private Long deletedBy;

	public void softDelete(Long deletedBy) {
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = deletedBy;
	}

	public void restore() {
		this.deletedAt = null;
		this.deletedBy = null;
	}

	public boolean isDeleted() { // 외부에서 상태 확인 용도로 쓰이기 때문에 public이 맞음
		return this.deletedAt != null;
	}
}