package vn.edu.usth.booksearch;

import static android.widget.Toast.LENGTH_LONG;

import androidx.appcompat.app.AppCompatActivity;
import vn.edu.usth.booksearch.net.BookClient;
import vn.edu.usth.booksearch.models.Book;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import android.util.Log;
import android.nfc.Tag;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    SearchView mySearchView;
    ListView myList;
    private TextView mTextViewResult;

    private BookClient client;
    private ProgressBar progress;
    private ArrayList<Book> aBooks;
    private boolean search;
    private ImageButton Search;
    private EditText input;
    Bitmap bitmap;
    private ImageView test;
    URL url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Book> aBooks = new ArrayList<Book>();
        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setText("123213");

        search = false;
        test = findViewById(R.id.test_image);

        input = (EditText) findViewById(R.id.search_input);
        Search = (ImageButton) findViewById(R.id.search_button);

        AsyncTask<String, Integer, Message> task = new AsyncTask<String, Integer, Message>() {
            @Override
            protected void onPreExecute() {
                // do some preparation here, if needed
            }
            @Override
            protected Message doInBackground(String... params) {
                try {
                    // wait for 5 seconds to simulate a long network access
//                    Thread.sleep(5000);
//                  initialize URL
                    url = new URL("https://images.unsplash.com/photo-1615789591457-74a63395c990?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80");
                    // Make a request to server
                    HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    // allow reading response code and response dataconnection.
                    connection.connect();
                    // Receive response
                    int response = connection.getResponseCode();
                    Log.i("USTHWeather", "The response is: " + response);
                    InputStream is = connection.getInputStream();
                    // Process image response
                    bitmap = BitmapFactory.decodeStream(is);
                    connection.disconnect();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                // Assume that we got our data from server
                Bundle bundle = new Bundle();
                bundle.putString("server_response", "some sample json here");
                // notify main thread
                Message msg = new Message();
                msg.setData(bundle);
                return msg;
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                // This method is called in the main thread, so it's possible
                // to update UI to reflect the worker thread progress here.
                // In a network access task, this should update a progress bar
                // to reflect how many percent of data has been retrieved
//                logo = (ImageView) findViewById(R.id.logo);
//                logo.setImageBitmap(bitmap);

            }
            @Override
            protected void onPostExecute(Message msg) {
//                test.setImageDrawable(new BitmapDrawable(bitmap));
                test.setImageResource(R.drawable.cloud);
                test.setImageDrawable(new BitmapDrawable(bitmap));
                String content = msg.getData().getString("server_response");
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        };

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.execute();
                fetchBooks(input.getText().toString(), search);
                Toast.makeText(MainActivity.this, input.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void fetchBooks(String query, boolean search) {
        this.search = search;
        client = new BookClient();
        ListView listView = (ListView) findViewById(R.id.search_items);

        client.getBooks(query, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("fetch", query);
                try {
                    JSONArray docs = null;
                    if(response != null) {
                        docs = response.getJSONArray("docs");
                        // Parse json array into array of model objects
                        final ArrayList<Book> books = Book.fromJson(docs);
                        // Remove all books from the adapter
                        Log.v("Testing", "Hello");

                        ArrayList<String> books_info = new ArrayList<>();

                        for (Book book : books) {
                            books_info.add(book.getTitle()+" \n"+book.getAuthor());
//                            books_info.add(book.getCoverUrl());
                            Log.i("Data",book.getTitle());
                            Log.i("Image",book.getCoverUrl());
                            mTextViewResult.setText(book.toString());
                        }

                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, books_info);
                        listView.setAdapter(arrayAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR",e.toString());
                }
            }
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("BIG PROBLEM", "FAIL");
            }
        });

    }

}