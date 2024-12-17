package com.scm.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {

	String uplaodImage(MultipartFile contactImage,String fileName);
	
	String getUrlFromPublicId(String publicId);
}
