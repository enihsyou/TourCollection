package com.enihsyou.TourCollection;

public class Tourist {
    final private String code; // 3位编号
    final private String name; // 姓名
    final private Gender gender; // 性别
    final private int age; // 年龄
    final private SinglyLinkedList<Tour> tours = new SinglyLinkedList<>(); // 参与的旅行团

    public Tourist(final String code, final String name, final Gender gender, final int age) {
        this.code = code;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Tourist tourist = (Tourist) o;

        return code.equals(tourist.code);
    }

    @Override
    public String toString() {
        return String.format("%s %s %d岁 编号%s", name, gender, age, code);
    }

    public int getTourCount() {
        return tours.size();
    }

    public void addTour(Tour tour) {
        tours.add(tour);
    }

    public boolean hasCollision(Tour trip) {
        for (int i = 0; i < tours.size(); i++) {
            Tour tour = tours.get(i);
            if (tour.getDeparture().equals(trip.getDeparture()))
                return true;
        }
        return false;
    }

    public Object[] toArray() {
        return new Object[]{code, name, gender.toString(), age};
    }

    public void removeTour(Tour selected_trip) {
        tours.remove(selected_trip);
    }

    public String getToursString() {
        return tours.toString();
    }
}
