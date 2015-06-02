/*
 * Copyright 2013 NextBus, Inc.
 * All Rights Reserved.
 */
package nextbus.predictiondist;

/**
 * Provides static comparison method.
 * @author noky
 */
public class Compare {
    private Compare() {}

    /**
     * Tests whether two objects are equal in a null pointer safe way. 
     * The objects are equal if they are both null, or their values are 
     * equal as determined by the equals method of the o1 object.
     * @param o1 first object (may be null)
     * @param o2 second object (may be null)
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) { return (o2 == null); }
        return (o1.equals(o2));
    }
}