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
    private LocalDate orderDate;
    private int orderNum;
    
    private String customerName;
    
    private String state;
    private BigDecimal percentTaxRate;
    
    private FlooringMasteryProduct orderedProd;
    private BigDecimal area;

    public FlooringMasteryOrder(
        LocalDate orderDate, 
        int orderNum, 
        String customerName, 
        String state, 
        BigDecimal percentTaxRate, 
        FlooringMasteryProduct orderedProd, 
        BigDecimal area) {
        
        this.orderDate = orderDate;
        this.orderNum = orderNum;
        this.customerName = customerName;
        this.state = state;
        this.percentTaxRate = percentTaxRate;
        this.orderedProd = orderedProd;
        this.area = area;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getState() {
        return state;
    }

    public BigDecimal getPercentTaxRate() {
        return percentTaxRate;
    }

    public String getProductType() {
        return orderedProd.getType();
    }
    
    public BigDecimal getArea() {
        return area;
    }
    
    public BigDecimal getCostPerSqFt() {
        return orderedProd.getCostPerSqFt();
    }
    
    public BigDecimal getLaborCostPerSqFt() {
        return orderedProd.getLaborCostPerSqFt();
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
        hash = 53 * hash + Objects.hashCode(this.orderDate);
        hash = 53 * hash + this.orderNum;
        hash = 53 * hash + Objects.hashCode(this.customerName);
        hash = 53 * hash + Objects.hashCode(this.state);
        hash = 53 * hash + Objects.hashCode(this.percentTaxRate);
        hash = 53 * hash + Objects.hashCode(this.orderedProd);
        hash = 53 * hash + Objects.hashCode(this.area);
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
        if (this.orderNum != other.orderNum) {
            return false;
        }
        if (!Objects.equals(this.customerName, other.customerName)) {
            return false;
        }
        if (!Objects.equals(this.state, other.state)) {
            return false;
        }
        if (!Objects.equals(this.orderDate, other.orderDate)) {
            return false;
        }
        if (!Objects.equals(this.percentTaxRate, other.percentTaxRate)) {
            return false;
        }
        if (!Objects.equals(this.orderedProd, other.orderedProd)) {
            return false;
        }
        return Objects.equals(this.area, other.area);
    }

    @Override
    public String toString() {
        return "FlooringMasteryOrder{" + "orderDate=" + orderDate + ", orderNum=" + orderNum + ", customerName=" + customerName + ", state=" + state + ", percentTaxRate=" + percentTaxRate + ", orderedProd=" + orderedProd + ", area=" + area + '}';
    }
}