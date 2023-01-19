package libs.functionality;

import java.io.File;
import javax.sound.sampled.*;
import javax.sound.sampled.Clip.*;
import javax.sound.sampled.AudioSystem;

public class sound {
	File audioFile;
	Clip clip;
	AudioInputStream stream;
	double volume = 1.0;
	boolean loaded = false, playing = false;
	long frame;
	public sound(String fn) {
		try {
			this.audioFile = new File(fn);
			this.stream = AudioSystem.getAudioInputStream(this.audioFile.getAbsoluteFile());
			this.clip = AudioSystem.getClip();
			this.clip.open(this.stream);
			this.frame = 0;
			this.loaded = true;
			this.pauseClip();
		} catch (Exception e) { System.out.println(e); }
	}
	float getVolume() {
		FloatControl control = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
		return (float) Math.pow(10f, control.getValue()/20f);
	}

	// v is between 0 - 1 (0 is lowest, 1 is loudest)
	public void setVolume(double v) {
		if (v >= 0f && v <= 1f) {
			FloatControl control = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
			float value = 20f * (float) Math.log10(v);
			this.volume = v;
			control.setValue(value);
		}
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
			this.setVolume(this.volume);
		} catch (Exception e) { System.out.println(e); }
	}
};
