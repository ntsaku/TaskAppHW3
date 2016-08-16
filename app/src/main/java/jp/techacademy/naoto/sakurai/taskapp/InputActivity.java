package jp.techacademy.naoto.sakurai.taskapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class InputActivity extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button mDateButton, mTimeButton, mCRegButton;
    private EditText mTitleEdit, mContentEdit;
    private Spinner mSpinner;


    private Task mTask;
    private Category mCategory;
    public ArrayList<Category> categoryList;

    //    Categoryクラスのオブジェクトを管理する realm
    private Realm categoryRealm;
    private RealmResults<Category> mCategoryRealmResults;
    private RealmChangeListener categoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            resetSpinner();
        }
    };

    private View.OnClickListener mOnDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(InputActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            String dateString = mYear + "/" + String.format("%02d", (mMonth + 1)) + "/" + String.format("%02d", mDay);
                            mDateButton.setText(dateString);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    };

    private View.OnClickListener mOnTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
                            mTimeButton.setText(timeString);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    };

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTask();
            finish();
        }
    };

    private View.OnClickListener mOnCregClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // カテゴリーを入力する画面に遷移させる
            Intent cintent = new Intent(InputActivity.this, CategoryActivity.class);

            startActivity(cintent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Intent intent = getIntent();
        mTask = (Task) intent.getSerializableExtra(MainActivity.EXTRA_TASK);


        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        mDateButton = (Button) findViewById(R.id.date_button);
        mDateButton.setOnClickListener(mOnDateClickListener);
        mTimeButton = (Button) findViewById(R.id.times_button);
        mTimeButton.setOnClickListener(mOnTimeClickListener);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        mCRegButton = (Button) findViewById(R.id.category_reg_button);
        mCRegButton.setOnClickListener(mOnCregClickListener);

        mTitleEdit = (EditText) findViewById(R.id.title_edit_text);
        mContentEdit = (EditText) findViewById(R.id.content_edit_text);

        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        categoryRealm = Realm.getDefaultInstance();
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない
        // Realmの設定 このままではいけないがどう修正すべきかわからない

        mCategoryRealmResults = categoryRealm.where(Category.class).findAll();
//        mCategoryRealmResults.sort("id", Sort.DESCENDING);NG);
        categoryRealm.addChangeListener(categoryRealmListener);

        // categoryListリストに設定されているカテゴリーを spinnerに設定
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        for (int i = 0; i < mCategoryRealmResults.size(); i++) {
            adapter.add(mCategoryRealmResults.get(i).getCategory_name());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner = (Spinner) findViewById(R.id.category_edit_text);
        mSpinner.setAdapter(adapter);


        if (mTask == null) {
            // 新規作成の場合
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        } else {
            // 更新の場合
            mTitleEdit.setText(mTask.getTitle());
            mContentEdit.setText(mTask.getContents());

            //mTaskに設定されているタスク名に該当するタスクがspinnerにあればそれを表示
           // 参考  http://anadreline.blogspot.jp/2013/07/spinner.html
            String category_name = mTask.getCategory_name();
            SpinnerAdapter sadapter = mSpinner.getAdapter();
            int index = 0;
            boolean cfound = false;
            for (int i = 0; i < sadapter.getCount(); i++) {
                if (sadapter.getItem(i).equals(category_name)) {
                    index = i;
                    cfound = true;
                    break;
                }
            }
            if(cfound) {  //カテゴリーが見つかった
                mSpinner.setSelection(index);
            } else {
                mSpinner.setSelection(0);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTask.getDate());
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            String dateString = mYear + "/" + String.format("%02d", (mMonth + 1)) + "/" + String.format("%02d", mDay);
            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
            mDateButton.setText(dateString);
            mTimeButton.setText(timeString);
        }
    }

    private void addTask() {
        Realm realm = Realm.getDefaultInstance();

        if (mTask == null) {
            // 新規作成の場合
            mTask = new Task();
            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {
                identifier = taskRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mTask.setId(identifier);
        }

        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();
        String category_name = (String)mSpinner.getSelectedItem();
        if (category_name == null || category_name.isEmpty()) {
            // 確認ダイアログの生成
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(InputActivity.this);
            alertDlg.setTitle("エラー");
            alertDlg.setMessage("カテゴリーが指定されていません！");
            alertDlg.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //OKボタンクリック処理

                        }
                    });
            // 表示
            alertDlg.create().show();
            return;
        }

        mTask.setTitle(title);
        mTask.setContents(content);
        mTask.setCategory_name(category_name);
        GregorianCalendar calendar = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute);
        Date date = calendar.getTime();
        mTask.setDate(date);


        realm.beginTransaction();
        realm.copyToRealmOrUpdate(mTask);
        realm.commitTransaction();

        realm.close();

        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
        resultIntent.putExtra(MainActivity.EXTRA_TASK, mTask);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                this,
                mTask.getId(),
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), resultPendingIntent);
    }

    private void resetSpinner() {
        //カテゴリー spinnerを再設定
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        for (int i = 0; i < mCategoryRealmResults.size(); i++){
            adapter.add(mCategoryRealmResults.get(i).getCategory_name());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner = (Spinner) findViewById(R.id.category_edit_text);
        mSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}