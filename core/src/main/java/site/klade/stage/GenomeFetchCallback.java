package site.klade.stage;

import site.klade.simulation.Genome;

import java.util.ArrayList;

public interface GenomeFetchCallback {
    void onSuccess(ArrayList<Genome> genomes);
    void onFailure(Throwable error);
}
