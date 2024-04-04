package GUI;

import Sequencer.*;

import javax.swing.*;
import java.awt.*;

public class DrumMachineGUI {
    private JFrame frame;
    private SequencerGrid sequencerGrid;

    public DrumMachineGUI(SequencerGrid sequencerGrid) {
        this.sequencerGrid = sequencerGrid;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Drum Machine GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        JPanel containerPanel = new JPanel(new GridBagLayout()); // Container panel with GridBagLayout
        GridBagConstraints gridConstraints = new GridBagConstraints();
        containerPanel.setBackground(new Color(87, 23, 32));

        // GridBagConstraints settings
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.weightx = 0.3;
        gridConstraints.weighty = 0.3;
        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.insets = new Insets(300, 200, 100, 200);

        // Use the stored SequencerGrid instead of instantiating a new one
        containerPanel.add(this.sequencerGrid, gridConstraints);
        frame.add(containerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }
}