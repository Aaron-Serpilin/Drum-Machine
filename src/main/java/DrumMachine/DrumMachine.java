package DrumMachine;

import Sequencer.*;
//import Sequencer.Sequencer;
import Instruments.*;
import javax.sound.midi.*;

import GUI.*;

public class DrumMachine {
    // All classes for basic functionality and personalized features
    //private Sequencer sequencer;
    private Synthesizer synthesizer;
    private DrumMachineGUI gui;
    private MidiChannel drumChannel;
    private SequencerGrid sequencerGrid;

    public void initialize() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            drumChannel = synthesizer.getChannels()[9]; // Drum channel in MIDI is channel 10

            sequencerGrid = new SequencerGrid(); // Instantiate SequencerGrid
            gui = new DrumMachineGUI(sequencerGrid); // Pass it to the DrumMachineGUI
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public SequencerGrid getSequencerGrid() {
        return sequencerGrid;
    }

    public void playDrumSound(int note, int velocity) {

        try {

            drumChannel.noteOn(note, velocity); // Play note
            Thread.sleep(500); // Hold note for 500 milliseconds
            drumChannel.noteOff(note);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }
    }

    public void closeSynthesizer() {
        if (synthesizer != null) {
            synthesizer.close();
        }
    }
}
