package Sequencer;

import Instruments.*;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Sequence implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Instrument.InstrumentEvent> sequence;

    public Sequence() {
        this.sequence = new ArrayList<>();
        System.out.println("SEQUENCE: Sequence initialized with 0 sequence.");
    }

    public void addInstrumentEvent(Instrument instrument, long timestamp, int panelId, int velocity) {
        sequence.add(new Instrument.InstrumentEvent(instrument, timestamp, panelId, velocity));
    }

    public void removeInstrumentEvent(Instrument instrument, int panelId) {
        boolean wasRemoved = sequence
                .removeIf(event -> event.getInstrument().equals(instrument) && event.getPanelId() == panelId);
        if (wasRemoved) {
            System.out.println("SEQUENCE: Event removed. Total sequence now: " + sequence.size());
        } else {
            System.out.println("SEQUENCE: No event found to remove for panelId " + panelId);
        }
    }

    public void updateInstrumentEventVelocity(Instrument instrument, int panelId, int newVelocity) {
        sequence.stream()
                .filter(event -> event.getInstrument().equals(instrument) && event.getPanelId() == panelId)
                .forEach(event -> event.setVelocity(newVelocity)); // Assuming you've added a setter for velocity in
                                                                   // InstrumentEvent
    }

    // Plays back the sequence
    public void playSequence() {
        System.out.println("SEQUENCE: Playing sequence with " + sequence.size() + " sequence.");
        // Need to work on it however because it plays all notes without considering
        // timing. Need to implement a mechanism to handle timing accurately
        for (Instrument.InstrumentEvent event : sequence) {
            event.getInstrument().playNote(event.getVelocity());
        }
    }

    public List<Instrument.InstrumentEvent> getSequence() {
        System.out.println("SEQUENCE: Retrieving sequence. Total sequence: " + sequence.size());
        return new ArrayList<>(this.sequence);
    }

    public String getSequenceString() {
        StringBuilder sequenceString = new StringBuilder("Sequence: [");

        for (int i = 0; i < sequence.size(); i++) {
            Instrument.InstrumentEvent event = sequence.get(i);
            sequenceString.append("{Note: ").append(event.getInstrument().getMidiNote());
            sequenceString.append(", Timestamp: ").append(event.getTimestamp()).append("}");
            if (i < sequence.size() - 1) {
                sequenceString.append(", ");
            }
        }
        sequenceString.append("]");
        System.out.println("SEQUENCE: sequence string: " + sequenceString.toString());
        return sequenceString.toString();
    }

    public boolean containsEventForPanel(int panelId) {
        boolean contains = sequence.stream().anyMatch(event -> event.getPanelId() == panelId);
        System.out.println("SEQUENCE: Checking if contains event for panelId " + panelId + ": " + contains);
        return contains;
    }
}

// public class SequenceStorage {
// public static void main(String[] args) {
// List<Sequence> sequences = new ArrayList<>();
// sequences.add(new Sequence(List.of(1, 2, 3, 4, 5)));
// sequences.add(new Sequence(List.of(6, 7, 8, 9, 10)));

// // Serialize the sequences
// try (ObjectOutputStream out = new ObjectOutputStream(new
// FileOutputStream("sequences.ser"))) {
// out.writeObject(sequences);
// System.out.println("Sequences saved successfully.");
// } catch (IOException e) {
// e.printStackTrace();
// }

// // Deserialize the sequences
// try (ObjectInputStream in = new ObjectInputStream(new
// FileInputStream("sequences.ser"))) {
// List<Sequence> loadedSequences = (List<Sequence>) in.readObject();
// System.out.println("Sequences loaded successfully:");
// for (Sequence seq : loadedSequences) {
// System.out.println(seq.getSequence());
// }
// } catch (IOException | ClassNotFoundException e) {
// e.printStackTrace();
// }
// }
// }
