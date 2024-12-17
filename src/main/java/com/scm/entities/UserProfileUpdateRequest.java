package com.scm.entities;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {

	private String id;
	
	@NotEmpty(message = "Name is required")
    private String name;
    
    private String about;

}
