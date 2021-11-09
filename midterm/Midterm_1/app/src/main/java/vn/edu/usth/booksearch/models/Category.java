package vn.edu.usth.booksearch.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Category implements Parcelable {
    private String CoverKey;
    private String title;
    private String author;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverUrl() {
        return "https://covers.openlibrary.org/b/olid/" + CoverKey + "-L.jpg?default=false";
    }

    public static Category fromJson(JSONObject jsonObject) {
        Category _cate = new Category();
        try {
            if (jsonObject.has("cover_edition_key")) {
                _cate.CoverKey = jsonObject.getString("cover_edition_key");
            } else if(jsonObject.has("edition_key")) {
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                _cate.CoverKey = ids.getString(0);
            }
            _cate.title = jsonObject.has("title") ? jsonObject.getString("title") : "";
            _cate.author = getAuthor(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return _cate;
    }

    private static String getAuthor(final JSONObject jsonObject) {
        try {
            final JSONArray authors = jsonObject.getJSONArray("authors");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "";
        }
    }

    public static ArrayList<Category> fromJson(JSONArray jsonArray) {
        ArrayList<Category> categorys = new ArrayList<Category>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject categoryJson = null;
            try {
                categoryJson = jsonArray.getJSONObject(i);
                Log.i("JSON Object",categoryJson.toString());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Category book = Category.fromJson(categoryJson);
            if (book != null) {
                categorys.add(book);
            }
        }
        return categorys;
    }

    public Category() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.title);
    }

    private Category(Parcel in) {
        this.author = in.readString();
        this.title = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
