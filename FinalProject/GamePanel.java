import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

public class GamePanel extends JPanel
{
    private Map<String, FallingWord> activeWords = new ConcurrentHashMap<>();
    private GroundPanel groundPanel = new GroundPanel();
    private TextStore textStore;
    private GameFrame gameFrame;
    private int preferredFontSize = 30;
    private int maxFallingWords = 30;       //최대 단어 개수
    private int coalBonus = 100;
    private int foodBonus = 100;
    private Timer wordGeneratorTimer;
    private Timer resourceConsumptionTimer;

    public GamePanel(TextStore textStore, GameFrame gameFrame)      //생성자
    {
        this.textStore = textStore;
        this.gameFrame = gameFrame;
        this.setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        add(new InputPanel(), BorderLayout.SOUTH);
        add(groundPanel, BorderLayout.CENTER);
    }

    // 폰트 설정 메소드 유지
    public void initializeUI(int preferredFontSize)     //폰트 설정 메소드 유지 및 preferredFontSize 저장
    {
        this.preferredFontSize = preferredFontSize;
        JTextField inputText = ((InputPanel)getComponent(0)).inputText;
        inputText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, preferredFontSize));
    }

    public void start()
    {
        this.wordGeneratorTimer = new Timer(1000, new ActionListener()     //1초마다 새 단어 생성
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GenerateNewWord();
            }
        });
        wordGeneratorTimer.start();
        this.resourceConsumptionTimer = new Timer(2000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                gameFrame.ConsumeResources();
            }
        });
        resourceConsumptionTimer.start();
    }

    private void GenerateNewWord()      //단어 생성 로직
    {
        if(activeWords.size() < maxFallingWords)
        {
            String text = textStore.get();
            if(text != null && !text.isEmpty() && !activeWords.containsKey(text))
            {
                boolean isCoalWord = new Random().nextBoolean();    //단어의 종류를 랜덤하게 정함.
                //GroundPanel의 크기를 기준으로 랜덤한 위치 계산
                int panelWidth = groundPanel.getWidth();
                int x = (int)(Math.random() * (panelWidth * 0.8));
                int y = 50;

                Font wordFont = new Font(Font.SANS_SERIF, Font.BOLD, preferredFontSize);
                FallingWord word = new FallingWord(text, x, y, wordFont, isCoalWord);
                SwingUtilities.invokeLater(()->
                {
                    groundPanel.add(word);
                    groundPanel.setComponentZOrder(word, 0);    //가장 위에 표시하기
                    groundPanel.revalidate();
                    groundPanel.repaint();
                });
                //Map에 등록 및 스레드 시작
                activeWords.put(text, word);
                new Thread(word).start();
            }
        }
    }

    public void RemoveWord(FallingWord word)
    {
        SwingUtilities.invokeLater(()->
        {
            groundPanel.remove(word);
            activeWords.remove(word.getText());
            groundPanel.revalidate();
            groundPanel.repaint();
            GenerateNewWord();      //단어가 사라지면 새 단어 생성
        });
    }

    public void StopAllWords()
    {
        if(wordGeneratorTimer != null)
        {
            wordGeneratorTimer.stop();
        }
        if(resourceConsumptionTimer != null)
        {
            resourceConsumptionTimer.stop();
        }
        for(FallingWord word : activeWords.values())
        {
            word.StopFalling();
        }
    }

    //개별 단어의 움직임을 처리하는 멀티스레드 클래스
    class FallingWord extends JLabel implements Runnable
    {
        private boolean isFalling = true;
        private boolean isCoalWord;

        public FallingWord(String text, int x, int y, Font wordFont, boolean isCoalWord)
        {
            super(text);
            this.isCoalWord = isCoalWord;
            this.setFont(wordFont);
            this.setForeground(isCoalWord ? Color.BLACK : new Color(0x966147));
            this.setSize(200, 50);
            this.setLocation(x, y);
            this.setVisible(true);
        }

        public void StopFalling()
        {
            this.isFalling = false;
        }

        public boolean isCoalWord()
        {
            return isCoalWord;
        }

        @Override
        public void run()
        {
            Thread.currentThread().setName("FallingWord - " + getText());
            while(isFalling)
            {
                try
                {
                    Thread.sleep(300);
                    SwingUtilities.invokeLater(()->
                    {
                        int newY = getY() + 10;
                        setLocation(getX(), newY);
                        if(newY > groundPanel.getHeight() - getHeight() - 10)   //여유있게 바닥에 닿았는지 확인
                        {
                            StopFalling();
                            RemoveWord(this);
                        }
                    });
                }
                catch(InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    StopFalling();
                    break;
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
                    if(inputText.equals("결과보기"))
                    {
                        gameFrame.IncreaseScore(1000);
                        gameFrame.EndGame();
                        textField.setText("");
                        return;
                    }
                    FallingWord matchedWord = activeWords.get(inputText);
                    if(matchedWord != null)
                    {
                        matchedWord.StopFalling();
                        if(matchedWord.isCoalWord())
                        {
                            gameFrame.AddCoal(coalBonus);
                        }
                        else
                        {
                            gameFrame.AddFood(foodBonus);
                        }
                        gameFrame.IncreaseScore(10);
                        RemoveWord(matchedWord);
                        textField.setText("");
                    }
                }
            });
        }
    }
}