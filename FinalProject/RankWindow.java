import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RankWindow extends JFrame
{
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
        int rankWidth = (int)(mainWindowWidth * 0.52);      //순위창 가로 길이 계산
        int rankHeight = (int)(mainWindowHeight * 0.52);    //순위창 세로 길이 계산
        int x = mainWindowWidth / 2 - rankWidth / 2;        //순위창의 X좌표
        int y = mainWindowHeight / 2 - rankHeight / 2;      //순위창의 Y좌표
        this.setSize(rankWidth, rankHeight);        //순위창의 크기 설정

        //배경색 및 이미지 설정
        try
        {
            Color backgroundColor = Color.decode("#a0F1316");
            this.getContentPane().setBackground(backgroundColor);
        }
        catch(NumberFormatException e)
        {
            this.getContentPane().setBackground(Color.BLACK);
        }
    }
}
