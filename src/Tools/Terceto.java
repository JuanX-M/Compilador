package Tools;

public class Terceto {

    private int numTerceto;
    private String first;
    private String second;
    private String third;

    public Terceto(int numTerceto, String first, String second, String third) {
        this.numTerceto = numTerceto;
        this.first = first;
        this.second = second;
        this.third =  third;
    }

    public int getNumTerceto() {
        return numTerceto;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getThird() {
        return third;
    }

    public void addFirst(String first) {
        this.first = first;
    }

    public void addSecond(String second) {
        this.second = second;
    }

    public void addThird (String third) {
        this.third = third;
    }

}
