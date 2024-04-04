package Sequencer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Instruments.*;

public class SequencerPanel extends JPanel {
    private int panelId;
    private Instrument instrument; // Associated Instrument for MIDI playback
    private Sequence sequence;
    private boolean offOrOn;

    private JSlider velocitySlider; // Slider for controlling velocity
    private int velocity = 100; // Default velocity

    public SequencerPanel(int panelId, Instrument instrument, Sequence sequence) {
        this.panelId = panelId;
        this.instrument = instrument;
        this.sequence = sequence;

        offOrOn = false; // Default state is off
        setPreferredSize(new Dimension(100, 100)); // Set panel size
        setBorder(BorderFactory.createLineBorder(Color.gray)); // Set panel border
        setBackground(Color.black); // Default background color

        velocitySlider = new JSlider(JSlider.VERTICAL, 0, 127, velocity);
        velocitySlider.setPreferredSize(new Dimension(10, 100)); // Adjust size as needed

        velocitySlider.addChangeListener(e -> {
            int newVelocity = velocitySlider.getValue();
            // Update velocity for all events this panel is responsible for
            sequence.updateInstrumentEventVelocity(instrument, panelId, newVelocity);
            velocity = newVelocity; // Update local velocity to reflect the change
        });

        // Add the slider to the panel
        this.setLayout(new BorderLayout()); // Use BorderLayout to manage components
        add(velocitySlider, BorderLayout.EAST); // Add the slider to the east side of the panel

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleState();
            }
        });
    }

    public void setActive(boolean active) {
        // Optionally trigger instrument playback or stop it depending on the state.
        if (active) {
            instrument.playNote(velocity);
            sequence.addInstrumentEvent(instrument, System.currentTimeMillis(), panelId, velocity);
            setBackground(Color.blue);
            offOrOn = true;
        } else {
            instrument.stopNote();
            sequence.removeInstrumentEvent(instrument, panelId);
            setBackground(Color.black);
            offOrOn = false;
        }
        repaint(); // Update the panel's appearance based on its new state.
    }

    public int getPanelId() {
        return panelId;
    }

    public boolean getState() {
        return offOrOn;
    }

    public void toggleState() {
        offOrOn = !offOrOn;
        if (offOrOn) {
            instrument.playNote(velocity);
            sequence.addInstrumentEvent(instrument, System.currentTimeMillis(), panelId, velocity);
            setBackground(Color.blue);
        } else {
            instrument.stopNote();
            sequence.removeInstrumentEvent(instrument, panelId);
            setBackground(Color.black);
        }
        repaint();
    }
}
