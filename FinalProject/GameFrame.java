import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.util.concurrent.atomic.AtomicInteger;       //멀티쓰레드 환경에서 안전하게 변수를 바꾸기 위해 추가

public class GameFrame extends JFrame{
    private int deviceWidth;
    private int deviceHeight;
    private int campaignLevel;
    private AtomicInteger coalAmount;       //쓰레드에서 바뀌는 변수
    private AtomicInteger foodAmount;       //쓰레드에서 바뀌는 변수
    private int population;
    private int houseTemperature;
    private int currentTemperature;
    private int topUIHeight;
    private TextStore textStore = new TextStore();
    private GamePanel gamePanel;
    //각 자원 및 수치 라벨
    private JLabel coalLabel;
    private JLabel foodLabel;
    private JLabel populationLabel;
    private JLabel houseTemperatureLabel;
    private JLabel currentTemperatureStringLabel;
    private JLabel currentTemperatureLabel;
    private Timer gameTimer;        //전체 게임의 제한 시간 카운트용 타이머
    private Timer temperatureTimer;     //온도 하락을 위한 타이머
    private Timer scoreTimer;       //점수 계산을 위한 타이머
    private long startTime;
    private int gameDuration = 120 * 1000;      // 2분
    private int temperatureDropInternal = 30 * 1000;
    private int finalDropTime = 10 * 1000;
    private boolean isGameRunning = true;       //게임 실행 상태 플래그
    private double score = 0.0;      //플레이어 점수
    private AudioPlayer gameAudio;
    private String gameMusicPath = "sounds/game.wav";
    private int[] scoreRates = {1, 2, 3, 4, 6};      //초당 점수
    private int[] timeIntervals = {30000, 60000, 90000, 110000, 120000};

    //인게임 UI 이미지 로딩
    ImageIcon topUI = new ImageIcon("images/InGameUI.png");

    public GameFrame(int coalAmount, int foodAmount, int population, int houseTemperature, int currentTemperature, int campaignLevel)
    {
        super("게임화면");

        //기본 프레임
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //프레임을 닫으면 메모리에서 제거되도록 설정하는 기능
        this.setUndecorated(true);      // 작업줄 또는 창 테두리 등이 안보이도록 하는 기능
        this.setLayout(null);       // 비율 기반 배치를 위한 null 레이아웃 설정

        //풀스크린으로 프레임이 열리도록 설정
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if(graphicsDevice.isFullScreenSupported())      //만약 이 디바이스가 풀스크린을 지원한다면
        {
            graphicsDevice.setFullScreenWindow(this);   // 이 프레임이 풀스크린으로 열리도록 함
            this.deviceWidth = graphicsDevice.getDisplayMode().getWidth();      // 현재 프레임의 가로값
            this.deviceHeight = graphicsDevice.getDisplayMode().getHeight();    // 현재 프레임의 세로값

            //16:9비율로 화면 크기 재계산
            Dimension scaleDim = getScaledDimensions16x9(this.deviceWidth, this.deviceHeight);
            int finalWidth = scaleDim.width;
            int finalHeight = scaleDim.height;

            //프레임 크기 및 위치 설정
            this.setSize(finalWidth, finalHeight);
            int x = (this.deviceWidth - finalWidth) / 2;
            int y = (this.deviceHeight - finalHeight) / 2;
            this.setLocation(x, y);

            //변수 값 바꿔주기
            this.deviceWidth = finalWidth;
            this.deviceHeight = finalHeight;
        }
        else    // 풀스크린을 지원하지 않는다면
        {
            System.out.println("풀스크린을 지원하지 않는 기기입니다.");     // 경고 안내문구 출력
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);       // 가로, 세로 전부 최대치로 열리도록 함
            this.deviceWidth = graphicsDevice.getDisplayMode().getWidth();      // 현재 프레임의 가로값
            this.deviceHeight = graphicsDevice.getDisplayMode().getHeight();    // 현재 프레임의 세로값

            Dimension scaledDim = getScaledDimensions16x9(this.deviceWidth, this.deviceHeight);
            this.deviceWidth = scaledDim.width;
            this.deviceHeight = scaledDim.height;
            this.setSize(this.deviceWidth, this.deviceHeight);
        }

        //배경색 설정
        try
        {
            Color backgroundColor = Color.decode("#16191B");
            this.getContentPane().setBackground(backgroundColor);
        }
        catch(NumberFormatException e)
        {
            System.err.println("유효하지 않은 색상코드입니다.");
            this.getContentPane().setBackground(Color.BLACK);
        }

        //받아온 매개변수 값을 클래스 내부 변수에 저장
        this.coalAmount = new AtomicInteger(coalAmount);       //석탄 양
        this.foodAmount = new AtomicInteger(foodAmount);       //식량 양
        this.population = population;       //인구수
        this.houseTemperature = houseTemperature;       //거주지 온도
        this.currentTemperature = currentTemperature;       //현재 온도
        this.campaignLevel = campaignLevel;

        this.gamePanel = new GamePanel(textStore, this);

        DisplayTopUIPanel();        //상단 UI 그리기
        addGamePanel();     //게임 화면 그리기
        InitializeGameAudio();

        //가시성 설정
        this.setVisible(true);      // 프레임이 보이도록 설정
        StartGameTimer();       //게임 시작과 동시에 타이머 시작
        StartTemperatureTimer();
        StartScoreTimer();
        this.gamePanel.start();  //게임이 시작하자마자 실행(단어 생성 메소드)
    }

    private void StartGameTimer()
    {
        this.startTime = System.currentTimeMillis();
        gameTimer = new Timer(gameDuration, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EndGame();
                gameTimer.stop();
            }
        });
        gameTimer.setRepeats(false);
        gameTimer.start();
    }

    private void StartScoreTimer()
    {
        // 100ms마다 실행하여 점수 누적
        scoreTimer = new Timer(100, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!isGameRunning) {
                    scoreTimer.stop();
                    return;
                }

                // 1. 현재 플레이 시간 계산
                long elapsedTime = System.currentTimeMillis() - startTime;

                if (elapsedTime > gameDuration) {
                    elapsedTime = gameDuration; // 2분을 초과하지 않도록 제한
                }

                // 2. 현재 시간 구간에 해당하는 초당 점수율을 찾습니다.
                int currentRate = 0;
                for (int i = 0; i < timeIntervals.length; i++)
                {
                    if (elapsedTime <= timeIntervals[i])
                    {
                        currentRate = scoreRates[i];
                        break;
                    }
                }
                double scoreToAdd = currentRate * 0.2;
                score += scoreToAdd;
            }
        });
        scoreTimer.start();
    }

    private void InitializeGameAudio()
    {
        this.gameAudio = new AudioPlayer(gameMusicPath);
        this.gameAudio.setVolume(0.5);
        this.gameAudio.play(true); // 게임 BGM 무한 반복 재생 시작
    }

    private void StartTemperatureTimer()
    {
        temperatureTimer = new Timer(temperatureDropInternal, new ActionListener()
        {
            private long startTime = System.currentTimeMillis();
            private long endTime = startTime + gameDuration;
            @Override
            public void actionPerformed(ActionEvent r)
            {
                long currentTime = System.currentTimeMillis();
                long remainingTime = endTime - currentTime;
                int dropAmount = 10;
                DropTemperature(dropAmount);
            }
        });
        temperatureTimer.start();
        int finalDropDelay = gameDuration - finalDropTime;
        if(finalDropDelay > 0)
        {
            Timer finalDropTimer = new Timer(finalDropDelay, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(isGameRunning)
                    {
                        DropTemperature(20);
                    }
                }
            });
            finalDropTimer.setRepeats(false);
            finalDropTimer.start();
        }
    }

    private void DropTemperature(int amount)
    {
        this.currentTemperature -= amount;
        UpdateStatsUI();
    }

    public void EndGame()
    {
        if(isGameRunning)
        {
            isGameRunning = false;
            if (temperatureTimer != null)
            {
                temperatureTimer.stop();
            }
            if (gameAudio != null)
            {
                gameAudio.stop();
            }
            if (scoreTimer != null)
            {
                scoreTimer.stop();
            }
            gamePanel.StopAllWords();
            ShowResultDialogue();
        }
    }

    //GamePanel이 자원 상태를 업데이트 하도록 공개시킨 메소드--------------------------
    public int getPopulation()
    {
        return this.population;
    }

    public int getHouseTemperature()
    {
        return this.houseTemperature;
    }

    public int getCurrentTemperature()
    {
        return this.currentTemperature;
    }

    public void AddCoal(int amount)
    {
        this.coalAmount.addAndGet(amount);
        UpdateStatsUI();
    }

    public void AddFood(int amount)
    {
        this.foodAmount.addAndGet(amount);
        UpdateStatsUI();
    }

    public void ConsumeResources()      //현실시간으로 2초동안 소비되는 자원의 양을 결정하고 호출
    {
        double coalConsumptionRate = ((double)population / 100.0) * ((double)currentTemperature / -10.0) * (double)campaignLevel;       //석탄 소비량
        int coalConsumed = (int) Math.max(1, Math.round(coalConsumptionRate));      //최소 소비량을 1로 보장
        int foodConsumed = Math.max(1, population / 10);    //식량 소비량
        //자원 소비
        this.coalAmount.updateAndGet(current -> Math.max(0, current - coalConsumed));
        this.foodAmount.updateAndGet(current -> Math.max(0, current - foodConsumed));
        UpdateStatsUI();
        if (this.coalAmount.get() <= 0)
        {
            EndGame();
        }
        else if (this.foodAmount.get() <= 0)
        {
            EndGame();
        }
    }

    public void UpdateStatsUI()     //UI 업데이트 전용 메소드
    {
        SwingUtilities.invokeLater(() ->
        {
           if(coalLabel != null)
           {
               this.coalLabel.setText("석탄 : " + String.format("%04d", this.coalAmount.get()));
           }
           if(foodLabel != null)
           {
               this.foodLabel.setText("식량 :  " + String.format("%04d", this.foodAmount.get()));
           }
           if(populationLabel != null)
           {
               this.populationLabel.setText("인구 : " + String.format("%04d", this.population));
           }
           if(houseTemperatureLabel != null)
           {
               this.houseTemperatureLabel.setText("집 온도 :  " + String.format("%04d", this.houseTemperature));
           }
           if(currentTemperatureLabel != null)
           {
               this.currentTemperatureLabel.setText(this.currentTemperature + "℃");
           }
           if(currentTemperatureStringLabel != null)
           {
               this.currentTemperatureStringLabel.setText("현재온도");
           }
        });
    }

    //---------------------------------------------------

    //글씨 크기 계산하는 메소드
    private int CalculateFontSize(double fontSizePercent)
    {
        return (int) (this.deviceHeight * fontSizePercent / 100.0);
    }

    private void addGamePanel() {
        // GamePanel의 위치와 크기를 절대 위치로 설정합니다.
        int panelY = this.topUIHeight;      //상단 UI 높이부터 시작
        int panelWidth = this.deviceWidth;      //너비는 화면 전체
        int panelHeight = this.deviceHeight - panelY;       //높이는 UI부분을 제외한 나머지
        getContentPane().add(gamePanel); // 제약 조건
        gamePanel.setBounds(0, panelY, panelWidth, panelHeight);

        // 폰트 크기 계산 및 GamePanel 초기화
        int preferredFontSize = CalculateFontSize(2);
        gamePanel.initializeUI(preferredFontSize);

        // GamePanel 내의 컴포넌트들이 크기가 변경된 GamePanel에 맞춰 재배치되도록 요청 (선택적)
        gamePanel.revalidate();
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

    private static Dimension getScaledDimensions16x9(int screenWidth, int screenHeight)
    {
        double targetRatio = 16.0 / 9.0;
        int scaledWidth = screenWidth;
        int scaledHeight = (int) (screenWidth / targetRatio);

        if (scaledHeight > screenHeight) {
            scaledHeight = screenHeight;
            scaledWidth = (int) (screenHeight * targetRatio);
        }
        return new Dimension(scaledWidth, scaledHeight);
    }

    private void DisplayTopUIPanel()
    {
        int elementWidthPercent = 65;      //화면 가로에서 차지할 비율
        int elementHeightPercent = 16;     //화면 세로에서 차지할 비율
        int yPositionPercent = 0;   //상단에서 떨어질 비율
        int elementWidth = (int) ((deviceWidth * elementWidthPercent) / 100.0);     //구현될 UI의 가로길이 계산
        int elementHeight = (int)((deviceHeight * elementHeightPercent) / 100.0);       //구현될 UI의 세로길이 계산
        int positionY = (int)((deviceHeight * yPositionPercent) / 100.0);       //Y축 좌표의 상대적 위치 계산
        int positionX = (deviceWidth / 2) - (elementWidth / 2);     //X축 위치의 상대적 위치 계산

        this.topUIHeight = positionY + elementHeight;

        ImageIcon resizedIcon = ResizeIcon(topUI, elementWidth, elementHeight);     //이미지 크기 재조정
        JLabel uiLabel = new JLabel();
        if(resizedIcon != null)
        {
            uiLabel.setIcon(resizedIcon);
            uiLabel.setBounds(positionX, positionY, elementWidth, elementHeight);
        }
        else
        {
            uiLabel.setBounds(positionX, positionY, elementWidth, elementHeight);
            uiLabel.setOpaque(true);
            uiLabel.setBackground(Color.GRAY);
        }

        this.add(uiLabel);      //프레임에 추가
        DisplayStatsOnUI(positionX, positionY, elementWidth, elementHeight);
    }

    private void DisplayStatsOnUI(int uiX, int uiY, int uiWidth, int uiHeight)      //레이블 생성 및 멤버변수에 저장
    {
        int subFontSize = CalculateFontSize(2);     //서브 글씨크기
        int mainFontSize = CalculateFontSize(6);    //메인 글씨크기
        Font subStatFont = new Font(Font.SANS_SERIF, Font.BOLD, subFontSize);
        Font mainStatFont = new Font(Font.SANS_SERIF, Font.BOLD, mainFontSize);

        Color textColor = Color.WHITE;
        double[][] relativePositions =
                {
                        {6.5, 4.0, 15.0, 0.0, 0.0},     //석탄
                        {26.0, 4.0, 15.0, 0.0, 0.0},     //식량
                        {63.0, 4.0, 15.0, 0.0, 0.0},     //인구
                        {82.0, 4.0, 20.0, 0.0, 0.0},     //집 온도
                        {45.0, 4.0, 12.0, 0.0, 0.0},     //현재온도 글씨
                        {41.0, 33.0, 16.0, 1.0, 1.0},     //현재 온도 숫자로
                };
        for(int i = 0; i < relativePositions.length; i++)
        {
            JLabel newLabel;
            int alignment = (int)relativePositions[i][4];
            if(i==5)
            {
                newLabel = new JLabel();
                newLabel.setFont(mainStatFont);
                currentTemperatureLabel = newLabel;
            }
            else
            {
                newLabel = new JLabel();
                newLabel.setFont(subStatFont);
                switch(i)
                {
                    case 0:
                        coalLabel = newLabel;
                        break;
                    case 1:
                        foodLabel = newLabel;
                        break;
                    case 2:
                        populationLabel = newLabel;
                        break;
                    case 3:
                        houseTemperatureLabel = newLabel;
                        break;
                    case 4:
                        currentTemperatureStringLabel = newLabel;
                        break;
                }
            }

            int labelX = uiX + (int) (uiWidth * relativePositions[i][0] / 100.0);
            int labelY = uiY + (int) (uiHeight * relativePositions[i][1] / 100.0);
            int labelW = (int) (uiWidth * relativePositions[i][2] / 100.0);
            int labelH = (int) (uiHeight * 35 / 100.0);

            newLabel.setBounds(labelX, labelY, labelW, labelH);
            newLabel.setForeground(textColor);
            //폰트 정렬 방식
            if (alignment == 1)     //중앙정렬
            {
                newLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
            else if (alignment == 2)        //오른쪽 정렬
            {
                newLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            }
            else        //왼쪽정렬
            {
                newLabel.setHorizontalAlignment(SwingConstants.LEFT);
            }

            this.add(newLabel);
        }
        UpdateStatsUI();
    }

    private void ShowResultDialogue()
    {
        JDialog resultDialog = new JDialog(this, "게임 종료", true);
        resultDialog.setLayout(new BorderLayout(15, 15));
        resultDialog.setSize(1000, 300);
        resultDialog.setResizable(false);
        resultDialog.setLocationRelativeTo(this);
        //상단 점수 표시
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(new Color(0x16191B));
        JLabel resultLabel = new JLabel("게임 종료", SwingConstants.CENTER);
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        scorePanel.add(resultLabel);
        JLabel finalScoreLabel = new JLabel("최종 점수 : " + (int)Math.ceil(this.score) + "점", SwingConstants.CENTER);
        finalScoreLabel.setForeground(Color.YELLOW);
        finalScoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
        //점수와 플레이어 이름 입력창 중앙정렬
        JPanel contentPanel = new JPanel(new GridLayout());
        contentPanel.setBackground(new Color(0x16191B));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10,10,10,10);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //플레이어 이름 입력
        JLabel namePrompt = new JLabel("이름을 입력하세요 : ", SwingConstants.CENTER);
        namePrompt.setForeground(Color.WHITE);
        JTextField nameTextField = new JTextField(15);
        nameTextField.setPreferredSize(new Dimension(200, 35));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        contentPanel.add(finalScoreLabel, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        contentPanel.add(namePrompt, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        contentPanel.add(nameTextField, gridBagConstraints);
        //하단 저장 및 종료 버튼 추가
        JButton saveAndExitButton = new JButton("저장 및 종료");
        saveAndExitButton.setBackground(new Color(0x4CAF50));
        saveAndExitButton.setForeground(Color.WHITE);
        saveAndExitButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        saveAndExitButton.setFocusPainted(false);
        saveAndExitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String userName = nameTextField.getText().trim();
                if(userName.isEmpty())
                {
                    JOptionPane.showMessageDialog(resultDialog, "플레이어 이름을 입력해야 저장됩니다.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String difficulty = "LV_" + campaignLevel;   //난이도 결정
                ScoreManager.saveScore(userName, difficulty, (int)Math.ceil(score));
                resultDialog.dispose();
                dispose();
                new MainWindow();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x16191B));
        buttonPanel.add(saveAndExitButton);
        resultDialog.add(scorePanel, BorderLayout.NORTH);
        resultDialog.add(contentPanel, BorderLayout.CENTER);
        resultDialog.add(buttonPanel, BorderLayout.SOUTH);
        resultDialog.setVisible(true);
    }
}
