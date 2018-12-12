package com.project.technion.appark;

public class XYLocation implements Location{
    private double x;
    private double y;



    public XYLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }



    @Override public double calcDist(Location other) {
        if(!(other instanceof XYLocation)) {
            throw new IllegalArgumentException("Location must be of type LocationImplementation");
        }
        XYLocation other_loc = (XYLocation)other;
        return Math.sqrt(Math.pow(this.x - other_loc.x, 2.0) + Math.pow(this.y - other_loc.y, 2.0));
    }



    @Override public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }
}
