package com.scm.validators;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile,MultipartFile>{

	private static final long MAX__FILE_SIZE=1024*1024*5; //5 MB FILE
	
	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		
		if(file==null || file.isEmpty()) {
			//context.disableDefaultConstraintViolation();
			//context.buildConstraintViolationWithTemplate("File can not be empty!").addConstraintViolation();
			return false;
		}
		
		System.out.println("File size: "+file.getSize());
		
		//file size
		if(file.getSize()>MAX__FILE_SIZE) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("File size should be less than 5 MB!").addConstraintViolation();
			return false;
		}
		//resolution
		/*try {
			BufferedImage bufferedImage= ImageIO.read(file.getInputStream());
			if(bufferedImage.getHeight()>MAX_FILE_HEIGHT) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		
		return true;
	}

}
