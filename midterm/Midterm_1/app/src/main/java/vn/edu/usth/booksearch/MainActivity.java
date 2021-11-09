package vn.edu.usth.booksearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import vn.edu.usth.booksearch.net.BookClient;
import vn.edu.usth.booksearch.models.Book;
import vn.edu.usth.booksearch.models.Category;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    BufferedReader reader;

    private TextView mTextViewResult;
    private BookClient client;
    private ProgressBar progress;
    private ArrayList<Book> aBooks;
    private ArrayList<Category> _Cate;
    private boolean search;
    URL url;
    Bitmap bitmap;
    private ImageButton Search;
    private EditText input;
    private ImageView test;

    ListView listView;
    ArrayList<String> mTitle = new ArrayList<String>();
    ArrayList<String> mAuthor = new ArrayList<String>();
    ArrayList<String> mUrls = new ArrayList<String>();
    ArrayList<BitmapDrawable> images = new ArrayList<BitmapDrawable>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Book> aBooks = new ArrayList<Book>();
        ArrayList<Category> _Cate = new ArrayList<Category>();
        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setText("123213");


        List<String> _available_cate = new ArrayList<String>();

        try{
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("subjects")));
            String line = reader.readLine();
            while(line != null){
                _available_cate.add(line);
                line = reader.readLine();
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }

//      Read from File

        for (int i = 0; i < _available_cate.size(); i++) {
            Log.v("READ FROM FILE ",_available_cate.get(i));
        }

//      API TESTING
        search = false;
//        fetchBooks("Harry Potter", search);
//        fetchCategory("subjects/architecture", search);
        input = (EditText) findViewById(R.id.search_input);
        Search = (ImageButton) findViewById(R.id.search_button);
        test = findViewById(R.id.test_image);

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBooks(input.getText().toString(), search);
            }
        });
    }

    public void fetchBooks(String query, boolean search) {
        Bitmap err = BitmapFactory.decodeResource(getResources(),R.drawable.not_found);
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
                        for (Book book : books) {
                            mTitle.add(book.getTitle());
//                            mTitle.add("book.getTitle()");
//                            mAuthor.add("book.getAuthor()");
                            mAuthor.add(book.getAuthor());
                            mUrls.add(book.getCoverUrl());
//                            Log.i("Data",book.getTitle());
                            mTextViewResult.setText(book.toString());
                            //test asyn
//                    test.setImageResource(R.drawable.cloud);
                            images.add(new BitmapDrawable(err));
                        }

                        Log.i("Data",mTitle.toString());
                        Log.i("Data",mAuthor.toString());
                        Log.i("Data",images.toString());

                        String[] aTitle = new String[mTitle.size()];
                        String[] aAuthor = new String[mAuthor.size()];
                        BitmapDrawable[] aImage = new BitmapDrawable[mAuthor.size()];
                        aTitle = mTitle.toArray(aTitle);
                        aAuthor = mAuthor.toArray(aAuthor);
                        aImage = images.toArray(aImage);
                        Log.i("Image", aImage.toString());
                        Log.i("Total", aImage.toString());

                        MyAdapter adapter = new MyAdapter(MainActivity.this, aTitle, aAuthor, aImage);//, images);
                        listView.setAdapter(adapter);

                        int index = 0;
//                        View rowView = listView.getAdapter().getView(index, null, listView);
//                        if(rowView == null){
//                            Log.i("child", "is null");
//                        } else {
//                            Log.i("child", "is not null");
//                            ImageView hImage = rowView.findViewById(R.id.image);
//                            hImage.setImageResource(R.drawable.err);
//                        }
//
//                        ImageView hImage = findViewById(index);
//                        hImage.setImageResource(R.drawable.err);
                        for (String tUrl : mUrls) {
                            View rowView = listView.getAdapter().getView(index, null, listView);
//                            Log.i("Iter", String.valueOf(index));
                            ImageView hImage = rowView.findViewById(R.id.image);
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
                                        url = new URL(tUrl);
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
                                    if(bitmap == null){
//                    test.setImageResource(R.drawable.cloud);
                                        hImage.setImageResource(R.drawable.err);
                                        test.setImageDrawable(new BitmapDrawable(err));
                                        Log.i("Image", "okay set here");
                                    } else {
                                        hImage.setImageDrawable(new BitmapDrawable(bitmap));
                                        test.setImageDrawable(new BitmapDrawable(bitmap));
                                    }
//                                    test.setImageDrawable(new BitmapDrawable(bitmap));
                                    String content = msg.getData().getString("server_response");
                                    Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                                }
                            };
                            task.execute();
                            index++;
                        }


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

    public void fetchCategory(String query, boolean search) {
        this.search = search;
        client = new BookClient();

        client.getCategory(query, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("fetch", query);
                try {
                    JSONArray works = null;
                    if(response != null) {
                        works = response.getJSONArray("works");
                        // Parse json array into array of model objects
                        final ArrayList<Category> _Cate = Category.fromJson(works);
                        // Remove all books from the adapter
                        Log.v("Testing", "Work");
                        for (Category cate : _Cate) {
                            Log.i("Category",cate.getCoverUrl());
                        }
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
    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rAuthor[];
        BitmapDrawable rImgs[];

        MyAdapter (Context c, String title[], String author[], BitmapDrawable Images[]){// ArrayList<BitmapDrawable> imgs) {
            super(c, R.layout.row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rAuthor = author;
            this.rImgs = Images;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myAuthor = row.findViewById(R.id.textView2);

            // now set our resources on views
            images.setImageDrawable(rImgs[position]);
//            images.setTag("ImageSearch"+position);
//            images.setId(position);
            myTitle.setText(rTitle[position]);
            myAuthor.setText(rAuthor[position]);

            return row;
        }

    }

}