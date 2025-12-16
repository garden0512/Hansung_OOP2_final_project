import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class BackgroundPanel extends JPanel
{
    private Image originalImage;

    public BackgroundPanel(ImageIcon icon, int width, int height)
    {
        this.originalImage = icon.getImage();
        this.setLayout(null);       //내부 JLabel을 절대 좌표로 배치
        this.setPreferredSize(new Dimension(width, height));
    }
    @Override
    protected void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        if(originalImage != null)
        {
            graphics.drawImage(originalImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class RankWindow extends JFrame
{
    //4K 모니터를 기준으로 삼는 동적 UI의 크기
    private int baseWidth = 3840;       //4K모니터 가로
    private int baseHeight = 2160;      //4K모니터 세로
    private int windowWidth = 2000;     //띄우고싶은 창의 4K기준 가로
    private int windowHeight = 1250;        //띄우고싶은 창의 4K기준 세로
    private int matrixStartX = 250;     //띄울 창에 가상으로 설정한 행렬의 왼쪽 상단 X좌표
    private int matrixStartY = 350;     //띄울 창에 가상으로 설정한 행렬의 왼쪽 상단 Y좌표
    private int cellWidth = 300;        //셀 하나의 가로길이
    private int cellHeight = 50;        //셀 하나의 세로길이
    private int maxRanks = 16;      //표시할 최대 순위
    private int maxDifficulties = 5;        //표시할 최대 난이도
    private int fontSizeBase = 20;      //기준 폰트 사이즈
    private int buttonStartX = 30;      //4K 기준으로 버튼의 X시작
    private int buttonStartY = 30;      //4K 기준으로 버튼의 Y시작
    private int buttonWidth = 100;      //버튼의 가로길이
    private int buttonHeight = 100;     //버튼의 세로길이
    private JButton backButton;
    private BackgroundPanel backgroundPanel;
    private Map<String, List<ScoreEntry>> groupedScores;
    private List<String> sortedDifficulties;
    ImageIcon backButtonDefaultIcon = new ImageIcon("images/back_default.png");
    ImageIcon backButtonRollOverIcon = new ImageIcon("images/back_hover.png");
    ImageIcon backButtonPressedIcon = new ImageIcon("images/back_hover.png");
    private Comparator<String> difficultyComparator =(d1, d2)->
    {
        try
        {
            int lv1 = Integer.parseInt(d1.substring(3));    //LV_를 제거
            int lv2 = Integer.parseInt(d2.substring(3));    //LV_를 제거
            return Integer.compare(lv1, lv2);
        }
        catch(Exception e)
        {
            return d1.compareTo(d2);    //파싱 실패 시 문자열로 비교
        }
    };

    public RankWindow(int mainWindowWidth, int mainWindowHeight)
    {
        super("순위창");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);     //전체 프로그램 종료 방지
        double screenWidthFactor = (double) mainWindowWidth / baseWidth;      //현재 열린 창의 가로비율
        double screenHeightFactor = (double)mainWindowHeight / baseHeight;    //현재 열린 창의 세로비율
        double scaleFactor = Math.min(screenWidthFactor, screenHeightFactor);   //비율 유지를 위한 최소 팩터 사용
        //스케일링
        int frameWidth = scale(windowWidth, scaleFactor);
        int frameHeight = scale(windowHeight, scaleFactor);
        //창 위치 결정
        int x = mainWindowWidth / 2 - frameWidth / 2;        //순위창의 X좌표
        int y = mainWindowHeight / 2 - frameHeight / 2;      //순위창의 Y좌표
        this.setSize(frameWidth, frameHeight);        //순위창의 크기 설정
        this.setLocation(x,y);      //화면 상 위치
        this.setLayout(new BorderLayout(0, 0));     //각 요소의 마진
        this.setUndecorated(true);      //타이틀이나 최소화/최대화/끄기 버튼 등을 안 보이도록 하는 메소드
        //점수 데이터 로드
        this.groupedScores = ScoreManager.loadScores();
        this.sortedDifficulties = groupedScores
                .keySet()
                .stream()
                .sorted(difficultyComparator)
                .collect(Collectors.toList());

        //backgroundPanel 생성
        ImageIcon backgroundIcon = new ImageIcon("images/rank_bg.png");
        this.backgroundPanel = new BackgroundPanel(backgroundIcon, frameWidth, frameHeight);
        this.backgroundPanel.setBackground(Color.decode("#0F1316"));
        this.setContentPane(this.backgroundPanel);
        //버튼 생성 및 배치
        int scaledButtonX = scale(buttonStartX, scaleFactor);
        int scaledButtonY = scale(buttonStartY, scaleFactor);
        int scaledButtonWidth = scale(buttonWidth, scaleFactor);
        int scaledButtonHeight = scale(buttonHeight, scaleFactor);
        this.backButton = new JButton();
        ImageIcon resizedDefaultIcon = ResizeIcon(backButtonDefaultIcon, scaledButtonWidth, scaledButtonHeight);
        ImageIcon resizedRollOverIcon = ResizeIcon(backButtonRollOverIcon, scaledButtonWidth, scaledButtonHeight);
        ImageIcon resizedPressedIcon = ResizeIcon(backButtonPressedIcon, scaledButtonWidth, scaledButtonHeight);
        this.backButton.setIcon(resizedDefaultIcon);
        this.backButton.setRolloverIcon(resizedRollOverIcon);
        this.backButton.setPressedIcon(resizedPressedIcon);
        this.backButton.setBounds(scaledButtonX, scaledButtonY, scaledButtonWidth, scaledButtonHeight);
        this.backgroundPanel.add(this.backButton);
        this.backButton.setContentAreaFilled(false);
        this.backButton.setBorderPainted(false);
        this.backButton.setFocusPainted(false);
        this.backButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GoBack();
            }
        });

        displayScores(this.backgroundPanel, this.groupedScores, this.sortedDifficulties, scaleFactor);

        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();
        this.setVisible(true);      //가시성 설정
    }

    private int scale(int baseValue, double scaleFactor)
    {
        return (int) (baseValue * scaleFactor);
    }

    private void displayScores(JPanel panel, Map<String, List<ScoreEntry>> groupedScores, List<String> sortedDifficulties, double scaleFactor)
    {
        String[] fixedDifficulties = {"LV_1", "LV_2", "LV_3", "LV_4", "LV_5"};
//        List<String> displayDifficulties = sortedDifficulties.stream()
//                .limit(maxDifficulties)
//                .collect(Collectors.toList());
        //스캐일링 된 기본값 계산
        int startX = scale(matrixStartX, scaleFactor);
        int startY = scale(matrixStartY, scaleFactor);
        int inCellWidth = scale(cellWidth, scaleFactor);
        int inCellHeight = scale(cellHeight, scaleFactor);
        int fontSize = scale(fontSizeBase, scaleFactor);

        Font scoreFont = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);

        //난이도 열 반복
        for(int col = 0; col < maxDifficulties; col++)
        {
            String difficulty = fixedDifficulties[col];
            List<ScoreEntry> scores = groupedScores.getOrDefault(difficulty, new ArrayList<>());
            int xPos = startX + (col * inCellWidth);      //X좌표 계산
            for(int rank = 0; rank < maxRanks; rank++)  //순위 행 반복
            {
                int yPos = startY + (rank * inCellHeight);      //현재 행의 Y좌표 계산
                String scoreText;
                if(rank < scores.size())
                {
                    ScoreEntry entry = scores.get(rank);
                    scoreText = String.format("%d (%s)", entry.getScore(), entry.getUserName());
                }
                else
                {
                    scoreText = "";
                }
                //라벨 생성 및 배치
                JLabel scoreLabel = new JLabel(scoreText, SwingConstants.CENTER);
                scoreLabel.setFont(scoreFont);
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setBounds(xPos, yPos, inCellWidth, inCellHeight);
                panel.add(scoreLabel);

            }
        }
        panel.revalidate();
        panel.repaint();
    }

    //이미지를 버튼 크기에 맞게 비율을 유지하며 조정하는 메소드
    private ImageIcon ResizeIcon(ImageIcon originalIcon, int targetWidth, int targetHeight)
    {
        if(originalIcon == null || originalIcon.getImage() == null)     //이미지가 없다면
        {
            return null;        // null반환
        }
        Image originalImage = originalIcon.getImage();      //원본 이미지 정보
        int originalWidth = originalImage.getWidth(null);       //원본 이미지의 가로
        int originalHeight = originalImage.getHeight(null);     //원본 이미지의 세로

        if(originalWidth <= 0 || originalHeight <= 0 || targetWidth <= 0 || targetHeight <= 0)      //크기 계산이 불가능한 상태라면
        {
            return originalIcon;    // 원본 반환
        }

        //버튼 크기와 이미지 크기의 비율 계산
        double widthRatio = (double)targetWidth / (double)originalWidth;
        double heightRatio = (double)targetHeight / (double)originalHeight;

        //비율 유지를 위해 더 작은 비율 선택 후 이미지가 찌그러지지 않도록 조정
        double ratio = Math.min(widthRatio, heightRatio);
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        //크기가 조절된 이미지 생성
        Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void GoBack()
    {
        dispose();
    }
}
