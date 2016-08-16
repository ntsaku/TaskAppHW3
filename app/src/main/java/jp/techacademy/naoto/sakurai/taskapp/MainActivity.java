package jp.techacademy.naoto.sakurai.taskapp;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "jp.techacademy.naoto.sakurai.taskapp.TASK";

    private Spinner mSpinner;

//    Taskクラスのオブジェクトを管理する realm
    private Realm mRealm;
    private RealmResults<Task> mTaskRealmResults;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            reloadListView();
        }
    };

//    Categoryクラスのオブジェクトを管理する realm
    private Realm categoryRealm;
    private RealmResults<Category> mCategoryRealmResults;
    private RealmChangeListener categoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            resetSpinner();
        }
    };


    private ListView mListView;
    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        // Realmの設定 これで良いのか理解していない。
        categoryRealm = Realm.getDefaultInstance();
        mCategoryRealmResults = categoryRealm.where(Category.class).findAll();
//        mCategoryRealmResults.sort("id", Sort.DESCENDING);NG);
        categoryRealm.addChangeListener(categoryRealmListener);

        //カテゴリー spinnerに設定
        //先頭は""を設定して、これを選択すると全選択できるようにする。
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        adapter.add("");
        for (int i = 0; i < mCategoryRealmResults.size(); i++){
            adapter.add(mCategoryRealmResults.get(i).getCategory_name());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) findViewById(R.id.category_search_text);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String item = (String)spinner.getSelectedItem();
                if (item.isEmpty()) {  /*検索条件が空なら全検索*/
                    mTaskRealmResults = mRealm.where(Task.class).findAll();
                } else {
                    mTaskRealmResults = mRealm.where(Task.class).equalTo("category_name", item).findAll();
                }
                mTaskRealmResults.sort("date", Sort.DESCENDING);

                reloadListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        // task クラスのオブジェクトを管理する Realmの設定
        mRealm = Realm.getDefaultInstance();
        mTaskRealmResults = mRealm.where(Task.class).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);



        // ListViewの設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);

        // ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task);

                startActivity(intent);
            }
        });



        // ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する

                final Task task = (Task) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.clear();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        reloadListView();
    }

    private void reloadListView() {

        ArrayList<Task> taskArrayList = new ArrayList<>();

        for (int i = 0; i < mTaskRealmResults.size(); i++) {
            Task task = new Task();

            task.setId(mTaskRealmResults.get(i).getId());
            task.setTitle(mTaskRealmResults.get(i).getTitle());
            task.setContents(mTaskRealmResults.get(i).getContents());
            task.setCategory_id(mTaskRealmResults.get(i).getCategory_id());
            task.setCategory_name(mTaskRealmResults.get(i).getCategory_name());
            task.setDate(mTaskRealmResults.get(i).getDate());

            taskArrayList.add(task);
        }

        mTaskAdapter.setTaskArrayList(taskArrayList);
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    private void resetSpinner() {
        //カテゴリー spinnerを再設定
        //先頭は""を設定して、これを選択すると全選択できるようにする。
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        adapter.add("");
        for (int i = 0; i < mCategoryRealmResults.size(); i++){
            adapter.add(mCategoryRealmResults.get(i).getCategory_name());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) findViewById(R.id.category_search_text);
        mSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();

    }


}