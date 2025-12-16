import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AddNewWordWindow extends JFrame {
    private JTextField wordInputField;
    private JButton addButton;
    private JButton backButton;
    private static final String FILENAME = "words.txt";

    public AddNewWordWindow() {
        super("단어 추가 창");

        // 기본 프레임 설정
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //전체 프로그램 종료 방지
        this.setSize(500, 250);
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        this.setLayout(new BorderLayout(15, 15));
        this.setUndecorated(true);

        // 배경색 설정 (게임의 통일된 배경색 사용)
        Color backgroundColor = Color.decode("#0F1316");
        this.getContentPane().setBackground(backgroundColor);

        // --- 1. 중앙 입력 패널 ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        inputPanel.setBackground(backgroundColor);

        JLabel promptLabel = new JLabel("추가할 단어를 입력하세요:", SwingConstants.CENTER);
        promptLabel.setForeground(Color.WHITE);
        promptLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        wordInputField = new JTextField(20);
        wordInputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        wordInputField.setPreferredSize(new Dimension(250, 30));

        inputPanel.add(promptLabel);
        inputPanel.add(wordInputField);
        this.add(inputPanel, BorderLayout.CENTER);

        // --- 2. 하단 버튼 패널 ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(backgroundColor);

        // '추가' 버튼
        addButton = new JButton("단어 추가");
        styleButton(addButton, new Color(0x4CAF50)); // 초록색 계열
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewWord();
            }
        });

        // '뒤로가기' 버튼
        backButton = new JButton("닫기");
        styleButton(backButton, new Color(0xAAAAAA)); // 회색 계열
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 창 닫기
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(backButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();
        this.setVisible(true);
    }

    // 버튼 스타일링 헬퍼 메소드
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
    }

    // 파일에 단어를 저장하는 핵심 로직
    private void addNewWord() {
        String newWord = wordInputField.getText().trim();

        if (newWord.isEmpty()) {
            JOptionPane.showMessageDialog(this, "단어를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 파일에 단어 추가 (append 모드 사용)
        try (FileWriter fw = new FileWriter(FILENAME, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(newWord); // 새 단어를 새 줄에 추가

            JOptionPane.showMessageDialog(this, "'" + newWord + "'가 words.txt에 성공적으로 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            wordInputField.setText(""); // 입력 필드 초기화

            // TextStore를 즉시 업데이트하려면, TextStore 인스턴스를 외부에서 새로고침 해야 하지만,
            // 여기서는 창을 닫고 다음 게임 시작 시 TextStore가 자동으로 새로 로드하도록 유도합니다.

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 저장 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            System.err.println("단어 파일 저장 오류: " + e.getMessage());
        }
    }
}