package alerts; 

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alerts.Alert;
import org.junit.jupiter.api.Test;
import com.alerts.decorator.AlertDecorator;
import com.alerts.decorator.PriorityAlertDecorator; 
import com.alerts.decorator.RepeatedAlertDecorator;

/**
 * Tests the alert decorator classes.
 */
class AlertDecoratorTest {

    /**
     * Tests that AlertDecorator stores and exposes the decorated alert.
     */
    @Test
    void alertDecoratorStoresDecoratedAlert() {
        Alert originalAlert = new Alert("1", "Low blood oxygen saturation", 1000L);

        AlertDecorator decorator = new TestAlertDecorator(originalAlert);

        assertSame(originalAlert, decorator.getDecoratedAlert());
        assertEquals("1", decorator.getPatientId());
        assertEquals("Low blood oxygen saturation", decorator.getCondition());
        assertEquals(1000L, decorator.getTimestamp());
    }

    /**
     * Tests that PriorityAlertDecorator stores the priority level correctly.
     */
    @Test
    void priorityAlertDecoratorStoresPriorityLevel() {
        Alert originalAlert = new Alert("2", "Critical blood pressure threshold", 2000L);

        PriorityAlertDecorator decorator =
                new PriorityAlertDecorator(originalAlert, "HIGH");

        assertSame(originalAlert, decorator.getDecoratedAlert());
        assertEquals("HIGH", decorator.getPriorityLevel());
        assertEquals("2", decorator.getPatientId());
        assertEquals("Critical blood pressure threshold", decorator.getCondition());
        assertEquals(2000L, decorator.getTimestamp());
        assertTrue(decorator.toString().contains("HIGH"));
    }

    /**
     * Tests that RepeatedAlertDecorator stores the repeat count correctly.
     */
    @Test
    void repeatedAlertDecoratorStoresRepeatCount() {
        Alert originalAlert = new Alert("3", "Abnormal ECG peak", 3000L);

        RepeatedAlertDecorator decorator =
                new RepeatedAlertDecorator(originalAlert, 3);

        assertSame(originalAlert, decorator.getDecoratedAlert());
        assertEquals(3, decorator.getRepeatCount());
        assertEquals("3", decorator.getPatientId());
        assertEquals("Abnormal ECG peak", decorator.getCondition());
        assertEquals(3000L, decorator.getTimestamp());
        assertTrue(decorator.toString().contains("3"));
    }

    /**
     * Test implementation because AlertDecorator is abstract.
     */
    private static class TestAlertDecorator extends AlertDecorator {

        TestAlertDecorator(Alert alert) {
            super(alert);
        }
    }
}
