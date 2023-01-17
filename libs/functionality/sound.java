package libs.functionality;

import java.io.File;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;

public class sound {
	File audioFile;
	Clip clip;
	AudioInputStream stream;
	boolean loaded = false, playing = false;
	long frame;
	public sound(String fn) {
		try {
			this.audioFile = new File(fn);
			this.stream = AudioSystem.getAudioInputStream(this.audioFile);
			this.clip = AudioSystem.getClip();
			this.clip.open(this.stream);
			this.clip.loop(Clip.LOOP_CONTINUOUSLY);
			this.frame = 0;
		} catch (Exception e) { System.out.println(e); }
	}
	public void playBeginning() {
		this.clip.start();
		this.playing = true;
	}
	public void resumeClip() {
		if (!playing) {
			this.clip.close();
		}
	}
};
