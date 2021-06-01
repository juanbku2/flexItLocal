package com.drawsforall.user.management.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissFieldException extends RuntimeException{



    public MissFieldException(String email){
        super(email);
    }

}
