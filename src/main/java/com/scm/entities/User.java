package com.scm.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="user")
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails{
	
	@Id
	private String userId;
	
	@Column(name="username",nullable=false)
	private String name;
	
	@Column(unique=true,nullable=false)
	private String email;
	
	@Getter(value=AccessLevel.NONE)
	private String password;
	
	@Lob
	@Column(length=10000)
	private String about;
	
	@Lob
	@Column(length=10000)
	private String profilePic;
	
	private String phoneNumber;
	
	@Getter(value=AccessLevel.NONE)
	private boolean enabled=false;
	
	private boolean emailVerified=false;
	private boolean phoneVerified=false;
	
	private String emailToken;
	
//	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<VerificationToken> emailVerificationTokens = new ArrayList<>();

	//how user signup? -> google,github->?
	@Enumerated(value=EnumType.STRING)
	private Providers provider=Providers.SELF;
	
	private String providerUserId;
	
	
	//add more fields if needed
	
	@OneToMany(mappedBy="user",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	 @JsonManagedReference
	private List<Contact> contacts=new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roleList=new ArrayList<>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
		return roles;
	}

	@Override
	public String getUsername() {
		return this.email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", email=" + email + ", password=" + password + ", about="
				+ about + ", phoneNumber=" + phoneNumber + ", enabled=" + enabled + ", emailVerified=" + emailVerified
				+ ", phoneVerified=" + phoneVerified + ", emailToken=" + emailToken + "]";
	}

	
	
	
}
