package dangvulinh.tricount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
public class ThirdActivity extends AppCompatActivity {

    EditText edtProjet;
    EditText edtDescription;
    EditText edtParticipant;
    EditText edtPrix;
    EditText edtDate;
    Button btnAdd;
    Button btnCount;
    ListView lvParticipant;

    // double total = 0;
    // double totalperso = 0;
    //   int id =0;

    Database database;

    //mang
    ArrayList<String> arrayParticipant;
    ArrayList<String> arrayPrix;
    ArrayList<String> arraySujet;
    ArrayAdapter adt;

    //vi tri
    //  int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        AnhXa();

        //cho listview
        adt = new ArrayAdapter(ThirdActivity.this,
                android.R.layout.simple_list_item_1,
                arraySujet);

        lvParticipant.setAdapter(adt);




        //dau tien la xu li phan participant
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addParticipant = edtParticipant.getText().toString();
                String addPrix = edtPrix.getText().toString();
                String add = addParticipant + " paid "+ addPrix;
                if (addParticipant.length() == 0 || addPrix.length() == 0) {
                    Toast.makeText(ThirdActivity.this,"Please write something",Toast.LENGTH_SHORT).show();
                } else if(addParticipant.length() > 30) {
                    Toast.makeText(ThirdActivity.this,"Limit of name is 30",Toast.LENGTH_SHORT).show();
                } else {
                    arrayParticipant.add(addParticipant);
                    arrayPrix.add(addPrix);
                    arraySujet.add(add);
                    edtParticipant.setText("");
                    edtPrix.setText("");
                    adt.notifyDataSetChanged();
                }
            }
        });



        //remove participant
        lvParticipant.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDEL(position);
                //   adt.notifyDataSetChanged();
                return false;
            }
        });


        //gio thi xu ly phan date
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickdate();
            }
        });





        //tao database
        database = new Database(this, "listprojet.sqlite", null,1);





        //tiep theo la count
        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String addProjet = edtProjet.getText().toString();
                String addDescription = edtDescription.getText().toString();
                //   String addDate = edtDate.getText().toString();
                if (addProjet.length() == 0 || addDescription.length() == 0) {
                    Toast.makeText(ThirdActivity.this, "Please write something",Toast.LENGTH_SHORT).show();
                } else if (arrayParticipant.isEmpty()) {
                    Toast.makeText(ThirdActivity.this, "Please add participants",Toast.LENGTH_SHORT).show();
                } else if(edtDate.getText().toString().length() == 0 ) {
                    Toast.makeText(ThirdActivity.this,"Please add date",Toast.LENGTH_SHORT).show();
                }
                else if(arrayPrix.size() < 2) {
                    Toast.makeText(ThirdActivity.this,"Why you need to count?",Toast.LENGTH_SHORT).show();
                }
                else {
                    //     id = id+1;
                    //phan chuyen doi Main sang Second
                    Intent intent = new Intent(ThirdActivity.this, SecondActivity.class);
                    intent.putStringArrayListExtra("dataParticipant",arrayParticipant);
                    intent.putStringArrayListExtra("dataPrix",arrayPrix);
                    intent.putStringArrayListExtra("dataSujet", arraySujet);
                    intent.putExtra("dataProjet",edtProjet.getText().toString());
                    intent.putExtra("dataDescription",edtDescription.getText().toString());
                    intent.putExtra("dataDate",edtDate.getText().toString());
                    //    intent.putExtra("dataprojetID",id);

                    startActivity(intent);
                }
            }
        });


    }


    void AnhXa() {
        edtProjet = (EditText) findViewById(R.id.edtprojet);
        edtDescription = (EditText) findViewById(R.id.edtdescription);
        edtParticipant = (EditText) findViewById(R.id.edtparticipant);
        edtPrix = (EditText) findViewById(R.id.edtprix);
        edtDate = (EditText) findViewById(R.id.edtdate);
        btnAdd = (Button) findViewById(R.id.btnadd);
        btnCount = (Button) findViewById(R.id.btncount);
        lvParticipant = (ListView) findViewById(R.id.lvparticipant);
        arrayParticipant = new ArrayList<>();
        arrayPrix = new ArrayList<>();
        arraySujet = new ArrayList<>();
    }

    void pickdate() {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datepicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //gan lich moi vo
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat  simpledateformat = new SimpleDateFormat("dd/MM/yyyy");
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
                //Chuyen sang avtivity 3
                Intent ittMainThird = new Intent(ThirdActivity.this, MainActivity.class);
                startActivity(ittMainThird);
                break;
            case R.id.menuNewProjet:
                //Chuyen sang avtivity 3
                Intent ittto3 = new Intent(ThirdActivity.this, ThirdActivity.class);
                startActivity(ittto3);
                break;
            case R.id.menuSync:
                //Chuyen sang avtivity 5
                Intent ittto5 = new Intent(ThirdActivity.this, FifthActivity.class);
                startActivity(ittto5);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void confirmDEL (final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //   alertDialog.setTitle("Notice!!!");
        alertDialog.setMessage("Do you want to delete ?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                arrayParticipant.remove(pos);
                arrayPrix.remove(pos);
                arraySujet.remove(pos);
                adt.notifyDataSetChanged();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });



        alertDialog.show();
    }

/*
    int testrepeat(ArrayList arrayPrix) {
        int s = 0;
        for (int i = 0; i< arrayPrix.size();i++) {
            if(arrayPrix.get(i).equals(arrayPrix.get(i+1))) {
                s++;
            }
        }
        if(s == arrayPrix.size()-2) {
            return 1;
        } else {
            return 0;
        }


    }
*/
}
