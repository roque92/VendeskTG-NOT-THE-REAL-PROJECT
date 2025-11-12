package com.vendeskgt.vendeskgt.domain.enums;

public enum RoleEnum {
    ADMIN("Admin"),
    USER("User"),
    TENANT("Tenant");

    private final String roleDisplay;

    RoleEnum(String roleDisplay){
        this.roleDisplay = roleDisplay;
    }

    public String getRoleDisplay(){
        return this.roleDisplay;
    }
}