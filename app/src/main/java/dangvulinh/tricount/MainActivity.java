package dangvulinh.tricount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



public class MainActivity extends AppCompatActivity {

    ListView lvprojet;
    ArrayList<ListProjet> arrayListProjet;
    ListProjetAdapter adapter;

    Database database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();



        adapter = new ListProjetAdapter(this,R.layout.list_projet,arrayListProjet);
        lvprojet.setAdapter(adapter);

        database = new Database(this, "listprojet.sqlite", null,1);


        //create table for list projet
        database.QueryData("CREATE TABLE IF NOT EXISTS listprojet(id INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "list VARCHAR(200)," +
                "description VARCHAR(200), " +
                "date VARCHAR(200), " +
                "total DECIMAL(10,2), " +
                "totalperso DECIMAL(10,2)" +
                " ) ");

        //tao table projet
        database.QueryData("CREATE TABLE IF NOT EXISTS projet( projetID INT, " +
                "participants VARCHAR(200), " +
                "paid DECIMAL(10,2), " +
                "count DECIMAL(10,2), " +
                "sujet VARCHAR(200), " +
                "result VARCHAR(200) " +
                ")");



        GetDataListProjet();

    }

    void AnhXa() {
        lvprojet = (ListView) findViewById(R.id.lvProjet);
        arrayListProjet = new ArrayList<>();
    }

    void  GetDataListProjet() {
        //get list projet
        Cursor dataprojet = database.GetData("SELECT * FROM listprojet");
        while (dataprojet.moveToNext()) {
            String projet = dataprojet.getString(1);
            String desc = dataprojet.getString(2);
            String date = dataprojet.getString(3);
            int id = dataprojet.getInt(0);
            arrayListProjet.add(new ListProjet(projet,desc,date,id,R.drawable.getin,R.drawable.modify));
        }
        adapter.notifyDataSetChanged();
    }

    //Go to Main 4
    void gotoResult(int id) {
        Intent itt3to4 = new Intent(MainActivity.this,FourhActivity.class);
        itt3to4.putExtra("dataID",id);
        startActivity(itt3to4);
    }

    //rest in Main 1, change name projet, description, date if we want
    void dialogmodify(final int id) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_modnameprojet);

        //Khai bao Anh Xa
        final EditText modProjet = (EditText) dialog.findViewById(R.id.edtModProjet);
        final EditText modDesc = (EditText) dialog.findViewById(R.id.edtModDesc);
        final EditText modDate = (EditText) dialog.findViewById(R.id.edtModDate);
        Button btncancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnDModify = (Button) dialog.findViewById(R.id.btnMod);

        modDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickdate(modDate);
            }
        });

        btnDModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newProjet = modProjet.getText().toString().trim();
                String newDesc = modDesc.getText().toString().trim();
                String newDate = modDate.getText().toString().trim();
            //update listprojet SET list="ahihi" where id=1

                if(newProjet.length() != 0) {
                    database.QueryData("UPDATE listprojet SET list='" +
                            newProjet + "' WHERE id=" + id + "");
                }

                if(newDesc.length() != 0) {
                    database.QueryData("UPDATE listprojet SET description='" +
                            newDesc + "' WHERE id=" + id + "");
                }

                if(newDate.length() != 0) {
                    database.QueryData("UPDATE listprojet SET date='" +
                            newDate + "' WHERE id=" + id + "");
                }

                dialog.dismiss();
                GetDataListProjet();

                Intent itt1 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(itt1);

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void pickdate(final EditText edtDate) {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datepicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //gan lich moi vo
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpledateformat = new SimpleDateFormat("dd/MM/yyyy");
                edtDate.setText(simpledateformat.format(calendar.getTime()));
            }
        },year,month,date);
        datepicker.show();
    }

    //them menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_demo,menu);

        return super.onCreateOptionsMenu(menu);
    }

    //bat su kien trong menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menulistProjet:
                //   Toast.makeText(this, "Chon List", Toast.LENGTH_SHORT).show();
                //Chuyen sang avtivity 1
               Intent ttt1 = new Intent(MainActivity.this, MainActivity.class);
               startActivity(ttt1);
                break;
            case R.id.menuNewProjet:
                //Chuyen sang avtivity 3
                Intent ittSndMain = new Intent(MainActivity.this, ThirdActivity.class);
                startActivity(ittSndMain);
                break;
            case R.id.menuSync:
                //Chuyen sang avtivity 5
                Intent ittto5 = new Intent(MainActivity.this, FifthActivity.class);
                startActivity(ittto5);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
