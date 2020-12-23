package dangvulinh.tricount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    ListView lvRes;
    ListView lvSuj;
    TextView txtProjet;
    TextView txtDescription;
    TextView txtDate;
    TextView txtTotal;
    TextView txtTotalPerso;
    Button btndel;

    Database database;

    double total = 0;
    double totalperso = 0;
    int projetid = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        AnhXa();

        //create database first
        database = new Database(this, "listprojet.sqlite", null,1);


        //nhap du lieu tu intent 1
        final Intent intent = getIntent();
        ArrayList arrayParticipant = intent.getStringArrayListExtra("dataParticipant");
        ArrayList arrayPrix = intent.getStringArrayListExtra("dataPrix");
        final ArrayList arraySujet = intent.getStringArrayListExtra("dataSujet");
        final String edtProjet = intent.getStringExtra("dataProjet");
        final String edtDescription = intent.getStringExtra("dataDescription");
        String edtDate = intent.getStringExtra("dataDate");
     //   final int projetid = intent.getIntExtra("dataprojetID",1234);


        //doi arraylist sang double[]
        final Double[] arrayPrix2 = new Double[arrayPrix.size()];
        for (int i = 0; i < arrayPrix.size(); i++) {
            arrayPrix2[i] = Double.parseDouble(arrayPrix.get(i)+"");
        }

        //total of array
        total = sumArray(arrayPrix2);

        //total par person
        totalperso = total/arrayPrix2.length;
        totalperso = Math.ceil(totalperso*100.)/100.;

        //new array after counting
        final Double[] arraychacun = ArrayPerso(total, arrayPrix2);

        //check last position negative
        int lastposnegative = lastposneg(arraychacun);

        //take the result
        final String[] res = arrayResult(lastposnegative, arraychacun, arrayParticipant);

        insertlistprojet(edtProjet,edtDescription,edtDate,total,totalperso);

        //get data ID
        Cursor dataprojet = database.GetData("SELECT * FROM listprojet WHERE list='" +
                edtProjet + "' AND description='" +
                edtDescription + "' AND date='" +
                edtDate + "' AND total=" +
                total + " AND totalperso=" +
                totalperso + " ");
        while (dataprojet.moveToNext()) {
            projetid = dataprojet.getInt(0);
        }


        assert arrayParticipant != null;
        insertprojet(projetid,arrayParticipant,arrayPrix2,arraychacun,arraySujet,res);

        //cho listview Result
        final ArrayAdapter adtResult = new ArrayAdapter(SecondActivity.this,
                android.R.layout.simple_list_item_1,
                res);

        lvRes.setAdapter(adtResult);

        //cho listview Sujet
        final ArrayAdapter adtSujet = new ArrayAdapter(SecondActivity.this,
                android.R.layout.simple_list_item_1,
                arraySujet);

        lvSuj.setAdapter(adtSujet);


        //giai quyet cac phan txt
        txtProjet.setText(edtProjet);
        txtDescription.setText(edtDescription);
        txtDate.setText(edtDate);
        txtTotal.setText("Total : " + total);
        txtTotalPerso.setText("Each Person : " + totalperso);


        //delete by change second to main
        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.QueryData("DELETE FROM listprojet WHERE id=" +
                        projetid +
                        "");

                database.QueryData("DELETE FROM projet WHERE projetID=" +
                        projetid +
                        "");

                Toast.makeText(SecondActivity.this, "Delete is completed", Toast.LENGTH_SHORT).show();

                Intent ittSndMain = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(ittSndMain);
            }
        });

    }

    void AnhXa() {
       lvRes = (ListView) findViewById(R.id.lvResult);
       lvSuj = (ListView) findViewById(R.id.lvSujet);
       txtProjet = (TextView) findViewById(R.id.tvProjet);
       txtDescription = (TextView) findViewById(R.id.tvDescription);
       txtDate = (TextView) findViewById(R.id.tvDate);
       txtTotal = (TextView) findViewById(R.id.tvTotal);
       txtTotalPerso = (TextView) findViewById(R.id.tvTotalPerso);
       btndel = (Button) findViewById(R.id.btnDel);
    }

    void insertlistprojet(String projet, String desc, String date, double total, double totalperso) {
        //them phan tu vao list projet
        database.QueryData("INSERT INTO listprojet VALUES (null,'" +
                projet + "','" +
                desc + "','" +
                date + "'," +
                total + "," +
                totalperso + ")");
    }


    void insertprojet (int id, ArrayList arrayParticipant, Double[] arrayPrix2, Double[] arraychacun, ArrayList arraySujet, String[] res){
        //them phan tu vao projet, projet ID se giai quyet sau
        for (int i = 0; i<arrayParticipant.size();i++) {
            database.QueryData("INSERT INTO projet VALUES (" +
                    id + "," +
                    "'" + arrayParticipant.get(i) + "'," +
                    arrayPrix2[i] + "," +
                    arraychacun[i] + "," +
                    "'" + arraySujet.get(i) + "'," +
                    "'" + res[i] + "'" +
                    ")");
        }
    }


    // ham tinh tong cua Array
    double sumArray(Double[] d) {
        double sum = 0;
        for (int i = 0; i< d.length; i++) {
            sum = sum + d[i];
        }
        return sum;
    }

    // new Array apres counting, lam tron double
    Double[] ArrayPerso(double total, Double[] ArrayPrix2) {
        double totalperso = total/ArrayPrix2.length;
        totalperso = Math.ceil(totalperso*100.)/100.;
        Double[] newarray = new Double[ArrayPrix2.length];
        for (int i = 0; i< ArrayPrix2.length; i++) {
            newarray[i] = ArrayPrix2[i] - totalperso;
        }
        return  newarray;
    }

    //trouver la derniere negative
    int lastposneg (Double[] array) {
        int pos = 0;
        for (int i = 0; i< array.length; i++) {
            if (array[i] < 0) {
                pos = i;
            }
        }
        return pos;
    }

    //array for the result
    String[] arrayResult(int lastposneg, Double[] arrayPerso, ArrayList arrayParticipant) {
        String[] result = new String[arrayPerso.length];
        for (int i = 0; i < arrayPerso.length; i++) {
            if (i == lastposneg) {
                result[i] = arrayParticipant.get(i) + " is the key to balance ";
            } else {
                if (arrayPerso[i] == 0) {
                    result[i] = arrayParticipant.get(i) + " don't have to pay";
                } else if (arrayPerso[i] > 0) {
                    result[i] = arrayParticipant.get(lastposneg) + " have to pay " + arrayPerso[i] + " to " + arrayParticipant.get(i);
                } else {
                    result[i] = arrayParticipant.get(i) + " have to pay " + makeroundneg(arrayPerso[i]) + " to " + arrayParticipant.get(lastposneg);
                }
            }
        }

        return  result;
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
                Intent ittSndThird = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(ittSndThird);
                break;
            case R.id.menuNewProjet:
                //Chuyen sang avtivity 1
                Intent ittSndMain = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(ittSndMain);
                break;
            case R.id.menuSync:
                //Chuyen sang avtivity 5
                Intent ittto5 = new Intent(SecondActivity.this, FifthActivity.class);
                startActivity(ittto5);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    double makeroundneg(double round) {
        round = Math.ceil(round*(-1.)*100.)/100.;
        return round;
    }

}
