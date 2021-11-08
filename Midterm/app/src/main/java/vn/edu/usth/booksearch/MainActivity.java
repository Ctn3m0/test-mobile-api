package vn.edu.usth.booksearch;

import static android.widget.Toast.LENGTH_LONG;

import androidx.appcompat.app.AppCompatActivity;
import vn.edu.usth.booksearch.net.BookClient;
import vn.edu.usth.booksearch.models.Book;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Book> aBooks = new ArrayList<Book>();
        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setText("123213");

        search = false;


        input = (EditText) findViewById(R.id.search_input);
        Search = (ImageButton) findViewById(R.id.search_button);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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