package testing;

import org.junit.Test;
import replists.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MetricsTest {
    @Test
    public void exceptionThrowForInstanceTest() throws IllegalAccessException, InstantiationException {
        final Class<?> metricsClass = Metrics.class;
        final Constructor<?> c = metricsClass.getDeclaredConstructors()[0];
        c.setAccessible(true);

        Throwable targetException = null;
        try {
            c.newInstance((Object[])null);
        } catch (InvocationTargetException ite) {
            targetException = ite.getTargetException();
        }
        assertNotNull(targetException);
        assertEquals(targetException.getClass(), InstantiationException.class);
    }

    @Test
    public void gatherMetricsTest(){
        assertTrue(Metrics.getAllMetrics());
    }

    @Test
    public void startTest(){
        assertTrue(Metrics.start());
    }

    @Test
    public void stopTest(){
        assertTrue(Metrics.stop());
    }
}
