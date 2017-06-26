public class Guest {
    private String code;
    private String name;
    private Gender gender;
    private int age;

    private enum Gender {
        MALE, FEMELE, OTHER
    }
}
