package com.data_management;

/**
 * Central source of truth for record type labels used by generators, readers,
 * storage, alert logic, and tests.
 */
public final class RecordLabels {

    private RecordLabels() {
        // Utility class. Prevent instantiation.
    }

    public static final String HEART_RATE = "HeartRate";
    public static final String BLOOD_SATURATION = "BloodSaturation";
    public static final String SYSTOLIC_BLOOD_PRESSURE = "SystolicBloodPressure";
    public static final String DIASTOLIC_BLOOD_PRESSURE = "DiastolicBloodPressure";
    public static final String ECG = "ECG";
    public static final String CHOLESTEROL = "Cholesterol";
    public static final String WHITE_BLOOD_CELLS = "WhiteBloodCells";
    public static final String RED_BLOOD_CELLS = "RedBloodCells";
    public static final String TRIGGERED_ALERT = "TriggeredAlert";

    /**
     * Normalizes older or inconsistent labels to the standard labels above.
     * This lets older generated files keep working while new code uses only
     * the constants in this class.
     *
     * @param rawLabel label from generator, file, WebSocket message, or test
     * @return standardized record label
     */
    public static String normalize(String rawLabel) {
        if (rawLabel == null) {
            throw new IllegalArgumentException("record label must not be null");
        }

        String label = rawLabel.trim();
        String key = label.replace(" ", "").replace("_", "").replace("-", "").toLowerCase();

        if (key.equals("heartrate") || key.equals("pulse")) {
            return HEART_RATE;
        }

        if (key.equals("saturation")
                || key.equals("bloodsaturation")
                || key.equals("oxygensaturation")
                || key.equals("bloodoxygensaturation")
                || key.equals("spo2")) {
            return BLOOD_SATURATION;
        }

        if (key.equals("systolicpressure")
                || key.equals("systolicbloodpressure")
                || key.equals("systolicbp")) {
            return SYSTOLIC_BLOOD_PRESSURE;
        }

        if (key.equals("diastolicpressure")
                || key.equals("diastolicbloodpressure")
                || key.equals("diastolicbp")) {
            return DIASTOLIC_BLOOD_PRESSURE;
        }

        if (key.equals("ecg") || key.equals("electrocardiogram")) {
            return ECG;
        }

        if (key.equals("cholesterol")) {
            return CHOLESTEROL;
        }

        if (key.equals("whitebloodcells") || key.equals("wbc")) {
            return WHITE_BLOOD_CELLS;
        }

        if (key.equals("redbloodcells") || key.equals("rbc")) {
            return RED_BLOOD_CELLS;
        }

        if (key.equals("alert") || key.equals("triggeredalert") || key.equals("manualalert")) {
            return TRIGGERED_ALERT;
        }

        return label;
    }
}
