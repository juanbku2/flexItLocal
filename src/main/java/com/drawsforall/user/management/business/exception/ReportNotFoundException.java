package com.drawsforall.user.management.business.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String query){
        super("No se encontro ningun registro con el siguiente dato: " + query);
    }
}
