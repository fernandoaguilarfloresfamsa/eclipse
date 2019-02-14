package com.famsa.enums;

public enum EstadoEnum {

	AC ("ACTIVO"),
	PR ("PROCESADO");
	
    private final String name;       

    private EstadoEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return name.equals(otherName);
    }

    @Override
    public String toString() {
       return this.name;
    }	
	
}
