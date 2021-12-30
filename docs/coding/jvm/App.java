public class App {
    public static void main(final String[] args) {
        int total = 1;
        for (int i = 1; i <= 200000; i++) {
            total = calc(total, i);
        }
        System.out.println("Total is: " + total);
    }

    public static int calc(final int current, final int add) {
        return current + add;
    }
}