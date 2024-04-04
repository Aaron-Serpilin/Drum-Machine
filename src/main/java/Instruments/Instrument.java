package Instruments;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.*;

public class Instrument implements Serializable {
    private static Synthesizer synthesizer;
    private static MidiChannel[] midiChannels;
    private String name;
    private int midiNote;
    private int velocity;

    // Static block to initialize MIDI system once for all instances
    static {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midiChannels = synthesizer.getChannels();
        } catch (MidiUnavailableException e) {
            e.printStackTrace(); // Handle exceptions as appropriate
        }
    }

    public Instrument(String name, int midiNote) {
        this.name = name;
        this.midiNote = midiNote;
    }

    public void playNote(int velocity) {
        midiChannels[9].noteOn(this.midiNote, velocity); // Channel 9 is for percussion instruments. Adjust as needed.
    }

    public void stopNote() {
        midiChannels[9].noteOff(this.midiNote);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public int getMidiNote() {
        return midiNote;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMidiNote(int midiNote) {
        this.midiNote = midiNote;
    }

    public static class InstrumentEvent implements Serializable {
        private final Instrument instrument;
        private final long timestamp; // Used for the time cycles
        private final int panelId;
        private int velocity;

        public InstrumentEvent(Instrument instrument, long timestamp, int panelId, int velocity) {
            this.instrument = instrument;
            this.timestamp = timestamp;
            this.panelId = panelId;
            this.velocity = velocity;
        }

        public Instrument getInstrument() {
            return instrument;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getPanelId() {
            return panelId;
        }

        public void setVelocity(int velocity) {
            this.velocity = velocity;
        }

        public int getVelocity() {
            return velocity;
        }

    }
}
