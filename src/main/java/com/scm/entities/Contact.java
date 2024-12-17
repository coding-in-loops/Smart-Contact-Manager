package com.scm.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

	@Id
	private String contactId;
	private String name;
	private String email;
	private String phoneNumber;
	private String address;
	@Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
	@PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
	private String picture;
	@Lob
	@Column(length=10000)
	private String description;
	private boolean favorite=false;
	private String websiteLink;
	private String LinkedInLink;
	private String cloudinaryImagePublicId;
	@ManyToOne
	@JsonBackReference
	private User user;
	@OneToMany(mappedBy="contact",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
	private List<SocialLink> links=new ArrayList<>();
}
