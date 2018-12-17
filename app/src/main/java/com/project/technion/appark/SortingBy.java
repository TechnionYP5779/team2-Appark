package com.project.technion.appark;

public enum SortingBy {
    PRICE{
        @Override
        public String toString() {
            return "by price";
        }
    }, DISTNACE{
        @Override
        public String toString() {
            return "by distance";
        }
    };

    @Override
    public String toString() {
        return super.toString();
    }
}
