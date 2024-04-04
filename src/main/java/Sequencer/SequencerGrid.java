package Sequencer;

import Instruments.*;
import TimeCycle.TimeCycle;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SequencerGrid extends JPanel {

    private ArrayList<SequencerPanel> panelList; // Tracks all panels in the sequencer grid
    private String[] soundNames = { "Kick", "Snare", "Closed Hi-Hat", "Open Hi-Hat", "Crash", "High Tom", "Mid Tom",
            "Low Tom" };
    private int[] midiNotes = { 36, 38, 42, 46, 49, 50, 47, 45 };
    private Sequence sequence;
    private JButton playButton, stopButton, saveButton, loadButton, clearButton;
    private TimeCycle timeCycle;

    public SequencerGrid() {
        setLayout(new BorderLayout());
        sequence = new Sequence();
        initializeButtons();
        initializeSequencerGrid();
        timeCycle = new TimeCycle(sequence);
    }

    private void initializeButtons() {
        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        clearButton = new JButton("Clear");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);

        JButton randomizeButton = new JButton("Randomize");
        buttonPanel.add(randomizeButton);
        randomizeButton.addActionListener(e -> randomizePanelStates());

        // Create tempo slider with adjusted size
        JSlider tempoSlider = new JSlider(JSlider.HORIZONTAL, 40, 240, 120); // Range from 40 to 240 BPM, default at 120
        tempoSlider.setMajorTickSpacing(40); // Adjust this for fewer major ticks if needed
        tempoSlider.setMinorTickSpacing(10); // Adjust this for fewer minor ticks if needed
        tempoSlider.setPaintTicks(true);
        tempoSlider.setPaintLabels(true);
        tempoSlider.setFont(new Font("Default", Font.PLAIN, 10));

        // Set the preferred size of the slider to be smaller
        tempoSlider.setPreferredSize(new Dimension(200, 40));

        tempoSlider.addChangeListener(e -> timeCycle.setTempo(tempoSlider.getValue()));

        buttonPanel.add(new JLabel("Tempo:"));
        buttonPanel.add(tempoSlider);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        playButton.addActionListener(e -> {
            timeCycle.playSequence();
        });

        stopButton.addActionListener(e -> {
            timeCycle.stopSequence();
        });

        saveButton.addActionListener(e -> saveSequence());

        loadButton.addActionListener(e -> loadSequence());

        clearButton.addActionListener(e -> clearSequence());

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void playSequence() {
        System.out.println("SEQUENCERGRID: Play Sequence");
        sequence.playSequence();

    }

    private void stopSequence() {
        System.out.println("SEQUENCERGRID: Stop Sequence");
    }

    private void clearSequence() {
        System.out.println("SEQUENCEGRID: Clear Sequence");
        for (SequencerPanel panel : panelList) {
            panel.setActive(false);
        }
    }

    private void saveSequence() {
        String sequenceName = JOptionPane.showInputDialog(this, "Enter a name for the sequence:");

        if (sequenceName != null && !sequenceName.trim().isEmpty()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(sequenceName + ".ser"))) {
                out.writeObject(sequence);
                System.out.println("Sequence \"" + sequenceName + "\" saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sequence not saved. Name cannot be empty.", "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> getAvailableSequenceNames() {
        List<String> sequenceNames = new ArrayList<>();
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));

        if (files != null) {
            for (File file : files) {
                String sequenceName = file.getName().replaceFirst("[.][^.]+$", ""); // Remove file extension
                sequenceNames.add(sequenceName);
            }
        }
        System.out.println("Sequence names: \"" + sequenceNames);
        return sequenceNames;
    }

    private void loadSequence() {
        // Provide a list of available sequences to the user
        // Let's assume you have a list of sequence names or identifiers

        System.out.println("Loading Sequence");
        List<String> sequenceNames = getAvailableSequenceNames();
        if (!sequenceNames.isEmpty()) {
            // Prompt the user to select a sequence
            String selectedSequenceName = (String) JOptionPane.showInputDialog(this,
                    "Select a sequence to load:", "Load Sequence", JOptionPane.QUESTION_MESSAGE,
                    null, sequenceNames.toArray(), sequenceNames.get(0));

            if (selectedSequenceName != null) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(selectedSequenceName + ".ser"))) {
                    Sequence loadedSequence = (Sequence) in.readObject();
                    updateGridWithSequence(loadedSequence);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error loading sequence: " + selectedSequenceName, "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No sequences available to load.", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGridWithSequence(Sequence sequence) {
        System.out.println("SEQUENCERGRID: Updating grid with sequence...");
        System.out.println("SEQUENCERGRID: Number of events in sequence: " + sequence.getSequence().size());

        for (SequencerPanel panel : panelList) {
            panel.setActive(false);
        }

        // Apply the sequence to the grid
        for (Instrument.InstrumentEvent event : sequence.getSequence()) {
            int panelId = event.getPanelId();
            boolean panelFound = false; // Flag to check if the panel is found

            for (SequencerPanel panel : panelList) {
                if (panel.getPanelId() == panelId) {
                    panel.setActive(true);
                    panelFound = true;
                    System.out.println("SEQUENCERGRID: Activating panel ID: " + panelId); // Debugging
                    break; // Panel found and activated, no need to continue in the inner loop
                }
            }

            if (!panelFound) {
                // If no panel was found for an event, log this information
                System.out.println("SEQUENCERGRID: No panel found for panel ID: " + panelId);
                // Update UI to reflect deselected node
                // Assuming setActive(false) method is provided in SequencerPanel class
                // You might need to call repaint() after this loop if needed
                for (SequencerPanel panel : panelList) {
                    if (panel.getPanelId() == panelId) {
                        panel.setActive(false);
                        break;
                    }
                }
                repaint();
            }
        }
        repaint(); // Refresh the UI to reflect changes if needed
    }

    public void randomizePanelStates() {
        for (SequencerPanel panel : panelList) {
            double probability = 0.2 + (Math.random() * 0.5);
            if (Math.random() < probability) {
                if (!panel.getState()) {
                    // panel.setActive(!panel.getState());
                    panel.setActive(true);
                }
            } else {
                if (panel.getState()) {
                    panel.setActive(false);
                }
            }
        }
    }

    private void initializeSequencerGrid() {
        // Panel that will contain both the labels and the sequencer grid
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // Code pertaining the label column for the sequencer grid
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(8, 1));
        labelPanel.setBackground(Color.DARK_GRAY);

        // Code pertaining sequencer grid itself
        JPanel gridPanel = new JPanel(new GridLayout(8, 16)); // The sequencer grid
        gridPanel.setBackground(Color.DARK_GRAY); // Ensuring the background is consistent

        panelList = new ArrayList<>();

        // Assume SequencerPanel sets its preferred height or adjust as needed
        Dimension labelSize = new Dimension(100, gridPanel.getPreferredSize().height / 8);

        // Addition of the labels and the corresponding sounds
        for (int i = 0; i < soundNames.length; i++) {
            JLabel soundLabel = new JLabel(soundNames[i], SwingConstants.CENTER);
            soundLabel.setForeground(Color.WHITE);
            labelPanel.add(soundLabel);

            for (int j = 0; j < 16; j++) {
                Instrument instrument = new Instrument(soundNames[i], midiNotes[i]);
                SequencerPanel panel = new SequencerPanel(i * 16 + j, instrument, sequence);
                gridPanel.add(panel);
                panelList.add(panel);
            }
        }

        add(labelPanel, BorderLayout.WEST);
        add(gridPanel, BorderLayout.CENTER);
    }

}
