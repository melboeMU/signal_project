package com.alerts.decorator;

import com.alerts.Alert;

/**
 * Abstract base class for all alert decorators.
 * 
 * The AlertDecorator wraps an existing Alert object and allows
 * additional functionality to be added dynamically without
 * modifying the original class.
 * In this case i did not change Alert to an Interface to keep the structure 
 */
public class AlertDecorator extends Alert {
    protected Alert alert;

    /**
     * Constructor that takes an Alert object to wrap.
     * @param alert
     */
    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());
        this.alert = alert;
    }
    
    /**
     * Returns the wrapped alert.
     *
     * @return the original decorated alert
     */
    public Alert getDecoratedAlert() {
        return alert;
    }
    
}
