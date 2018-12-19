package com.project.technion.appark;

public enum SortingBy {

    DISTANCE_LOWEST{
        @Override
        public String toString() {
            return "by distance (lowest)";
        }
    },
    DISTANCE_HIGHEST{
        @Override
        public String toString() {
            return "by distance (highest)";
        }
    },
    PRICE_LOWEST{
        @Override
        public String toString() {
            return "by price (lowest)";
        }
    }
    , PRICE_HiGHEST{
        @Override
        public String toString() {
            return "by price (highest)";
        }
    };

    @Override
    public String toString() {
        return super.toString();
    }
}
