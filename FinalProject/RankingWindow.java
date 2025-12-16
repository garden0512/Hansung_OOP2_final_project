import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RankingWindow extends JFrame
{
    // 점수가 높은 순서로 정렬된 난이도 목록을 반환하는 Comparator
    private static final Comparator<String> DIFFICULTY_COMPARATOR = (d1, d2) -> {
        try {
            int lv1 = Integer.parseInt(d1.substring(3)); // "POP_" 제거
            int lv2 = Integer.parseInt(d2.substring(3));
            return Integer.compare(lv1, lv2);
        } catch (NumberFormatException e) {
            return d1.compareTo(d2); // 파싱 실패 시 문자열 비교
        }
    };

    public RankingWindow(int mainWindowWidth, int mainWindowHeight) {
        super("게임 랭킹");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫기
        int deviceWidth = (int) (mainWindowWidth * 0.52);       //순위창의 가로
        int deviceHeight = (int) (mainWindowHeight * 0.58);     //순위창의 세로
        int x = mainWindowWidth / 2 - deviceWidth / 2;
        int y = mainWindowHeight / 2 - deviceHeight / 2;
        this.setSize(deviceWidth, deviceHeight);
        this.setLocation(x, y);
        this.setLayout(new BorderLayout(10, 10));
        this.setUndecorated(true);

        // 1. 점수 데이터 로드
        Map<String, List<ScoreEntry>> groupedScores = ScoreManager.loadScores();

        // 2. JTable 데이터 생성
        JTable rankingTable = createRankingTable(groupedScores);

        // 3. UI 구성
        JLabel titleLabel = new JLabel("난이도별 최고 점수 (좌측일수록 쉬움)", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        this.add(titleLabel, BorderLayout.NORTH);

        // JTable을 JScrollPane에 넣어 스크롤 가능하게 만듭니다.
        this.add(new JScrollPane(rankingTable), BorderLayout.CENTER);

        // 4. 가시화
        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();
        this.setVisible(true);
    }

    private JTable createRankingTable(Map<String, List<ScoreEntry>> groupedScores) {

        // 난이도 목록을 인구수(난이도)가 쉬운 순서(POP_10, POP_50, POP_100...)로 정렬
        List<String> sortedDifficulties = groupedScores.keySet().stream()
                .sorted(DIFFICULTY_COMPARATOR)
                .collect(Collectors.toList());

        // 최대 순위 개수 (행 개수 결정). 가장 많은 점수가 있는 난이도의 개수를 따라갑니다.
        int maxRanks = groupedScores.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        // --- 1. 컬럼 헤더 생성 (난이도) ---
        // 첫 번째 컬럼은 "순위"
        List<String> headers = new ArrayList<>();
        headers.add("순위");
        headers.addAll(sortedDifficulties);

        // --- 2. 데이터 배열 생성 (행렬) ---
        // (행: 순위, 열: 난이도)
        Object[][] tableData = new Object[maxRanks][headers.size()];

        // 테이블 데이터 채우기
        for (int rank = 0; rank < maxRanks; rank++) {
            // 첫 번째 열은 순위 번호 (1위부터 시작)
            tableData[rank][0] = (rank + 1) + "위";

            for (int col = 0; col < sortedDifficulties.size(); col++) {
                String difficulty = sortedDifficulties.get(col);
                List<ScoreEntry> scores = groupedScores.get(difficulty);

                if (rank < scores.size()) {
                    ScoreEntry entry = scores.get(rank);
                    // 셀의 내용은 "점수 (사용자 이름)" 형식으로 표시
                    tableData[rank][col + 1] = String.format("%d (%s)", entry.getScore(), entry.getUserName());
                } else {
                    // 해당 순위에 데이터가 없을 경우
                    tableData[rank][col + 1] = "-";
                }
            }
        }

        // JTable 생성
        DefaultTableModel model = new DefaultTableModel(tableData, headers.toArray());
        JTable table = new JTable(model);

        // 테이블 스타일 설정
        table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        return table;
    }
}