import java.util.LinkedList;

public class Tour implements Comparable<Tour> {
    final private String where; // 旅行团名称
    final private Date departTime; // 出发时间
    final private LinkedList<Tourist> guests; // 参与的旅客

    public Tour(final String where, final Date when) {
        this.where = where;
        this.departTime = when;
        this.guests = new LinkedList<>();
    }

    public Tour(final String search_key) {
        this.where = search_key;
        this.departTime = new Date();
        this.guests = new LinkedList<>();
    }

    public Date getDepartTime() {
        return departTime;
    }

    public String getGuestsString() {
        return guests.toString();
    }

    public int getGuestsCount() {
        return guests.size();
    }

    public void addGuest(Tourist who) {
        guests.add(who);
    }

    public Tourist getGuest(final int index) {
        return guests.get(index);
    }

    @Override
    public int compareTo(final Tour o) {
        return where.compareTo(o.where);
    }

    @Override
    public int hashCode() {
        int result = where.hashCode();
        result = 31 * result + departTime.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Tour tour = (Tour) o;

        if (!where.equals(tour.where))
            return false;
        return departTime.equals(tour.departTime);
    }

    @Override
    public String toString() {
        return String.format("%s旅行团 @ %s出发", where, departTime);
    }

    public void removeGuest(final Tourist guest) {
        guests.remove(guest);
    }

    public boolean contain(final Tourist selected_guest) {
        return guests.contains(selected_guest);
    }
}
