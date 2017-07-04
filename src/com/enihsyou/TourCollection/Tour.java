package com.enihsyou.TourCollection;

public class Tour implements Comparable<Tour> {
    final private String name; // 旅行团名称
    final private Date departure; // 出发时间
    final private SinglyLinkedList<Tourist> tourists; // 参与的旅客

    public Tour(final String name, final Date when) {
        this.name = name;
        this.departure = when;
        this.tourists = new SinglyLinkedList<>();
    }

    public Tour(final String search_key) {
        this.name = search_key;
        this.departure = new Date();
        this.tourists = new SinglyLinkedList<>();
    }

    public Date getDeparture() {
        return departure;
    }

    public String getGuestsString() {
        return tourists.toString();
    }

    public int getGuestsCount() {
        return tourists.size();
    }

    public void addGuest(Tourist who) {
        tourists.add(who);
    }

    public Tourist getGuest(final int index) {
        return tourists.get(index);
    }

    @Override
    public int compareTo(final Tour o) {
        return java.text.Collator.getInstance().compare(name, o.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + departure.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Tour tour = (Tour) o;

        if (!name.equals(tour.name))
            return false;
        return departure.equals(tour.departure);
    }

    @Override
    public String toString() {
        return String.format("%s旅行团 @ %s出发", name, departure);
    }

    public void removeGuest(final Tourist guest) {
        tourists.remove(guest);
    }

    public boolean contain(final Tourist selected_guest) {
        return tourists.contains(selected_guest);
    }
}
