public class Date {
    final private int year, month, day;

    public Date() {
        this.year = 0;
        this.month = 0;
        this.day = 0;
    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Date date1 = (Date) o;

        if (year != date1.year)
            return false;
        if (month != date1.month)
            return false;
        return day == date1.day;
    }

    @Override
    public String toString() {
        return String.format("%d年%d月%d日", year, month, day);
    }
}
