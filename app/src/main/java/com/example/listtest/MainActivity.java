package com.example.listtest;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.support.v7.widget.Toolbar;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    private final String dbName = "items";
    private final String tableName = "person";

    private String names[];
    {
        names = new String[]{"Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Donut", "Eclair", "Froyo", "Gingerbread", "Donut", "Eclair", "Froyo", "Gingerbread", "Donut", "Eclair", "Froyo", "Gingerbread", "Eclair", "Froyo", "Gingerbread"};
    }

    private final String prices[];
    {
        prices = new String[]{"1000", "2000", "4000", "5000", "15000", "2000", "4000", "5000", "15000", "2000", "4000", "5000", "15000", "2000", "4000", "5000", "15000", "4000", "5000", "15000"};
    }

    ArrayList<HashMap<String, String>> shopplist;
    ListView list;
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE ="price";

    SQLiteDatabase sampleDB = null;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (ListView) findViewById(R.id.listview1);
        shopplist = new ArrayList<HashMap<String, String>>();

        try{
            sampleDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "+ tableName + "(name VARCHAR(20), price VARCHAR(20));");
            sampleDB.execSQL("DELETE FROM " + tableName);
            for(int i=0; i<names.length; i++ ) {
                sampleDB.execSQL("INSERT INTO " + tableName
                        + " (name, price)  Values ('" + names[i] + "', '" + prices[i]+"');");
            }
            sampleDB.close();
        }catch (SQLiteException se){
            Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("", se.getMessage());
        }
        showList();


/*
        final ListView listview ;
        final CustomChoiceListViewAdapter adapters;


        //데이터를 저장하게 되는 리스트
        final List<String> items = new ArrayList<String>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        adapters = new CustomChoiceListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(adapters);

        Button addButton = (Button)findViewById(R.id.add) ;
        Button deleteButton = (Button)findViewById(R.id.delete) ;
        final EditText edit = (EditText)findViewById(R.id.edt);
        final EditText price = (EditText)findViewById(R.id.price);

        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                //입력된 문자열 얻어오기
                String str = edit.getText().toString();
                // 아이템 추가.
                adapters.addItem(str) ;

                // listview 갱신
                adapters.notifyDataSetChanged();

                //입력된 값 지우기
                edit.setText("");
            }
        }) ;

        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count ;
                count = adapters.getCount() ;

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        items.remove(i) ;
                    }
                }
                // 모든 선택 상태 초기화.
                listview.clearChoices();
                adapters.notifyDataSetChanged();

            }
        }) ;
*/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).initiateScan();

            }
            
        });
        
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        final ListView listview ;
        final CustomChoiceListViewAdapter adapters;


        //데이터를 저장하게 되는 리스트
        final List<String> items = new ArrayList<String>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        adapters = new CustomChoiceListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(adapters);

        super.onActivityResult(requestCode, resultCode, data);

        // QR코드/ 바코드를 스캔한 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        try {
            //data를 json으로 변환
            JSONObject obj = new JSONObject(result.getContents());
            adapters.addItem(obj.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // result.getFormatName() : 바코드 종류

        // mTextMessage.setText( result.getContents() );
    }
    protected void showList(){
        try {

            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);


            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName, null);

            if (c != null) {


                if (c.moveToFirst()) {
                    do {

                        //테이블에서 두개의 컬럼값을 가져와서
                        String Name = c.getString(c.getColumnIndex("name"));
                        String Price = c.getString(c.getColumnIndex("price"));

                        //HashMap에 넣습니다.
                        HashMap<String,String> persons = new HashMap<String,String>();

                        persons.put(TAG_NAME,Name);
                        persons.put(TAG_PRICE,Price);

                        //ArrayList에 추가합니다..
                        shopplist.add(persons);

                    } while (c.moveToNext());
                }
            }

            ReadDB.close();


            //새로운 apapter를 생성하여 데이터를 넣은 후..
            adapter = new SimpleAdapter(
                    this, shopplist, R.layout.listview_item,
                    new String[]{TAG_NAME,TAG_PRICE},
                    new int[]{ R.id.textView1, R.id.price}
            );


            //화면에 보여주기 위해 Listview에 연결합니다.
            list.setAdapter(adapter);


        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }
}
