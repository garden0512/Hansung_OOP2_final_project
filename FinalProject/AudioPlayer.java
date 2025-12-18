import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip; // 오디오 클립을 제어하는 객체
    private FloatControl gainControl;
    public AudioPlayer(String filePath) {
        try {
            // 1. 오디오 입력 스트림 생성
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("오디오 파일이 없습니다: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // 2. Clip 객체를 통해 오디오 포맷 정보 얻기
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            // 3. Clip 객체 열기
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("지원되지 않는 오디오 파일 형식입니다: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("오디오 라인을 사용할 수 없습니다: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("오디오 파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    public void play(boolean loop) {
        if (clip != null) {
            clip.setFramePosition(0); // 재생 위치를 처음으로 리셋
            if (loop) {
                // Clip.LOOP_CONTINUOUSLY는 무한 반복을 의미합니다.
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void setVolume(double volume) {
        if (gainControl != null) {
            // 1. 최소/최대 볼륨 값 가져오기
            float minGain = gainControl.getMinimum();
            float maxGain = gainControl.getMaximum();

            // 2. 비율(volume)을 dB 값으로 변환
            // Math.log10(volume) * 20.0 공식 사용 (선형 비율을 로그 스케일인 dB로 변환)
            // volume이 0이면 Math.log10이 무한대가 되므로 최소값으로 제한합니다.
            double calculatedGain;
            if (volume <= 0.0) {
                calculatedGain = minGain;
            } else {
                calculatedGain = Math.min(maxGain, Math.max(minGain, (float)(Math.log10(volume) * 20.0)));
            }

            // 3. 볼륨 적용
            gainControl.setValue((float)calculatedGain);
            System.out.println("볼륨 설정 완료: " + volume * 100 + "% (" + calculatedGain + " dB)");
        }
    }
}