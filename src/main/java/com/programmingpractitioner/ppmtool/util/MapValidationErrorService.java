package com.programmingpractitioner.ppmtool.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MapValidationErrorService {
    public ResponseEntity<?> mavValidationService(BindingResult result) {
        if (result.hasErrors()){
            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fieldError:fieldErrors
            ) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
