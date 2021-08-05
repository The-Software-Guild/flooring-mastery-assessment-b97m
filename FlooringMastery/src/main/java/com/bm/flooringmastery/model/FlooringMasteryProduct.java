package com.bm.flooringmastery.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a Product
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryProduct {
    private final String TYPE;
    private final BigDecimal COST_PER_SQ_FT;
    private final BigDecimal LABOR_COST_PER_SQ_FT;

    public FlooringMasteryProduct(
        String type, 
        BigDecimal costPerSqFt, 
        BigDecimal laborCostPerSqFt) {
        
        this.TYPE = type;
        this.COST_PER_SQ_FT = costPerSqFt;
        this.LABOR_COST_PER_SQ_FT = laborCostPerSqFt;
    }

    public String getType() {
        return TYPE;
    }

    public BigDecimal getCostPerSqFt() {
        return COST_PER_SQ_FT;
    }

    public BigDecimal getLaborCostPerSqFt() {
        return LABOR_COST_PER_SQ_FT;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.TYPE);
        hash = 23 * hash + Objects.hashCode(this.COST_PER_SQ_FT);
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
        final FlooringMasteryProduct other = (FlooringMasteryProduct) obj;
        if (!Objects.equals(this.TYPE, other.TYPE)) {
            return false;
        }
        if (!Objects.equals(this.COST_PER_SQ_FT, other.COST_PER_SQ_FT)) {
            return false;
        }
        return Objects.equals(this.LABOR_COST_PER_SQ_FT, other.LABOR_COST_PER_SQ_FT);
    }

    @Override
    public String toString() {
        return "FlooringMasteryProduct{" + "TYPE=" + TYPE + ", COST_PER_SQ_FT=" + COST_PER_SQ_FT + ", LABOR_COST_PER_SQ_FT=" + LABOR_COST_PER_SQ_FT + '}';
    }
}
