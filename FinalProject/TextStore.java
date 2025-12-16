import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextStore {
    private Vector<String> v = new Vector<String>();
    private String FILENAME = "words.txt";

    public TextStore()
    {
        try
        {
            File file = new File(FILENAME);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine())
            {
                String word = scanner.nextLine().trim();
                if(!word.isEmpty())
                {
                    v.add(word);
                }
            }
            scanner.close();
            if(v.isEmpty())
            {
                v.add("기본");
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("파일을 찾을 수 없음");
            v.add("오류상황");
        }
    }

    public String get()
    {
        if(v.isEmpty())
        {
            return "empty";
        }
        int index = (int)(Math.random() * v.size());
        return v.get(index);
    }
}
