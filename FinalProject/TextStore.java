import java.util.Vector;

public class TextStore {
    private Vector<String> v = new Vector<String>();

    public TextStore()
    {
        v.add("1");
        v.add("2");
        v.add("3");
        v.add("4");
        v.add("5");
        v.add("6");
        v.add("7");
        v.add("8");
        v.add("9");
        v.add("10");
    }

    public String get()
    {
        int index = (int)(Math.random() * v.size());
        return v.get(index);
    }
}
