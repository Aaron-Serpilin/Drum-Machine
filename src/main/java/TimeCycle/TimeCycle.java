package TimeCycle;

import Instruments.*;
import Sequencer.Sequence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class TimeCycle {
    private ScheduledExecutorService scheduler;
    private Sequence sequence; // Store the sequence object
    // private ScheduledFuture<?> scheduledFuture; // To keep track of the scheduled
    // task
    // private volatile boolean isPlaying; // Track if the sequence is currently
    // playing
    private int tempo; // Beats per minute (BPM)
    private ScheduledFuture<?> scheduledFuture; // To keep track of the scheduled task
    private boolean isPlaying;

    public TimeCycle(Sequence sequence) {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.sequence = sequence;
        this.tempo = 120;
        this.isPlaying = false;
    }

    public void playSequence() {
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1);
        isPlaying = true;

        List<Instrument.InstrumentEvent> currentEvents = sequence.getSequence();
        List<List<Instrument.InstrumentEvent>> eventsByColumn = organizeEventsByColumn(currentEvents);
        long delayBetweenBeats = 60000 / tempo;
        final long totalSequenceDuration = delayBetweenBeats * eventsByColumn.size();

        for (int i = 0; i < eventsByColumn.size(); i++) {
            final int column = i;
            scheduler.schedule(() -> {
                eventsByColumn.get(column).forEach(event -> event.getInstrument().playNote(event.getVelocity()));
            }, i * delayBetweenBeats, TimeUnit.MILLISECONDS);
        }
        scheduler.schedule(() -> {
            if (isPlaying) { // Check if the sequence is supposed to be playing
                playSequence(); // Replay the sequence
            }
        }, totalSequenceDuration, TimeUnit.MILLISECONDS);
    }

    private List<List<Instrument.InstrumentEvent>> organizeEventsByColumn(List<Instrument.InstrumentEvent> events) {
        // Initialize the list of lists to store events for each column.
        List<List<Instrument.InstrumentEvent>> columnEvents = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            columnEvents.add(new ArrayList<>());
        }

        // Iterate through each event, determining its column based on panelId and
        // grouping accordingly.
        for (Instrument.InstrumentEvent event : events) {
            int column = event.getPanelId() % 16; // Assuming 16 columns. Adjust accordingly if different.
            columnEvents.get(column).add(event);
        }

        return columnEvents;
    }

    public void stopSequence() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow(); // Stop all currently executing tasks
            isPlaying = false; // Update flag to indicate the sequence is not playing
        }
    }

    public void setTempo(int newTempo) {
        this.tempo = newTempo;
    }

}
