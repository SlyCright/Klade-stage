package site.klade.stage;

import com.badlogic.gdx.Gdx;
import site.klade.simulation.Arena;
import site.klade.simulation.ArenaSettings;
import site.klade.simulation.Genome;

import java.util.ArrayList;

public class ArenaSimulation {

    private static final int FRAMES_PER_TICK = 2;

    private Arena arena;
    private long arenaTotalTicks = 0;
    private int frameCounter = 0;

    public ArenaSimulation() {
        // No arena until settings are fetched from Main; defaults live only in
        // Main's SimulationProperties and are never replicated here.
    }

    public void update() {
        if (arena == null) {
            return;
        }
        frameCounter++;
        if (frameCounter >= FRAMES_PER_TICK) {
            arena.update();
            frameCounter = 0;
            arenaTotalTicks++;
        }
    }

    public void resetArena(final ArrayList<Genome> genomes, final ArenaSettings settings) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                arena = new Arena(genomes, settings);
                arenaTotalTicks = 0;
                frameCounter = 0;
            }
        });
    }

    public boolean isEvaluationComplete() {
        return arena != null && arena.isDone();
    }

    public long getTotalTicks() {
        return arenaTotalTicks;
    }

    public Arena getArena() {
        return arena;
    }
}
