package com.example.Sample.exception;

public class MissingFieldException  extends RuntimeException{
	
	  private String fieldName;

	    public MissingFieldException(String fieldName) {
	        super("Field " + fieldName + " is missing.");
	        this.fieldName = fieldName;
	    }

	    public String getFieldName() {
	        return fieldName;
	    }

}
