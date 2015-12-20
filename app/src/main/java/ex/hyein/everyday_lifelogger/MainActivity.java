package ex.hyein.everyday_lifelogger;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {

    // Database 관련 객체들
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    String s="";
    int dbMode = Context.MODE_PRIVATE;//이패키지에서만 디비 사용하겟다.

    /*버튼*/
    Button bt_plus; //할 일 추가 버튼
    Button bt_record; // 기록한거 보기 버튼


    TextView text;

    /*지도 관련 변수들*/
    private GoogleMap map;
    static final LatLng SEOUL = new LatLng(37.56, 126.97);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        //현재 위치로 가는 버튼 표시
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));//초기 위치...수정필요

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                String msg = "lon: " + location.getLongitude() + " -- lat: " + location.getLatitude();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                drawMarker(location);

            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(

                getApplicationContext(), locationResult);

        // // Database 생성 및 열기
        db = openOrCreateDatabase(dbName, dbMode, null);

        // 테이블 생성
        createTable();

        bt_plus = (Button) findViewById(R.id.plus);
        bt_record = (Button) findViewById(R.id.my_life);
        /*My life record 버튼 누를 때 실행할 화면*/
        bt_record.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent(MainActivity.this, result.class);
                                             startActivity(intent);//새로운 화면에서 결과를 보여주도록 한다.

                                         }
                                     });
    }

    // Table 생성 함수
    public void createTable() {
        try {
            String eat = "create table " + tableName + "(id integer/*int 형*/ primary key autoincrement, " + "name text not null)";//not null- 꼭 입력을하라-입력안하면 오류
            db.execSQL(eat);//delete insert 등과같은거 인자로 넣으면 기능 가능

        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite", "error: " + e);
        }
    }

    // Data 추가 함수
    public void insertData(String name) {
        String sql = "insert into " + tableName + " values(NULL, '" + name + "');";//null 이면 autoincrement이므로 자동증가
        db.execSQL(sql);
    }

    /*현재위치 나타내는 마커*/
    private void drawMarker(Location location) {

        //기존 마커 지우기
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                .title("지금 위치 입니다.^^ "));
    }
    /* 할 일 추가하는 버튼 리스너*/
    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.plus://할 일 추가(데이터 추가)

                //Dialog에서 보여줄 입력화면 View 객체 생성 작업
                //Layout xml 리소스 파일을 View 객체로 부풀려 주는(inflate) LayoutInflater 객체 생성
                LayoutInflater inflater = getLayoutInflater();

                //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언
                final View dialogView = inflater.inflate(R.layout.add_doing, null);

                //한 일의 카테고리 설정 Dialog 생성 및 보이기
                AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
                buider.setTitle("What are you doing?"); //Dialog 제목
                buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

                buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    //Dialog에 "확인"라는 타이틀의 버튼을 설정
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //멤버 정보의 입력을 완료하고 TextView에 추가 하도록 하는 작업 수행

                        //dialogView 객체 안에서 NAME을 입력받는 EditText 객체 찾아오기(주의: dialaogView에서 find 해야함)
                        EditText edit_doing = (EditText) dialogView.findViewById(R.id.dialog_write);

                        //dialogView 객체 안에서 카테고리를 입력받는 RadioGroup 객체 찾아오기
                        RadioGroup rg = (RadioGroup) dialogView.findViewById(R.id.dialog_rg);

                        //EditText에 입력된 메모 얻어오기
                        String memo = edit_doing.getText().toString();

                        //선택된 RadioButton의 ID를 RadioGroup에게 얻어오기
                        int checkedId = rg.getCheckedRadioButtonId();

                        //Check 된 RadioButton의 ID로 라디오버튼 객체 찾아오기
                        RadioButton rb = (RadioButton) rg.findViewById(checkedId);

                        String category = rb.getText().toString();//RadionButton의 Text 얻어오기

                        String s = category + "            "  + memo +  " "+"\n";// 데이터베이스에 넣을 string( 카테고리+ 한일)

                        insertData(s);//  한 일과 카테고리 추가

                        //특정카테고리에 추가작업을 완료 하였기에 '완료'했다는 메세지를 Toast로 출력
                        Toast.makeText(MainActivity.this, category +"카테고리 추가 완료!", Toast.LENGTH_SHORT).show();
                    }
                });
                buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    //Dialog에 "취소"이라는 타이틀의 버튼을 설정

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //한 일을 입력하지 않고 다시 메인으로 돌아가기위해
                        //취소하였기에 특별한 작업은 없고 '취소'했다는 메세지만 Toast로 출력
                        Toast.makeText(MainActivity.this, "한 일 추가를 취소합니다", Toast.LENGTH_SHORT).show();
                    }
                });

                //설정한 값으로 AlertDialog 객체 생성
                AlertDialog dialog = buider.create();

                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정

                //Dialog 보이기
                dialog.show();

                break;
        }//switch

    }//mOnClickMethod

}




