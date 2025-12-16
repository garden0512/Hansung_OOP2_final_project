import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 점수 항목을 저장할 내부 클래스 (레코드와 유사)
class ScoreEntry {
    String difficulty;
    int score;
    String userName;

    public ScoreEntry(String difficulty, int score, String userName) {
        this.difficulty = difficulty;
        this.score = score;
        this.userName = userName;
    }

    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public String getUserName() { return userName; }
}

public class ScoreManager {
    private static final String FILENAME = "ranking_data.txt";

    public static void saveScore(String userName, String difficulty, int finalScore) {
        // ... (saveScore 메소드는 이전과 동일)
        String data = String.format("%s,%d,%s", difficulty, finalScore, userName);

        try (FileWriter fw = new FileWriter(FILENAME, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(data);

        } catch (IOException e) {
            System.err.println("점수 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 파일에서 모든 점수를 읽어와 난이도별로 그룹화하고,
     * 각 난이도 내에서 점수가 높은 순서로 정렬하여 반환합니다.
     * @return 난이도 문자열을 키로 하고, 해당 난이도의 정렬된 ScoreEntry 리스트를 값으로 하는 맵
     */
    public static Map<String, List<ScoreEntry>> loadScores() {
        // 난이도별 점수 리스트를 저장할 맵
        Map<String, List<ScoreEntry>> groupedScores = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        String difficulty = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        String userName = parts[2].trim();

                        ScoreEntry entry = new ScoreEntry(difficulty, score, userName);

                        // 난이도별로 그룹화
                        groupedScores.computeIfAbsent(difficulty, k -> new ArrayList<>()).add(entry);

                    } catch (NumberFormatException e) {
                        System.err.println("잘못된 점수 형식: " + line);
                    }
                }
            }
        } catch (IOException e) {
            // 파일이 없거나 읽을 수 없을 경우 빈 맵 반환 (오류로 처리하지 않음)
            System.out.println("랭킹 파일이 존재하지 않거나 읽을 수 없습니다. 새로운 파일이 생성됩니다.");
        }

        // 각 난이도 리스트를 점수가 높은 순서로 정렬 (내림차순)
        for (List<ScoreEntry> list : groupedScores.values()) {
            list.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        }

        return groupedScores;
    }
}