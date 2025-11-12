package com.vendeskgt.vendeskgt.domain.enums;

public enum PlanTypeEnum {
    MONTHLY("Mensual"),
    ANNUAL("Anual");

    private final String planTypeDisplay;

    PlanTypeEnum(String planTypeDisplay){
        this.planTypeDisplay = planTypeDisplay;
    }

    public String getPlanTypeDisplay() {
        return planTypeDisplay;
    }
}
