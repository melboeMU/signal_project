package data_management;

import org.junit.jupiter.api.Test;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void shouldReturnRecordsWithinGivenTimeRange() {
        Patient patient = new Patient(1);

        patient.addRecord(100, "ECG", 1000);
        patient.addRecord(110, "ECG", 2000);
        patient.addRecord(120, "ECG", 3000);

        List<PatientRecord> result = patient.getRecords(1500, 2500);

        assertEquals(1, result.size());
        assertEquals(110, result.get(0).getMeasurementValue());
        assertEquals("ECG", result.get(0).getRecordType());
        assertEquals(2000, result.get(0).getTimestamp());
    }

    @Test
    void shouldIncludeRecordsAtStartAndEndTime() {
        Patient patient = new Patient(1);

        patient.addRecord(100, "HeartRate", 1000);
        patient.addRecord(110, "HeartRate", 2000);
        patient.addRecord(120, "HeartRate", 3000);

        List<PatientRecord> result = patient.getRecords(1000, 3000);

        assertEquals(3, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoRecordsAreInTimeRange() {
        Patient patient = new Patient(1);

        patient.addRecord(100, "BloodSaturation", 1000);
        patient.addRecord(110, "BloodSaturation", 2000);

        List<PatientRecord> result = patient.getRecords(3000, 4000);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenPatientHasNoRecords() {
        Patient patient = new Patient(1);

        List<PatientRecord> result = patient.getRecords(1000, 3000);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllRecordsWhenFullTimeRangeIsUsed() {
        Patient patient = new Patient(1);

        patient.addRecord(80, "HeartRate", 1000);
        patient.addRecord(95, "BloodSaturation", 2000);
        patient.addRecord(1.5, "ECG", 3000);

        List<PatientRecord> result = patient.getRecords(0, Long.MAX_VALUE);

        assertEquals(3, result.size());
    }
}