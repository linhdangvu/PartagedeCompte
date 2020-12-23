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

public class FourhActivity extends AppCompatActivity {

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
    String edtProjet;
    String edtDescription;
    String edtDate;
    ArrayList<String> arrayRes;
    ArrayList<String> arraySujet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourh);

        AnhXa();

        //get ID
        Intent intent = getIntent();
        projetid = intent.getIntExtra("dataID", 1234);

        //create database first
        database = new Database(this, "listprojet.sqlite", null,1);

        //get data ID from listprojet
        Cursor datalistprojet = database.GetData("SELECT * FROM listprojet WHERE id=" +
                projetid +
                " ");
        while (datalistprojet.moveToNext()) {
            edtProjet = datalistprojet.getString(1);
            edtDescription = datalistprojet.getString(2);
            edtDate = datalistprojet.getString(3);
            total = datalistprojet.getDouble(4);
            totalperso= datalistprojet.getDouble(5);
        }

        //get data ID from projet
        Cursor dataprojet = database.GetData("SELECT * FROM projet WHERE projetID=" +
                projetid +
                " ");
        while (dataprojet.moveToNext()) {
            arrayRes.add(dataprojet.getString(5));
            arraySujet.add(dataprojet.getString(4)) ;
        }



        //cho listview Result
        final ArrayAdapter adtResult = new ArrayAdapter(FourhActivity.this,
                android.R.layout.simple_list_item_1,
                arrayRes);

        lvRes.setAdapter(adtResult);

        //cho listview Sujet
        final ArrayAdapter adtSujet = new ArrayAdapter(FourhActivity.this,
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

                Toast.makeText(FourhActivity.this, "Delete is completed", Toast.LENGTH_SHORT).show();

                Intent itt2to1 = new Intent(FourhActivity.this, MainActivity.class);
                startActivity(itt2to1);
            }
        });

    }

    void AnhXa() {
        lvRes = (ListView) findViewById(R.id.lvResult);
        lvSuj = (ListView) findViewById(R.id.lvSujet);
        txtProjet = (TextView) findViewById(R.id.tvProjet);
        txtDescription = (TextView) findViewById(R.id.tvDescription);
        txtDate = (TextView) findViewById(R.id.tvDate);
        btndel = (Button) findViewById(R.id.btnDel);
        edtProjet = null;
        edtDescription = null;
        edtDate = null;
        arrayRes = new ArrayList<>();
        arraySujet = new ArrayList<>();
        txtTotal = (TextView) findViewById(R.id.tvTotal);
        txtTotalPerso = (TextView) findViewById(R.id.tvTotalPerso);
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
                Intent ittSndThird = new Intent(FourhActivity.this, MainActivity.class);
                startActivity(ittSndThird);
                break;
            case R.id.menuNewProjet:
                //Chuyen sang avtivity 1
                Intent ittSndMain = new Intent(FourhActivity.this, ThirdActivity.class);
                startActivity(ittSndMain);
                break;
            case R.id.menuSync:
                //Chuyen sang avtivity 5
                Intent ittto5 = new Intent(FourhActivity.this, FifthActivity.class);
                startActivity(ittto5);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
