package jp.techacademy.naoto.sakurai.taskapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;

public class InputCategoryActivity extends AppCompatActivity {

    private Category mCategory;
    private EditText mNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_category);
        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        Button mDoneButton = (Button)findViewById(R.id.ctable_done_button);
        mDoneButton.setOnClickListener(mOnDoneClickListener);
        mNameEdit = (EditText)findViewById(R.id.ctable_name_edit_text);

        Intent intent = getIntent();
        mCategory = (Category) intent.getSerializableExtra(CategoryActivity.EXTRA_CATEGORY);
        if (mCategory != null) { // 更新の場合
            mNameEdit.setText(mCategory.getCategory_name());
        }
    }

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(addCategory()) { //成功した
                finish();
            }
        }
    };

    private boolean addCategory() {
        // これではいけないはず
        // これではいけないはず
        Realm realm = Realm.getDefaultInstance();
        // これではいけないはず
        // これではいけないはず
        
        
        if (mCategory == null) {
            // 新規作成の場合
            mCategory = new Category();
            RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAll();

            int identifier;
            if (categoryRealmResults.max("id") != null) {
                identifier = categoryRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mCategory.setId(identifier);
        }

        String category_name = mNameEdit.getText().toString();
        if (category_name.isEmpty()) {
            // 確認ダイアログの生成
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
            alertDlg.setTitle("エラー");
            alertDlg.setMessage("カテゴリー名が空です！");
            alertDlg.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //OKボタンクリック処理
                        }
                    });
            // 表示
            alertDlg.create().show();
            return  false;
        }
        mCategory.setCategory_name(category_name);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(mCategory);
        realm.commitTransaction();

        realm.close();
        return true;
    }


}


