package com.alerts.decorator;

import com.alerts.Alert;

/**
 * Decorator that represents a repeated alert.
 * 
 * This can be used to indicate that an alert condition
 * has been detected multiple times over a period.
 */
public class RepeatedAlertDecorater extends AlertDecorator {

    private final int repeatCount;

    /**
     * Constructs a repeated alert decorator.
     *
     * @param alert the alert to decorate
     * @param repeatCount how many times the alert has been triggered
     */
    public RepeatedAlertDecorater(Alert alert, int repeatCount) {
        super(alert);
        this.repeatCount = repeatCount;
    }

    /**
     * Returns how many times the alert has been repeated.
     *
     * @return repeat count
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    @Override
    public String toString() {
        return "[REPEATED x" + repeatCount + "] " + alert.toString();
    }
}