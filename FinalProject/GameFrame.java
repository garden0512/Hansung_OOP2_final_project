import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class GameFrame extends JFrame{
    private int deviceWidth;
    private int deviceHeight;
    private int coalAmount;
    private int foodAmount;
    private int population;
    private int houseTemperature;
    private int currentTemperature;

    //인게임 UI 이미지 로딩
    ImageIcon topUI = new ImageIcon("images/InGameUI.png");

    public GameFrame(int coalAmount, int foodAmount, int population, int houseTemperature, int currentTemperature)
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
        }
        else    // 풀스크린을 지원하지 않는다면
        {
            System.out.println("풀스크린을 지원하지 않는 기기입니다.");     // 경고 안내문구 출력
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);       // 가로, 세로 전부 최대치로 열리도록 함
            this.deviceWidth = graphicsDevice.getDisplayMode().getWidth();      // 현재 프레임의 가로값
            this.deviceHeight = graphicsDevice.getDisplayMode().getHeight();    // 현재 프레임의 세로값
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

        //상단 UI 그리기
        DisplayTopUIPanel();

        //가시성 설정
        this.setVisible(true);      // 프레임이 보이도록 설정

        //받아온 매개변수 값을 클래스 내부 변수에 저장
        this.coalAmount = coalAmount;       //석탄 양
        this.foodAmount = foodAmount;       //식량 양
        this.population = population;       //인구수
        this.houseTemperature = houseTemperature;       //거주지 온도
        this.currentTemperature = currentTemperature;       //현재 온도
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

    private void DisplayTopUIPanel()
    {
        int elementWidthPercent = 65;      //화면 가로에서 차지할 비율
        int elementHeightPercent = 16;     //화면 세로에서 차지할 비율
        int yPositionPercent = 0;   //상단에서 떨어질 비율
        int elementWidth = (int) ((deviceWidth * elementWidthPercent) / 100.0);
        int elementHeight = (int)((deviceHeight * elementHeightPercent) / 100.0);
        int positionY = (int)((deviceHeight * yPositionPercent) / 100.0);
        int positionX = (deviceWidth / 2) - (elementWidth / 2);

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
    }
}
