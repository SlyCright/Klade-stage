package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import site.klade.simulation.Gene;
import site.klade.simulation.Genome;
import site.klade.simulation.MetaGenes;
import site.klade.simulation.Morphogen;

import java.util.ArrayList;

public class GenomeFetcher {

    public void fetchBestGenome(final GenomeFetchCallback callback) {
        Net.HttpRequest httpRequest = createGenomeRequest();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                try {
                    ArrayList<Genome> newGenomes = parseGenomesFromJson(response);
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
            public void failed(Throwable t) {
                Gdx.app.log("GenomeFetcher", "HTTP request failed", t);
                callback.onFailure(t);
            }

            @Override
            public void cancelled() {
                callback.onFailure(new Exception("Request cancelled"));
            }
        });
    }

    private Net.HttpRequest createGenomeRequest() {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        return requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url("/api/best-genome")
                .build();
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

        String genomeDsl = genomeValue.getString("genomeDsl", null);
        if (genomeDsl != null && !genomeDsl.isEmpty()) {
            Gdx.app.log("GenomeFetcher", "genomeDsl received but stage cannot parse DSL yet; using initialAngle only.");
        }

        return genome;
    }
}
