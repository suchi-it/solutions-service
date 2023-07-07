package com.solutions.model;

import java.util.Date;
import org.springframework.data.annotation.CreatedDate;

public class Comment {
	private String comments;
	private String createdBy;
	@CreatedDate
	private Date createdAt;

	public String getComment() {
		return comments;
	}

	public void setComment(String comment) {
		this.comments = comment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
