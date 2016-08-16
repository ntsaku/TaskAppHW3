package jp.techacademy.naoto.sakurai.taskapp;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class CategoryActivity extends AppCompatActivity{
  
    public final static String EXTRA_CATEGORY = "jp.techacademy.naoto.sakurai.taskapp.CATEGORY";

    private Realm categoryRealm;
    private RealmResults<Category> mCategoryRealmResults;
    private RealmChangeListener categoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            reloadCategoryListView();
        }
    };

    private ListView mCategoryListView;
    private CategoryAdapter mCategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcategory);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton cfab = (FloatingActionButton) findViewById(R.id.cfab);
        cfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, InputCategoryActivity.class);
                startActivity(intent);
            }
        });

        
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        categoryRealm = Realm.getDefaultInstance();
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない

        mCategoryRealmResults = categoryRealm.where(Category.class).findAll();
//        mCategoryRealmResults.sort("id", Sort.DESCENDING);
        categoryRealm.addChangeListener(categoryRealmListener);



        // ListViewの設定
        mCategoryAdapter = new CategoryAdapter(CategoryActivity.this);
        mCategoryListView = (ListView) findViewById(R.id.listView2);

        // ListViewをタップしたときの処理
        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Category category = (Category) parent.getAdapter().getItem(position);

                Intent intent = new Intent(CategoryActivity.this, InputCategoryActivity.class);
                intent.putExtra(EXTRA_CATEGORY, category);

                startActivity(intent);
            }
        });



        // ListViewを長押ししたときの処理
        mCategoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する

                final Category fcategory = (Category) parent.getAdapter().getItem(position);
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Task> tresults = realm.where(Task.class).equalTo("category_name", fcategory.getCategory_name()).findAll();
                if (!tresults.isEmpty()) {
                    //削除しようとしているカテゴリーを設定しているタスクが存在する。
                    AlertDialog.Builder ebuilder = new AlertDialog.Builder(CategoryActivity.this);
                    ebuilder.setTitle("エラー");
                    ebuilder.setMessage(fcategory.getCategory_name() + "を利用しているタスクがあるので削除できません。");
                    ebuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = ebuilder.create();
                    dialog.show();
                    return false;
                }

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                builder.setTitle("削除");
                builder.setMessage(fcategory.getCategory_name() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Category> results = categoryRealm.where(Category.class).equalTo("id", fcategory.getId()).findAll();

                        categoryRealm.beginTransaction();
                        results.clear();
                        categoryRealm.commitTransaction();

                        reloadCategoryListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        reloadCategoryListView();
    }

    private void reloadCategoryListView() {

        ArrayList<Category> categoryArrayList = new ArrayList<>();

        for (int i = 0; i < mCategoryRealmResults.size(); i++) {
            Category category = new Category();

            category.setId(mCategoryRealmResults.get(i).getId());
            category.setCategory_name(mCategoryRealmResults.get(i).getCategory_name());

            categoryArrayList.add(category);
        }

        mCategoryAdapter.setCategoryArrayList(categoryArrayList);
        mCategoryListView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        categoryRealm.close();

    }

}
