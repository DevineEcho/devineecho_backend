package com.example.devineecho.exception;

public class InsufficientCurrencyException extends RuntimeException {
    private final String currencyType;
    private final int missingAmount;

    public InsufficientCurrencyException(String currencyType, int missingAmount) {
        super(currencyType + "가 " + missingAmount + " 부족합니다.");
        this.currencyType = currencyType;
        this.missingAmount = missingAmount;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public int getMissingAmount() {
        return missingAmount;
    }
}
