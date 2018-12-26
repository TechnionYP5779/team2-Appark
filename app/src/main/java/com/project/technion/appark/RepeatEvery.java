package com.project.technion.appark;

public enum RepeatEvery {
    NO_REPEAT{

    },
    DAY{
        public int getDaysNumber(){return 1;}
    },
    WEEK{
        public int getDaysNumber(){return 7;}
    };

    public int getDaysNumber(){return 7;}

}
