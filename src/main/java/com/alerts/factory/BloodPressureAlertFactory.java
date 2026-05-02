package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.types.BloodPressureAlert; 

/**
 * Factory for creating Blood Pressure Alerts
 * 
 * @author Melanie Böhmer
 */
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    /**
     * Creates a Blood Pressure Alert with the specified parameters.
     *
     * @param patientId   the ID of the patient
     * @param condition   the condition for the alert
     * @param timestamp   the timestamp of the alert
     * @return the created Blood Pressure Alert
     */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }   
    
}
