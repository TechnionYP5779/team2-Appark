package com.project.technion.appark;

public enum SortingBy {

    DISTANCE_LOWEST{
        @Override
        public String toString() {
            return "by distance (CLOSEST first)";
        }
    },
    DISTANCE_HIGHEST{
        @Override
        public String toString() {
            return "by distance (FARTHEST first)";
        }
    },
    PRICE_LOWEST{
        @Override
        public String toString() {
            return "by price (LOWEST first)";
        }
    }
    , PRICE_HiGHEST{
        @Override
        public String toString() {
            return "by price (HIGHEST first)";
        }
    };

    @Override
    public String toString() {
        return super.toString();
    }
}
