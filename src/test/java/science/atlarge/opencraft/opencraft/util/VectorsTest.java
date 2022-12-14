package science.atlarge.opencraft.opencraft.util;

import org.bukkit.util.Vector;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VectorsTest {

    private double EPSILON = 0.0000000000001d;

    @Test
    public void floorTest() {

        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector floored = Vectors.floor(vector);

        assertEquals(floored.getX(), 0.0, EPSILON);
        assertEquals(floored.getY(), 1.0, EPSILON);
        assertEquals(floored.getZ(), -1.0, EPSILON);
    }

    @Test
    public void ceilTest() {

        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector ceiled = Vectors.ceil(vector);

        assertEquals(ceiled.getX(), 1.0, EPSILON);
        assertEquals(ceiled.getY(), 1.0, EPSILON);
        assertEquals(ceiled.getZ(), 0.0, EPSILON);
    }

    @Test
    public void projectTest() {

        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector normal = new Vector(1.0, 0.0, 0.0);
        Vector projected = Vectors.project(vector, normal);

        assertEquals(projected.getX(), 0.1, EPSILON);
        assertEquals(projected.getY(), 0.0, EPSILON);
        assertEquals(projected.getZ(), 0.0, EPSILON);
    }

    @Test
    public void projectTest1() {

        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector normal = new Vector(0.0, 1.0, 0.0);
        Vector projected = Vectors.project(vector, normal);

        assertEquals(projected.getX(), 0.0, EPSILON);
        assertEquals(projected.getY(), 1.0, EPSILON);
        assertEquals(projected.getZ(), 0.0, EPSILON);
    }

    @Test
    public void projectTest2() {

        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector normal = new Vector(0.0, 0.0, 1.0);
        Vector projected = Vectors.project(vector, normal);

        assertEquals(projected.getX(), 0.0, EPSILON);
        assertEquals(projected.getY(), 0.0, EPSILON);
        assertEquals(projected.getZ(), -0.9, EPSILON);
    }

    @Test
    public void clampTestPositiveClamped() {

        Vector vector = new Vector(0.0, 4.0, 3.0);
        Vector projected = Vectors.clamp(vector, 1.0);

        assertEquals(projected.getX(), 0.0, EPSILON);
        assertEquals(projected.getY(), 0.8, EPSILON);
        assertEquals(projected.getZ(), 0.6, EPSILON);
    }

    @Test
    public void clampTestPositiveNotClamped() {

        Vector vector = new Vector(0.0, 0.8, 0.6);
        Vector projected = Vectors.clamp(vector, 1.0);

        assertEquals(projected.getX(), 0.0, EPSILON);
        assertEquals(projected.getY(), 0.8, EPSILON);
        assertEquals(projected.getZ(), 0.6, EPSILON);
    }

    @Test
    public void clampTestNegativeClamped() {

        Vector vector = new Vector(0.0, -4.0, -3.0);
        Vector projected = Vectors.clamp(vector, 1.0);

        assertEquals(projected.getX(), 0.0, EPSILON);
        assertEquals(projected.getY(), -0.8, EPSILON);
        assertEquals(projected.getZ(), -0.6, EPSILON);
    }

    @Test
    public void clampTestNegativeNotClamped() {

        Vector vector = new Vector(0.0, -0.8, -0.6);
        Vector projected = Vectors.clamp(vector, 1.0);

        assertEquals(projected.getX(), 0.0, EPSILON);
        assertEquals(projected.getY(), -0.8, EPSILON);
        assertEquals(projected.getZ(), -0.6, EPSILON);
    }

    @Test
    public void equalsTest() {
        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector otherVector = new Vector(0.2 / 2.0, 2.0 / 2.0, -1.8 / 2.0);

        assertEquals(Vectors.equals(vector, otherVector, EPSILON), true);
    }

    @Test
    public void equalEarlyExitXTest() {
        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector otherVector = mock(Vector.class);

        when(otherVector.getX()).thenReturn(0.0);
        assertEquals(Vectors.equals(vector, otherVector, EPSILON), false);

        verify(otherVector, times(0)).getY();

    }

    @Test
    public void equalEarlyExitYTest() {

        Vector vector = new Vector(0.1, 1.0, -0.9);
        Vector otherVector = mock(Vector.class);

        when(otherVector.getX()).thenReturn(0.1);
        when(otherVector.getY()).thenReturn(0.5);
        assertEquals(Vectors.equals(vector, otherVector, EPSILON), false);

        verify(otherVector, times(0)).getZ();
    }

    @Test
    public void computeVolumeTest() {
        Vector vector = new Vector(5.0, 1.0, 3.0);
        assertEquals(15.0, Vectors.computeVolume(vector), Double.MIN_VALUE);
    }

    @Test
    public void flipInfinityTest() {
        Vector positiveInfinity = new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 5.0);
        Vector flipped = new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 5.0);
        Vectors.flipInfinity(positiveInfinity);
        assertTrue(Vectors.equals(positiveInfinity, flipped));
    }

    @Test
    public void flipInfinityNegativeTest() {
        Vector positiveInfinity = new Vector(Double.NEGATIVE_INFINITY, 2.0, 5.0);
        Vector flipped = new Vector(Double.POSITIVE_INFINITY, 2.0, 5.0);
        Vectors.flipInfinity(positiveInfinity);
        assertTrue(Vectors.equals(positiveInfinity, flipped));
    }
}
