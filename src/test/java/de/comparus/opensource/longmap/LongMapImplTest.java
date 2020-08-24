package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.exceptions.NoBucketWithSuchKeyException;
import de.comparus.opensource.longmap.utils.ContextValidation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;


public class LongMapImplTest {
    private LongMap testObject;
    private long firstTestKey;
    private long secondTestKey;
    private String firstTestValue;
    private String secondTestValue;

    @Mock
    private ContextValidation context;

    @Before
    public void setUp() {
        firstTestKey = 1L;
        firstTestValue = "value";
        secondTestKey = 2L;
        secondTestValue = "value2";

        initMocks(this);
        testObject = new LongMapImpl<>();
        ((LongMapImpl) testObject).setContext(context);
    }

    @Test
    public void testPut() {
        testObject.put(firstTestKey, firstTestValue);
        assertEquals(1, testObject.size());
    }

    @Test
    public void testGet() {
        testObject.put(firstTestKey, firstTestValue);
        Object result = testObject.get(firstTestKey);
        assertEquals(firstTestValue, result);
    }

    @Test
    public void testGetNullCase() {
        testObject.put(firstTestKey, firstTestValue);
        Object result = testObject.get(secondTestKey);
        assertNull(result);
    }

    @Test
    public void testRemove() throws NoBucketWithSuchKeyException {
        prepareMap();

        String expResult = secondTestValue;
        Object result = testObject.remove(secondTestKey);
        assertEquals(expResult, result);
    }

    @Test(expected = NoBucketWithSuchKeyException.class)
    public void testRemoveException() throws NoBucketWithSuchKeyException {
        testObject.put(firstTestKey, firstTestValue);
        testObject.remove(secondTestKey);
    }

    @Test
    public void testIsEmptyTrueCase() {
        Assert.assertTrue(testObject.isEmpty());
    }

    @Test
    public void testIsEmptyFalseCase() {
        testObject.put(firstTestKey, firstTestValue);
        Assert.assertFalse(testObject.isEmpty());
    }

    @Test
    public void testContainsKeyTrueCase() {
        testObject.put(firstTestKey, firstTestValue);
        Assert.assertTrue(testObject.containsKey(firstTestKey));
    }

    @Test
    public void testContainsKeyFalseCase() {
        testObject.put(firstTestKey, firstTestValue);
        Assert.assertFalse(testObject.containsKey(secondTestKey));
    }

    @Test
    public void testContainsValueTrueCase() {
        testObject.put(firstTestKey, firstTestValue);
        Assert.assertTrue(testObject.containsValue(firstTestValue));
    }

    @Test
    public void testContainsValueFalseCase() {
        testObject.put(firstTestKey, firstTestValue);
        Assert.assertFalse(testObject.containsValue(secondTestKey));
    }

    @Test
    public void testKeys() {
        prepareMap();
        long [] expectedArray = {firstTestKey, secondTestKey};

        long [] actualArray = testObject.keys();

        assertEquals(expectedArray[0], actualArray[0]);
        assertEquals(expectedArray[1], actualArray[1]);
    }

    @Test
    public void testValues() {
        prepareMap();
        Object [] expectedArray = {firstTestValue, secondTestValue};

        Object [] actualArray = testObject.values();

        assertEquals(expectedArray[0], actualArray[0]);
        assertEquals(expectedArray[1], actualArray[1]);
    }

    public void prepareMap() {
        testObject.put(firstTestKey, firstTestValue);
        testObject.put(secondTestKey, secondTestValue);
    }

    @Test
    public void size() {
        testObject.put(firstTestKey, firstTestValue);
        assertEquals(1, testObject.size());
    }

    @Test
    public void clear() {
        prepareMap();
        testObject.clear();
        assertEquals(0, testObject.size());
    }
}