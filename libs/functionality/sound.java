package libs.functionality;

import java.io.File;
import javax.sound.sampled.*;
import javax.sound.sampled.Clip.*;
import javax.sound.sampled.AudioSystem;

public class sound {
	File audioFile;
	Clip clip;
	AudioInputStream stream;
	FloatControl volume;
	boolean loaded = false, playing = false;
	long frame;
	public sound(String fn) {
		try {
			this.audioFile = new File(fn);
			this.stream = AudioSystem.getAudioInputStream(this.audioFile);
			this.clip = AudioSystem.getClip();
			this.clip.open(this.stream);
			this.frame = 0;
			this.loaded = true;
			this.pauseClip();
		} catch (Exception e) { System.out.println(e); }
	}
	public void loop() {
		this.clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void play() {
		if (this.loaded && !this.playing) {
			this.clip.start();
			this.playing = true;
		}
	}
	public void playBeginning() {
		if (this.loaded) {
			this.reset();
			this.clip.start();
			this.playing = true;
		}
	}
	public void pauseClip() {
		if (this.playing && this.loaded) {
			this.frame = this.clip.getMicrosecondPosition();
			this.clip.stop();
			this.playing = false;
		}
	}
	public void closeClip() {
		if (this.loaded) {
			this.frame = 0;
			this.clip.stop();
			this.clip.close();
			this.playing = false;
		}
	}
	public void resumeClip() {
		if (!this.playing && this.loaded) {
			this.clip.close();
			this.reset();
			this.clip.setMicrosecondPosition(this.frame);
			this.play();
		}
	}
	void reset() {
		try {
			this.stream = AudioSystem.getAudioInputStream(this.audioFile);
			this.clip = AudioSystem.getClip();
			this.clip.open(this.stream);
		} catch (Exception e) { System.out.println(e); }
	}
};
