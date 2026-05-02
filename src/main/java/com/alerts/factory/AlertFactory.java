package com.alerts.factory;

import com.alerts.Alert;

/**
 * Abstract factory class for creating different types of health alerts, it creates Alert objects
 * without specifying their exact concrete class
 * 
 * @author Melanie Böhmer
 */
public abstract class AlertFactory {

    /**
     * Creates an alert of a specific type based on the provided parameters
     * 
     * @param patientId the ID of the patient
     * @param condition the health condition triggering the alert
     * @param timestamp the time when the alert was triggered
     * @return the created alert object
     */
    public abstract Alert createAlert(
        String patientId,
        String condition,
        long timestamp
    );
    
};
