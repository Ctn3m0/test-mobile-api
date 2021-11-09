package vn.edu.usth.booksearch.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BookClient {
    private static final String API_BASE_URL = "http://openlibrary.org/";
    private AsyncHttpClient client;

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }


    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("search.json?q=");
            Log.i("query", url + URLEncoder.encode(query, "utf-8"));
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("MYAPP", "exception", e);
        }
    }

    public void getCategory(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl(query+".json?limit=12&offset=12");
            Log.v("category", "subjects/"+query+".json?limit=12&offset=12");
            client.get(url , handler);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("MYAPP", "exception", e);
        }
    }
}
