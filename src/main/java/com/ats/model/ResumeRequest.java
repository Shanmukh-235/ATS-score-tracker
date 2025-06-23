package com.ats.model;

import org.springframework.web.multipart.MultipartFile;

public class ResumeRequest {
    private String name;
    private String role;
    private MultipartFile resume;

    public ResumeRequest() {
		
	}
    
    public ResumeRequest(String name, String role, MultipartFile resume) {
		super();
		this.name = name;
		this.role = role;
		this.resume = resume;
	}

	public String getName() { 
    	return name; 
    }
    public void setName(String name) { 
    	this.name = name; 
    }

    public String getRole() { 
    	return role; 
    }
    public void setRole(String role) { 
    	this.role = role; 
    }

    public MultipartFile getResume() { 
    	return resume; 
    }
    public void setResume(MultipartFile resume) { 
    	this.resume = resume; 
    }
}
