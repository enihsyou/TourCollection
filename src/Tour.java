import java.util.Date;

public class Tour implements Comparable<Tour> {
    private String place;
    private Date date;
    private int guests;

    @Override
    public int compareTo(final Tour o) {
        return date.compareTo(o.date);
    }
}
