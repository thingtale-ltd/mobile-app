package com.thingtale.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.thingtale.mobile_app.content.Database;
import com.thingtale.mobile_app.content.QRCodeData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ContentCreatorActivity extends AppCompatActivity implements ContentListAdapter.ContentAdapterListener {
    private static final String TAG = ContentCreatorActivity.class.getSimpleName();

    private List<QRCodeData> contentList;
    private RecyclerView recyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.actionbar_contentlist, menu);

        final MenuItem addItem = menu.findItem(R.id.add);
        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(ContentCreatorActivity.this, ContentEditActivity.class);
                startActivity(intent);

                return false;
            }
        });

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Pattern pattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

                List<QRCodeData> l = new ArrayList<>();
                for (QRCodeData c : contentList) {
                    Boolean match = false;

                    match |= pattern.matcher(c.getIsbn()).find();
                    match |= pattern.matcher(c.getBookName()).find();
                    match |= pattern.matcher(c.getAuthor()).find();
                    match |= pattern.matcher(c.getLanguage()).find();
                    match |= pattern.matcher(c.getAudioFile()).find();
                    match |= pattern.matcher(Integer.toString(c.getPageNum())).find();
                    match |= pattern.matcher(Integer.toString(c.getLevel())).find();

                    if (match) {
                        l.add(c);
                    }
                }
                ((ContentListAdapter) recyclerView.getAdapter()).setContentList(l);

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_creator);

        //command = getIntent().getStringExtra(MainActivity.COMMAND);

        //contentList = new QRCodeData[]{
        //        new QRCodeData("isbn-foo", "bookName-foo", "author-foo", "language-foo", "audiofile-foo.mp3", 1, 1),
        //        new QRCodeData("isbn-bar", "bookName-bar", "author-bar", "language-bar", "audiofile-bar.mp3", 3, 2),
        //        new QRCodeData("isbn-baz", "bookName-baz", "author-baz", "language-baz", "audiofile-baz.mp3", 5, 3)
        //};
        contentList = Database.load(getApplicationContext());

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new ContentListAdapter(contentList, this, getApplicationContext()));
    }

    @Override
    public void onContentSelected(QRCodeData content) {
        Intent intent = new Intent(ContentCreatorActivity.this, ContentEditActivity.class);

        // find id in global list (no just search result)
        int content_idx = -1;
        for (int i = 0; i < contentList.size(); i++) {
            if (contentList.get(i) == content) {
                content_idx = i;
            }
        }

        intent.putExtra("content_idx", content_idx);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) { // if edit activity returned
            contentList = Database.load(getApplicationContext());
            ((ContentListAdapter) recyclerView.getAdapter()).setContentList(contentList);
        }
    }
}