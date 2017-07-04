package com.enihsyou.TourCollection;

public enum Gender {
    MALE("男"), FEMALE("女");
    final private String chinese;

    Gender(final String chinese) {
        this.chinese = chinese;
    }

    @Override
    public String toString() {
        return chinese;
    }
}
