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

    /**
     * Resets the simulation to a fresh {@link Arena} built from the given genomes and settings,
     * returning that new arena immediately.
     *
     * <p>It is important to return the arena here rather than have the caller read it back via
     * {@link #getArena()}: the field swap runs through {@link Gdx#app postRunnable}, which executes
     * only on the next render-thread tick. Calling {@link #getArena()} right after this method would
     * therefore still return the previous arena (and on first load {@code null}), which the caller
     * would then capture into a renderer and crash on. Returning the already-constructed instance
     * lets the caller build its renderer over the exact arena that will be simulated, while the
     * field swap below still completes on the render thread.</p>
     *
     * @param genomes  genomes that will populate the new arena
     * @param settings settings that configure the new arena
     * @return the brand-new arena (same instance the simulation will tick once the runnable runs)
     */
    public Arena resetArena(final ArrayList<Genome> genomes, final ArenaSettings settings) {
        final Arena newArena = new Arena(genomes, settings);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                arena = newArena;
                arenaTotalTicks = 0;
                frameCounter = 0;
            }
        });
        return newArena;
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
