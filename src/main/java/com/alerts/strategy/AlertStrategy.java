package com.alerts.strategy;

import com.alerts.Alert;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * Defines the contract for alert strategies used to analyze patient records and generate alerts.
 * 
 * @author Melanie Böhmer
 */
public interface AlertStrategy {
    /**
     *  Analyzes a list of patient records and determines whether any alerts should be triggered based on specific criteria. 
     * 
     * @param records the list of patient records to analyze
     * @return a list of generated alerts based on the strategy's logic
     */
    List<Alert> checkAlert(List<PatientRecord> records);
    
}
