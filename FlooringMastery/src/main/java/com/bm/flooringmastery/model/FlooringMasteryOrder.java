package com.bm.flooringmastery.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an Order
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryOrder {
    private final LocalDate ORDER_DATE;
    private final int ORDER_NUM;
    
    private final String CUSTOMER_NAME;
    
    private final String STATE;
    private final BigDecimal PERCENT_TAX_RATE;
    
    private final FlooringMasteryProduct ORDERED_PROD;
    private final BigDecimal AREA;

    public FlooringMasteryOrder(
        LocalDate orderDate, 
        int orderNum, 
        String customerName, 
        String state, 
        BigDecimal percentTaxRate, 
        FlooringMasteryProduct orderedProd, 
        BigDecimal area) {
        
        this.ORDER_DATE = orderDate;
        this.ORDER_NUM = orderNum;
        this.CUSTOMER_NAME = customerName;
        this.STATE = state;
        this.PERCENT_TAX_RATE = percentTaxRate;
        this.ORDERED_PROD = orderedProd;
        this.AREA = area;
    }

    public LocalDate getOrderDate() {
        return ORDER_DATE;
    }

    public int getOrderNum() {
        return ORDER_NUM;
    }

    public String getCustomerName() {
        return CUSTOMER_NAME;
    }

    public String getState() {
        return STATE;
    }

    public BigDecimal getPercentTaxRate() {
        return PERCENT_TAX_RATE;
    }

    public String getProductType() {
        return ORDERED_PROD.getType();
    }
    
    public BigDecimal getArea() {
        return AREA;
    }
    
    public BigDecimal getCostPerSqFt() {
        return ORDERED_PROD.getCostPerSqFt();
    }
    
    public BigDecimal getLaborCostPerSqFt() {
        return ORDERED_PROD.getLaborCostPerSqFt();
    }
    
    public BigDecimal getMaterialCost() {
        return getArea().multiply(getCostPerSqFt());
    }
    
    public BigDecimal getLaborCost() {
        return getArea().multiply(getLaborCostPerSqFt());
    }
    
    private BigDecimal getUntaxedTotalCost() {
        return getMaterialCost().add(getLaborCost());
    }
    
    public BigDecimal getTax() {
        BigDecimal trueTaxRate = getPercentTaxRate().divide(new BigDecimal("100"));
        return getUntaxedTotalCost().multiply(trueTaxRate);
    }
    
    public BigDecimal getTotal() {
        return getUntaxedTotalCost().add(getTax());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.ORDER_DATE);
        hash = 53 * hash + this.ORDER_NUM;
        hash = 53 * hash + Objects.hashCode(this.CUSTOMER_NAME);
        hash = 53 * hash + Objects.hashCode(this.STATE);
        hash = 53 * hash + Objects.hashCode(this.PERCENT_TAX_RATE);
        hash = 53 * hash + Objects.hashCode(this.ORDERED_PROD);
        hash = 53 * hash + Objects.hashCode(this.AREA);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FlooringMasteryOrder other = (FlooringMasteryOrder) obj;
        if (this.ORDER_NUM != other.ORDER_NUM) {
            return false;
        }
        if (!Objects.equals(this.CUSTOMER_NAME, other.CUSTOMER_NAME)) {
            return false;
        }
        if (!Objects.equals(this.STATE, other.STATE)) {
            return false;
        }
        if (!Objects.equals(this.ORDER_DATE, other.ORDER_DATE)) {
            return false;
        }
        if (!Objects.equals(this.PERCENT_TAX_RATE, other.PERCENT_TAX_RATE)) {
            return false;
        }
        if (!Objects.equals(this.ORDERED_PROD, other.ORDERED_PROD)) {
            return false;
        }
        return Objects.equals(this.AREA, other.AREA);
    }

    @Override
    public String toString() {
        return "FlooringMasteryOrder{" + "orderDate=" + ORDER_DATE + ", orderNum=" + ORDER_NUM + ", customerName=" + CUSTOMER_NAME + ", state=" + STATE + ", percentTaxRate=" + PERCENT_TAX_RATE + ", orderedProd=" + ORDERED_PROD + ", area=" + AREA + '}';
    }
}