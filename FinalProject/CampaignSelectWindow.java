import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class CampaignSelectWindow extends JFrame {
    private int deviceWidth;
    private int deviceHeight;

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

        //가시성 설정
        this.setVisible(true);      // 프레임이 보이도록 설정
    }
}
