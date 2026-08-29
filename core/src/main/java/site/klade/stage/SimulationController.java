package site.klade.stage;

import com.badlogic.gdx.Gdx;
import site.klade.simulation.Arena;
import site.klade.simulation.Genome;

import java.util.ArrayList;

public class SimulationController {

    private static final int FRAMES_PER_TICK = 2;

    private Arena arena;
    private ArenaRenderer arenaRenderer;
    private long arenaTotalTicks = 0;
    private int frameCounter = 0;

    public SimulationController() {
        arena = new Arena(new Genome());
        arenaRenderer = new ArenaRenderer(arena);
    }

    public void update() {
        frameCounter++;
        if (frameCounter >= FRAMES_PER_TICK) {
            arena.update();
            frameCounter = 0;
            arenaTotalTicks++;
        }
    }

    public void resetArena(final ArrayList<Genome> genomes) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                arena = new Arena(genomes);
                arenaRenderer = new ArenaRenderer(arena);
                arenaTotalTicks = 0;
                frameCounter = 0;
            }
        });
    }

    public boolean isEvaluationComplete() {
        return arena.isEvaluationComplete();
    }

    public long getTotalTicks() {
        return arenaTotalTicks;
    }

    public ArenaRenderer getArenaRenderer() {
        return arenaRenderer;
    }

    public void dispose() {
        arenaRenderer.dispose();
    }
}
