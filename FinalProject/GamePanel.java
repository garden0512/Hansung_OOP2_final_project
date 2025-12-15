import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private JLabel fallingLabel = new JLabel("");
    private GroundPanel groundPanel = new GroundPanel();
    private TextStore textStore = null;
    private FallingThread fallingThread = new FallingThread();

    // 생성자 시그니처를 유지하되, scorePanel을 Object로 받도록 변경하여 유연하게 대처
    public GamePanel(TextStore textStore)
    {
        this.textStore = textStore;
        this.setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        add(new InputPanel(), BorderLayout.SOUTH);
        add(groundPanel, BorderLayout.CENTER);
    }

    // 폰트 설정 메소드 유지
    public void initializeUI(int preferredFontSize) {
        fallingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, preferredFontSize));
        fallingLabel.setForeground(Color.BLACK);

        // InputPanel의 JTextField에도 폰트 설정
        ((InputPanel)getComponent(0)).inputText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, preferredFontSize));
    }

    public void start()
    {
        fallingLabel.setVisible(true);
        String text = textStore.get();
        fallingLabel.setText(text);

        if (!fallingThread.isAlive()) {
            fallingThread.start();
        }
    }

    class FallingThread extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                try
                {
                    sleep(300);
                    int x = fallingLabel.getX();
                    int y = fallingLabel.getY();
                    fallingLabel.setLocation(x, y + 10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    class GroundPanel extends JPanel
    {
        public GroundPanel()
        {
            this.setBackground(Color.DARK_GRAY);
            this.setLayout(null);
            fallingLabel.setSize(200, 100);
            fallingLabel.setLocation(100, 100);
            fallingLabel.setVisible(false);
            add(fallingLabel);
        }
    }

    class InputPanel extends JPanel
    {
        private JTextField inputText = new JTextField(20);
        public InputPanel()
        {
            this.setBackground(Color.GRAY);
            add(inputText);
            inputText.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JTextField textField = (JTextField) (e.getSource());
                    String inputText = textField.getText();
                    if(inputText.equals(fallingLabel.getText()))
                    {
                        String text = textStore.get();
                        fallingLabel.setText(text);
                        fallingLabel.setLocation((int)(Math.random() * groundPanel.getWidth() * 0.8), 50);  //X위치를 랜덤으로 설정
                        textField.setText("");
                    }
                }

            });
        }
    }
}