import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class CampaignSelectWindow extends JFrame {
    private int deviceWidth;
    private int deviceHeight;
    private JToggleButton lv1Button;
    private JToggleButton lv2Button;
    private JToggleButton lv3Button;
    private JToggleButton lv4Button;
    private JToggleButton lv5Button;
    private JButton backButton;
    private JButton campaignLoadButton;
    private JButton newCampaignButton;

    //기본 이미지 로딩
    ImageIcon lv1DefaultIcon = new ImageIcon("images/lv1_default.png");
    ImageIcon lv2DefaultIcon = new ImageIcon("images/lv2_default.png");
    ImageIcon lv3DefaultIcon = new ImageIcon("images/lv3_default.png");
    ImageIcon lv4DefaultIcon = new ImageIcon("images/lv4_default.png");
    ImageIcon lv5DefaultIcon = new ImageIcon("images/lv5_default.png");
    ImageIcon backDefaultIcon = new ImageIcon("images/back_default.png");
    ImageIcon campaignLoadDefaultIcon = new ImageIcon("images/campaign_load_default.png");
    ImageIcon newCampaignDefaultIcon = new ImageIcon("images/new_default.png");

    //선택된 이미지 로딩
    ImageIcon lv1SelectedIcon = new ImageIcon("images/lv1_selected.png");
    ImageIcon lv2SelectedIcon = new ImageIcon("images/lv2_selected.png");
    ImageIcon lv3SelectedIcon = new ImageIcon("images/lv3_selected.png");
    ImageIcon lv4SelectedIcon = new ImageIcon("images/lv4_selected.png");
    ImageIcon lv5SelectedIcon = new ImageIcon("images/lv5_selected.png");

    //호버링 이미지 로딩
    ImageIcon backRollOverIcon = new ImageIcon("images/back_hover.png");
    ImageIcon campaignLoadRollOverIcon = new ImageIcon("images/campaign_load_hover.png");
    ImageIcon newCampaignRollOverIcon = new ImageIcon("images/new_hover.png");

    //클릭 이미지 로딩
    ImageIcon backPressedIcon = new ImageIcon("images/back_hover.png");
    ImageIcon campaignLoadPressedIcon = new ImageIcon("images/campaign_load_hover.png");
    ImageIcon newCampaignPressedIcon = new ImageIcon("images/new_hover.png");

    //레벨별 수치 배열
    private int[][] levelStats =
            {
                    //앞에서부터 차례로 석탄, 식량, 인구수, 거주지 온도 수준, 현재 온도임
                    {400, 100, 100, 1, -10},
                    {300, 50, 100, 1, -10},
                    {200, 50, 150, 2, -20},
                    {150, 50, 200, 3, -30},
                    {100, 50, 250, 4, -40}
            };

    public CampaignSelectWindow()
    {
        super("캠페인 모드 선택 창");

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

        //기본 UI 생성
        CreateButtons();    // 버튼들 만드는 메소드

        //가시성 설정
        this.setVisible(true);      // 프레임이 보이도록 설정
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

    //요소들을 화면에 맞게 동적으로 비율을 정해서 상대적으로 배치
    private JToggleButton CreateRelativeButton(ImageIcon defaultIcon, ImageIcon selectedIcon, int width, int height, int xPositionPercent, int yPositionPercent, int elementWidthPercent, int elementHeightPercent)
    {
        int positionX = (int)((width * xPositionPercent) / 100.0);       // 현재 화면을 기준으로 계산된 버튼의 상대적 가로 위치
        int positionY = (int)((height * yPositionPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 세로 위치
        int elementWidth = (int) ((width * elementWidthPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 가로값
        int elementHeight = (int)((height * elementHeightPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 세로값
        JToggleButton newButton = new JToggleButton(defaultIcon);        // 버튼 컴포넌트 생성
        newButton.setBounds(positionX, positionY, elementWidth, elementHeight);     // 계산된 값들을 이용해 상대위치 / 크기로 버튼 생성

        //버튼의 상대적 크기에 맞게 모든 아이콘의 크기 조절
        ImageIcon resizedDefaultIcon = ResizeIcon(defaultIcon, elementWidth, elementHeight);
        ImageIcon resizedSelectedIcon = ResizeIcon(selectedIcon, elementWidth, elementHeight);

        //조정된 아이콘들을 버튼에 적용
        newButton.setIcon(resizedDefaultIcon);
        newButton.setSelectedIcon(resizedSelectedIcon);

        newButton.setContentAreaFilled(false);     //버튼 내부 채우지 않도록 하는 메소드
        newButton.setBorderPainted(false);     //버튼 외곽선 삭제
        newButton.setFocusPainted(false);      //버튼 선택 시 생성되는 얇은 선 삭제

        return newButton;       // 생성된 버튼 반환
    }

    private JButton CreateRelativeButton(ImageIcon defaultIcon, ImageIcon rolloverIcon, ImageIcon pressedIcon, int width, int height, int xPositionPercent, int yPositionPercent, int elementWidthPercent, int elementHeightPercent)
    {
        int positionX = (int)((width * xPositionPercent) / 100.0);       // 현재 화면을 기준으로 계산된 버튼의 상대적 가로 위치
        int positionY = (int)((height * yPositionPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 세로 위치
        int elementWidth = (int) ((width * elementWidthPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 가로값
        int elementHeight = (int)((height * elementHeightPercent) / 100.0);    // 현재 화면을 기준으로 계산된 버튼의 상대적 세로값
        JButton newButton = new JButton(defaultIcon);        // 버튼 컴포넌트 생성
        newButton.setBounds(positionX, positionY, elementWidth, elementHeight);     // 계산된 값들을 이용해 상대위치 / 크기로 버튼 생성

        //버튼의 상대적 크기에 맞게 모든 아이콘의 크기 조절
        ImageIcon resizedDefaultIcon = ResizeIcon(defaultIcon, elementWidth, elementHeight);
        ImageIcon resizedRollOverIcon = ResizeIcon(rolloverIcon, elementWidth, elementHeight);
        ImageIcon resizedPressedIcon = ResizeIcon(pressedIcon, elementWidth, elementHeight);

        //조정된 아이콘들을 버튼에 적용
        newButton.setIcon(resizedDefaultIcon);
        newButton.setRolloverIcon(resizedRollOverIcon);
        newButton.setPressedIcon(resizedPressedIcon);

        newButton.setContentAreaFilled(false);     //버튼 내부 채우지 않도록 하는 메소드
        newButton.setBorderPainted(false);     //버튼 외곽선 삭제
        newButton.setFocusPainted(false);      //버튼 선택 시 생성되는 얇은 선 삭제

        return newButton;       // 생성된 버튼 반환
    }

    //버튼 하나만 선택할 수 있도록 하는 메소드
    private void DeselectButtons(JToggleButton selectedButton)
    {
        JToggleButton[] campaignButtons =
                {
                        this.lv1Button,
                        this.lv2Button,
                        this.lv3Button,
                        this.lv4Button,
                        this.lv5Button,
                };
        for(JToggleButton button : campaignButtons)
        {
            if(button != selectedButton && button.isSelected())
            {
                button.setSelected(false);
            }
        }
        UpdateNewCampaignButtonState();
    }

    // 게임 시작하기 버튼의 상태 병경 메소드
    private void UpdateNewCampaignButtonState()
    {
        boolean isAnySelected =
                this.lv1Button.isSelected() ||
                this.lv2Button.isSelected() ||
                this.lv3Button.isSelected() ||
                this.lv4Button.isSelected() ||
                this.lv5Button.isSelected();
        //하나라도 선택이 되어있다면 버튼 활성화
        this.newCampaignButton.setEnabled(isAnySelected);
    }

    //캠페인 모드를 선택하는 버튼들 생성
    private void CreateButtons()
    {
        //버튼 생성
        this.lv1Button = CreateRelativeButton(lv1DefaultIcon, lv1SelectedIcon, deviceWidth, deviceHeight, 5, 15, 18, 69);
        this.lv2Button = CreateRelativeButton(lv2DefaultIcon, lv2SelectedIcon, deviceWidth, deviceHeight, 23, 15, 18, 69);
        this.lv3Button = CreateRelativeButton(lv3DefaultIcon, lv3SelectedIcon, deviceWidth, deviceHeight, 41, 15, 18, 69);
        this.lv4Button = CreateRelativeButton(lv4DefaultIcon, lv4SelectedIcon, deviceWidth, deviceHeight, 59, 15, 18, 69);
        this.lv5Button = CreateRelativeButton(lv5DefaultIcon, lv5SelectedIcon, deviceWidth, deviceHeight, 77, 15, 18, 69);
        this.backButton = CreateRelativeButton(backDefaultIcon, backRollOverIcon, backPressedIcon, deviceWidth, deviceHeight, 5, 89, 4, 7);
        this.campaignLoadButton = CreateRelativeButton(campaignLoadDefaultIcon, campaignLoadRollOverIcon, campaignLoadPressedIcon, deviceWidth, deviceHeight, 12, 90, 13, 5);
        this.newCampaignButton = CreateRelativeButton(newCampaignDefaultIcon, newCampaignRollOverIcon, newCampaignPressedIcon, deviceWidth, deviceHeight, 28, 90, 13, 5);

        //newCampaignButton 을 비활성화 상태로 초기화
        this.newCampaignButton.setEnabled(false);
        this.newCampaignButton.setDisabledIcon(newCampaignDefaultIcon);

        //버튼 기능 오버라이딩
        this.lv1Button.addActionListener(new ActionListener()
        {
           @Override
           public void actionPerformed(ActionEvent e)
           {
               SelectLv1();
           }
        });
        this.lv2Button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SelectLv2();
            }
        });
        this.lv3Button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SelectLv3();
            }
        });
        this.lv4Button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SelectLv4();
            }
        });
        this.lv5Button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SelectLv5();
            }
        });
        this.backButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PressedBackButton();
            }
        });
        this.campaignLoadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PressedCampaignLoadButton();
            }
        });
        this.newCampaignButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PressedNewCampaignButton();
            }
        });

        //버튼 추가
        this.add(this.lv1Button);
        this.add(this.lv2Button);
        this.add(this.lv3Button);
        this.add(this.lv4Button);
        this.add(this.lv5Button);
        this.add(this.backButton);
        this.add(this.campaignLoadButton);
        this.add(this.newCampaignButton);
    }

    //버튼들 메소드
    private void SelectLv1()
    {
        DeselectButtons(this.lv1Button);
    }

    private void SelectLv2()
    {
        DeselectButtons(this.lv2Button);
    }

    private void SelectLv3()
    {
        DeselectButtons(this.lv3Button);
    }

    private void SelectLv4()
    {
        DeselectButtons(this.lv4Button);
    }

    private void SelectLv5()
    {
        DeselectButtons(this.lv5Button);
    }

    private void PressedBackButton()
    {
        new MainWindow();
        this.dispose();
    }

    private void PressedCampaignLoadButton()
    {
        //추후 추가예정
    }

    private void PressedNewCampaignButton()
    {
        //선택한 버튼에 따른 레벨
        int selectedLevel = 0;
        if(this.lv1Button.isSelected())
        {
            selectedLevel = 0;
        }
        else if(this.lv2Button.isSelected())
        {
            selectedLevel = 1;
        }
        else if(this.lv3Button.isSelected())
        {
            selectedLevel = 2;
        }
        else if(this.lv4Button.isSelected())
        {
            selectedLevel = 3;
        }
        else if(this.lv5Button.isSelected())
        {
            selectedLevel = 4;
        }
        int[] stats = levelStats[selectedLevel];    //선택한 레벨의 기본 자원 수치 가져오기
        new GameFrame(stats[0], stats[1], stats[2], stats[3], stats[4]);        //자원량 전달하는 생성자
        this.dispose();
    }
}
