package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

/**
 * Common HTTP logic shared by every stage API fetcher.
 * Subclasses only provide the URL and parse the response body into their domain type.
 */
public abstract class ApiFetcher {

    public interface HttpResponseCallback {
        void onSuccess(String body);
        void onFailure(Throwable error);
    }

    protected void fetch(String url, final HttpResponseCallback callback) {
        Net.HttpRequest httpRequest = createRequest(url);
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                callback.onSuccess(httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void cancelled() {
                callback.onFailure(new Exception("Request cancelled"));
            }
        });
    }

    private Net.HttpRequest createRequest(String url) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        return requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .build();
    }
}
