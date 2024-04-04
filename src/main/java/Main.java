import DrumMachine.*;
import Sequencer.*;

public class Main {
    public static void main(String[] args) {
        DrumMachine drumMachine = new DrumMachine();

        drumMachine.initialize();

        SequencerGrid sequencerGrid = drumMachine.getSequencerGrid();
    }
}
