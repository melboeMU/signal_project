package com.alerts.decorator;

import com.alerts.Alert;

/**
 * Decorator that adds priority information to an alert.
 * 
 * This can be used to mark alerts as high priority
 * when urgent medical attention is required.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    
    private final String priorityLevel;

    /**
     * Constructs a priority alert decorator.
     * @param alert the alert to decorate
     * @param priorityLevel the priority level to assign
     */
    public PriorityAlertDecorator(Alert alert, String priorityLevel) {
        super(alert);
        this.priorityLevel = priorityLevel;
    }

   /**
     * Returns the priority level of the alert.
     *
     * @return the priority level
     */
    public String getPriorityLevel() {
        return priorityLevel;
    }

    @Override
    public String toString() {
        return "[PRIORITY: " + priorityLevel + "] " + alert.toString();
    }
    
}
