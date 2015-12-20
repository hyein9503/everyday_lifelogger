package ex.hyein.everyday_lifelogger;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/*My life record 버튼을 눌렀을 때 보여지는 화면의 클래스*/

public class result extends Activity {
//db객체
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;


    ArrayAdapter<String> baseAdapter;   //어댑터
    ArrayList<String> memoList;//스트링 배열

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        ListView mList = (ListView) findViewById(R.id.list_view);// 결과를 listview로 보여주기 위함

        db = openOrCreateDatabase(dbName, dbMode, null);//db 생성

    /* database에 저장된 list를 listview에 연결*/
        memoList = new ArrayList<String>();
        baseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, memoList);
        mList.setAdapter(baseAdapter);

        memoList.clear();//클리어 안하면 계속 중복되서 계속 출력됨!!
        selectAll();
        baseAdapter.notifyDataSetChanged();

    }
    //모든 메모리스트 가져오는 함수
    public  void selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);//
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);

            Log.d("lab_sqlite", "index= " + id + " name=" + name);//log.d :안드로이드시 프린트 에프 같은것//실행창에 띄워줌

            memoList.add(name);//네임 어레이리스트에 추가추가추가!!!
            results.moveToNext();
        }
        results.close();
    }

}