package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import site.klade.simulation.Gene;
import site.klade.simulation.Genome;
import site.klade.simulation.MetaGenes;
import site.klade.simulation.Morphogen;

import java.util.ArrayList;

public class GenomeFetcher extends ApiFetcher {

    private static final String BEST_GENOME_URL = "/api/best-genome";

    public void fetchBestGenome(final GenomeFetchCallback callback) {
        fetch(BEST_GENOME_URL, new HttpResponseCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    ArrayList<Genome> newGenomes = parseGenomesFromJson(body);
                    if (newGenomes != null) {
                        callback.onSuccess(newGenomes);
                    } else {
                        callback.onFailure(new Exception("No genome data received"));
                    }
                } catch (Exception e) {
                    Gdx.app.log("GenomeFetcher", "Failed to parse genome JSON", e);
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Gdx.app.log("GenomeFetcher", "HTTP request failed", error);
                callback.onFailure(error);
            }
        });
    }

    private ArrayList<Genome> parseGenomesFromJson(String response) {
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(response);

        if (root == null) {
            Gdx.app.log("GenomeFetcher", "No genome data received, keeping current arena.");
            return null;
        }

        JsonValue genomesArray = root.get("genomes");
        if (genomesArray == null || !genomesArray.isArray() || genomesArray.size == 0) {
            Gdx.app.log("GenomeFetcher", "No genomes array in response, keeping current arena.");
            return null;
        }

        ArrayList<Genome> genomes = new ArrayList<Genome>();
        for (JsonValue genomeValue : genomesArray) {
            genomes.add(parseGenome(genomeValue));
        }

        return genomes;
    }

    /**
     * Parses one genome entry of the /api/best-genome response:
     * { "initialAngle": float, "fitness": float, "speciesIndex": int, "genomeDsl": string|null }.
     * Only initialAngle is consumed for rendering; morphogens and genes are left empty.
     */
    private Genome parseGenome(JsonValue genomeValue) {
        MetaGenes metaGenes = new MetaGenes();
        metaGenes.setInitialAngle(genomeValue.getFloat("initialAngle", 0.0f));

        Genome genome = new Genome(metaGenes, new ArrayList<Morphogen>(), new ArrayList<Gene>());
        genome.setAccumulatedFitness(genomeValue.getFloat("fitness", 0.0f));

        // Note: the stage will never parse the genome DSL. The DSL text is kept
        // here purely for users to explore by hand, should that ever be of interest.
        String genomeDsl = genomeValue.getString("genomeDsl", null);
        if (genomeDsl != null && !genomeDsl.isEmpty()) {
            Gdx.app.log("GenomeFetcher", genomeDsl);
        }

        return genome;
    }
}
