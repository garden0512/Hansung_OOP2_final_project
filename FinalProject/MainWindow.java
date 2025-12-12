import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class MainWindow extends JFrame{
    private JButton exitButton;
    private JButton productionListButton;
    private JButton steamPageButton;
    private JButton studioNewsButton;
    private JButton settingButton;
    private JButton loadGameButton;
    private JButton infinityModeButton;
    private JButton campaignButton;
    private JButton continueGameButton;
    private int deviceWidth;
    private int deviceHeight;

    //기본 이미지 로딩
    ImageIcon continueDefaultIcon = new ImageIcon("images/continue_default.png");
    ImageIcon campaignDefaultIcon = new ImageIcon("images/campaign_default.png");
    ImageIcon infinityModeDefaultIcon = new ImageIcon("images/infinity_default.png");

    //커서 호버링 이미지 로딩
    ImageIcon continueRollOverIcon = new ImageIcon("images/continue_hover.png");
    ImageIcon campaignRollOverIcon = new ImageIcon("images/campaign_hover.png");
    ImageIcon infinityModeRollOverIcon = new ImageIcon("images/infinity_hover.png");

    //클릭 이미지 로딩
    ImageIcon continuePressedIcon = new ImageIcon("images/continue_hover.png");
    ImageIcon campaignPressedIcon = new ImageIcon("images/campaign_hover.png");
    ImageIcon infinityModePressedIcon = new ImageIcon("images/infinity_hover.png");

    //게임을 플레이 할 수 있도록 하는 객체 생성자 메소드
    public MainWindow()
    {
        super("메인화면");      // 창 타이틀

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
        }
        else    // 풀스크린을 지원하지 않는다면
        {
            System.out.println("풀스크린을 지원하지 않는 기기입니다.");     // 경고 안내문구 출력
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);       // 가로, 세로 전부 최대치로 열리도록 함
            this.deviceWidth = graphicsDevice.getDisplayMode().getWidth();      // 현재 프레임의 가로값
            this.deviceHeight = graphicsDevice.getDisplayMode().getHeight();    // 현재 프레임의 세로값
        }

        //기본 UI 생성
        CreateButtons();    // 버튼들 만드는 메소드

        //가시성 설정
        this.setVisible(true);      // 프레임이 보이도록 설정
    }

    //메인화면 버튼들을 생성하는 메소드
    private void CreateButtons()
    {
        //버튼 생성
        this.exitButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 88, 16, 4);
        this.productionListButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 82, 16, 4);
        this.steamPageButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 76, 16, 4);
        this.studioNewsButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 70, 16, 4);
        this.settingButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 54, 16, 4);
        this.loadGameButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 48, 16, 4);
        this.infinityModeButton = CreateRelativeButton(infinityModeDefaultIcon, deviceWidth, deviceHeight, 15, 41, 16, 5);
        this.campaignButton = CreateRelativeButton(campaignDefaultIcon, deviceWidth, deviceHeight, 15, 35, 16, 4);
        this.continueGameButton = CreateRelativeButton(continueDefaultIcon, deviceWidth, deviceHeight, 15, 26, 16, 4);

        //버튼 기능 오버라이딩
        this.exitButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Exit();  // 게임 종료 메소드 호출
            }
        });
        this.productionListButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowCrewList();  // 게임 제작진 목록 표시창으로 이동
            }
        });
        this.steamPageButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OpenSteamPage();  // 스팀 페이지 열기
            }
        });
        this.studioNewsButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowStudioNewsPage();  // 게임사 소식 보러가기
            }
        });
        this.settingButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OpenSetting();  // 설정 페이지 열기
            }
        });
        this.loadGameButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OpenGameDataFile();  // 저장된 게임 불러오기 페이지 열기
            }
        });
        this.infinityModeButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OpenInfinityModeSetting();      // 무한모드 게임 설정열기
            }
        });
        this.campaignButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OpenCampaignModeSetting();      // 캠페인 모드 설정 열기
            }
        });
        this.continueGameButton.addActionListener(new ActionListener()      // 해당 버튼을 누를 시 발생할 이벤트 설정
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ContinueRecentGame();       // 최근 진행한 게임 이어하기
            }
        });

        //버튼 추가
        this.add(exitButton);
        this.add(productionListButton);
        this.add(steamPageButton);
        this.add(studioNewsButton);
        this.add(settingButton);
        this.add(loadGameButton);
        this.add(infinityModeButton);
        this.add(campaignButton);
        this.add(continueGameButton);

        //버튼 내부 채우지 않도록 하는 메소드
        continueGameButton.setContentAreaFilled(false);
        campaignButton.setContentAreaFilled(false);
        infinityModeButton.setContentAreaFilled(false);

        //외곽선 삭제
        continueGameButton.setBorderPainted(false);
        campaignButton.setBorderPainted(false);
        infinityModeButton.setBorderPainted(false);

        //버튼 선택 시 생성되는 얇은 선 삭제
        continueGameButton.setFocusPainted(false);
        campaignButton.setFocusPainted(false);
        infinityModeButton.setFocusPainted(false);

        //버튼 호버링 상태 설정
        continueGameButton.setRolloverIcon(continueRollOverIcon);
        campaignButton.setRolloverIcon(campaignRollOverIcon);
        infinityModeButton.setRolloverIcon(infinityModeRollOverIcon);

        //버튼 클릭 상태 설정
        continueGameButton.setPressedIcon(continuePressedIcon);
        campaignButton.setPressedIcon(campaignPressedIcon);
        infinityModeButton.setPressedIcon(infinityModePressedIcon);
    }

    //요소들을 화면에 맞게 동적으로 비율을 정해서 상대적으로 배치
    private JButton CreateRelativeButton(ImageIcon buttonText, int width, int height, int xPositionPercent, int yPositionPercent, int elementWidthPercent, int elementHeightPercent)
    {
        int positionX = (int)((width * xPositionPercent) / 100.0);       // 현재 화면을 기준으로 계산된 버튼의 상대적 가로 위치
        int positionY = (int)((height * yPositionPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 세로 위치
        int elementWidth = (int) ((width * elementWidthPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 가로값
        int elementHeight = (int)((height * elementHeightPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 세로값
        JButton newButton = new JButton(buttonText);        // 버튼 컴포넌트 생성
        newButton.setBounds(positionX, positionY, elementWidth, elementHeight);     // 계산된 값들을 이용해 상대위치 / 크기로 버튼 생성
        return newButton;       // 생성된 버튼 반환
    }

    //버튼 기능 메소드들
    private void ContinueRecentGame()
    {
        //추후 추가예정
    }

    private void OpenCampaignModeSetting()
    {
        //추후 추가예정
    }

    private void OpenInfinityModeSetting()
    {
        //추후 추가예정
    }

    private void OpenGameDataFile()
    {
        //추후 추가예정
    }

    private void OpenSetting()
    {
        //추후 추가예정
    }

    private void ShowStudioNewsPage()
    {
        //추후 추가예정
    }

    private void OpenSteamPage()
    {
        //추후 추가예정
    }

    private void ShowCrewList()
    {
        //추후 추가예정
    }

    private void Exit()
    {
        System.exit(0);
    }

    // 게임을 실행하도록 해주는 실행 메소드
    public static void main(String[] args)
    {
        new MainWindow();       // 새 객체 생성
    }
}
