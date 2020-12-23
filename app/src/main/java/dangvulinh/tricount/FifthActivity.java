package dangvulinh.tricount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FifthActivity extends AppCompatActivity {

    Switch swtsync;
    TextView tv;

    int i = 0;

    private Socket mSocket;

    Database database;


    //mang projet old
    ArrayList<Integer> oldprojetID;
    ArrayList<String> oldparticipant;
    ArrayList<Double> oldpaid;
    ArrayList<Double> oldcount;
    ArrayList<String> oldsujet;
    ArrayList<String> oldresult;

    //mang list projet old
    ArrayList<Integer> oldID;
    ArrayList<String> oldlistprojet;
    ArrayList<String> olddescription;
    ArrayList<String> olddate;
    ArrayList<Double> oldtotal;
    ArrayList<Double> oldperso;

    //mang projet
    ArrayList<Integer> projetID;
    ArrayList<String> participant;
    ArrayList<Double> paid;
    ArrayList<Double> count;
    ArrayList<String> sujet;
    ArrayList<String> result;

    //mang list projet
    ArrayList<Integer> ID;
    ArrayList<String> listprojet;
    ArrayList<String> description;
    ArrayList<String> date;
    ArrayList<Double> total;
    ArrayList<Double> perso;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        database = new Database(this, "listprojet.sqlite", null,1);

        AndXa();

        GetDataProjet( oldprojetID, oldparticipant, oldpaid,oldcount, oldsujet,oldresult);
        GetDataListProjet(oldID,oldlistprojet,olddescription,olddate,oldtotal,oldperso);

        if(checkequal(oldID, oldprojetID) == 0) { //0 - false
            database.QueryData("DELETE FROM projet");
         //   database.QueryData("DELETE FROM listprojet");
        }


        try {
            mSocket = IO.socket("http://192.168.1.11:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        swtsync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                  //  Toast.makeText(FifthActivity.this, "Turn on", Toast.LENGTH_SHORT).show();
                    //Connected
                    mSocket.connect();

                    //server send data projet
                    mSocket.on("server-send-dataprojetID", ondataprojetID);
                    mSocket.on("server-send-dataParticipant", ondataParticipant);
                    mSocket.on("server-send-dataPaid", ondataPaid);
                    mSocket.on("server-send-dataCount", ondataCount);
                    mSocket.on("server-send-dataSujet", ondataSujet);
                    mSocket.on("server-send-dataResult", ondataResult);

                    //server send date list projet
                    mSocket.on("server-send-datalistID", ondatalistID);
                    mSocket.on("server-send-dataList", ondataList);
                    mSocket.on("server-send-dataDescription", ondataDesc);
                    mSocket.on("server-send-dataDate", ondataDate);
                    mSocket.on("server-send-dataTotal", ondataTotal);
                    mSocket.on("server-send-dataPerso", ondataPerso);

                    //client send data
                    int longprojet = oldprojetID.size();
                    mSocket.emit("client-send-datalongprojet",longprojet);
                    int longlist = oldID.size();
                    mSocket.emit("client-send-datalonglist",longlist);


                    //truyen tai projet
                    for (int j = 0; j < longprojet; j++) {
                        mSocket.emit("client-send-dataprojetID" + j + "",oldprojetID.get(j));
                        mSocket.emit("client-send-dataParticipant" + j + "",oldparticipant.get(j));
                        mSocket.emit("client-send-dataPaid" + j + "",oldpaid.get(j));
                        mSocket.emit("client-send-dataCount" + j + "",oldcount.get(j));
                        mSocket.emit("client-send-dataSujet" + j + "",oldsujet.get(j));
                        mSocket.emit("client-send-dataResult" + j + "",oldresult.get(j));
                    }


                    //truyen tai list projet
                    for (int j = 0; j < longlist; j++) {
                        mSocket.emit("client-send-datalistID" + j + "",oldID.get(j));
                        mSocket.emit("client-send-dataList" + j + "",oldlistprojet.get(j));
                        mSocket.emit("client-send-dataDescription" + j + "",olddescription.get(j));
                        mSocket.emit("client-send-dataDate" + j + "",olddate.get(j));
                        mSocket.emit("client-send-dataTotal" + j + "",oldtotal.get(j));
                        mSocket.emit("client-send-dataPerso" + j + "",oldperso.get(j));
                    }
////////



                } else {

                    Toast.makeText(FifthActivity.this, "Turn off", Toast.LENGTH_SHORT).show();
                    mSocket.disconnect();

                }

                if(ID.isEmpty() && projetID.isEmpty() && participant.isEmpty() && paid.isEmpty() &&
                        count.isEmpty() && sujet.isEmpty() && result.isEmpty() && listprojet.isEmpty() &&
                        description.isEmpty() && date.isEmpty() && total.isEmpty() && perso.isEmpty() ) {
                    tv.setText("vide from server");
                } else {
                    tv.setText("no vide from server");

                    //remove if 2 list is the same


                    //change new ID for the new from serveur
                    if( ( isequal(projetID, participant, paid, count, sujet, result) == 1 ) &&
                            ( isequal(ID, listprojet, description, date, total, perso) == 1 )) {
                        int sumoldID = sumArrayList(oldID);

                        for (int a = 0; a < ID.size(); a++) {
                            int x = 0;
                            x = ID.get(a) + sumoldID;
                            ID.set(a, x);
                        }

                        for (int a = 0; a < projetID.size(); a++) {
                            int x = 0;
                            x = projetID.get(a) + sumoldID;
                            projetID.set(a, x);
                        }


                        duplicate();


                        insertArrayprojet(projetID, participant, paid, count, sujet, result);
                        insertArraylistprojet(ID, listprojet, description, date, total, perso);






                        tv.setText("Sync is done");
                    } else {
                        tv.setText("Sync is fail");
                    }



                }
            }
        });



    }

    void AndXa() {
        swtsync = (Switch) findViewById(R.id.swtSync);
        tv = (TextView) findViewById(R.id.tvTest);

        //mang projet old
        oldprojetID =  new ArrayList<>();
        oldparticipant =  new ArrayList<>();
        oldpaid =  new ArrayList<>();
        oldcount =  new ArrayList<>();
        oldsujet =  new ArrayList<>();
        oldresult =  new ArrayList<>();

        //mang list projet old
        oldID =  new ArrayList<>();
        oldlistprojet =  new ArrayList<>();
        olddescription =  new ArrayList<>();
        olddate =  new ArrayList<>();
        oldtotal =  new ArrayList<>();
        oldperso =  new ArrayList<>();

        //mang projet
        projetID =  new ArrayList<>();
        participant =  new ArrayList<>();
        paid =  new ArrayList<>();
        count =  new ArrayList<>();
        sujet =  new ArrayList<>();
        result =  new ArrayList<>();

        //mang list projet
        ID =  new ArrayList<>();
        listprojet =  new ArrayList<>();
        description =  new ArrayList<>();
        date =  new ArrayList<>();
        total =  new ArrayList<>();
        perso =  new ArrayList<>();


    }



    private void duplicate () {
        //xet su trung lap list
        for (int j = 0; j<ID.size(); j++) {
            for (int f = j+1; f<ID.size(); f++) {
                if (ID.get(j).equals(ID.get(f))) {
                    ID.remove(f);
                    listprojet.remove(f);
                    description.remove(f);
                    date.remove(f);
                    total.remove(f);
                    perso.remove(f);
                    break;
                }
            }
        }

        //trung lap cho projet
        for (int j = 0; j<projetID.size(); j++) {
            for (int f = j+1; f<projetID.size(); f++) {
                if (projetID.get(j).equals(projetID.get(f)) &&
                        participant.get(j).equals(participant.get(f)) &&
                        paid.get(j).equals(paid.get(f)) &&
                        count.get(j).equals(count.get(f)) &&
                        sujet.get(j).equals(sujet.get(f)) &&
                        result.get(j).equals(result.get(f))) {

                    projetID.remove(f);
                    participant.remove(f);
                    paid.remove(f);
                    count.remove(f);
                    sujet.remove(f);
                    result.remove(f);
                    break;
                }
            }
        }
    }
    /*
    private  void duplicateAll() {
        int repond = 0; // 0 false; 1 true
        //trung lap cho projet
        for (int j = 0; j<oldprojetID.size(); j++) {
            for (int f = 0; f<projetID.size(); f++) {
                if (oldprojetID.get(j).equals(projetID.get(f)) &&
                        oldparticipant.get(j).equals(participant.get(f)) &&
                        oldpaid.get(j).equals(paid.get(f)) &&
                        oldcount.get(j).equals(count.get(f)) &&
                        oldsujet.get(j).equals(sujet.get(f)) &&
                        oldresult.get(j).equals(result.get(f))) {
                        repond = 1;

                    break;
                }
            }
        }
    }*/

    // 1 - true ; 0 est false
    int checkequal(ArrayList<Integer> idlist, ArrayList<Integer> idprojet) {
        int a = 0;
        for (int b = 0; b < idlist.size(); b++) {
            for (int c = 0; c < idprojet.size(); c++) {
                if (idlist.get(b) == idprojet.get(c)) {
                    a = 1;
                }
            }
        }
        return a;
    }

    // 0 = false not egal, 1 = egal
    int isequal (ArrayList projetID,
                     ArrayList participant,
                     ArrayList paid,
                     ArrayList count,
                     ArrayList sujet,
                     ArrayList result) {
        int egal = projetID.size();
        int n = 0;
        if ( (participant.size() == egal) &&
                (paid.size() == egal) &&
                (count.size() == egal) &&
                (sujet.size() == egal) &&
                ( result.size() == egal) ) {
            n = 1;
        }
        return n;
    }

    void insertArraylistprojet(ArrayList<Integer> ID,
            ArrayList<String> listprojet,
            ArrayList<String> description,
            ArrayList<String> date,
            ArrayList<Double> total,
            ArrayList<Double> perso) {
        //them phan tu vao list projet
        for (int a = 0; a < ID.size(); a++) {
            database.QueryData("INSERT INTO listprojet VALUES (" +
                    ID.get(a) + ",'" +
                    listprojet.get(a) + "','" +
                    description.get(a) + "','" +
                    date.get(a) + "'," +
                    total.get(a) + "," +
                    perso.get(a) + ")");
        }
    }


    void insertArrayprojet ( ArrayList<Integer> projetID,
            ArrayList<String> participant,
            ArrayList<Double> paid,
            ArrayList<Double> count,
            ArrayList<String> sujet,
            ArrayList<String> result){

        for (int a = 0; a<projetID.size();a++) {
            database.QueryData("INSERT INTO projet VALUES (" +
                    projetID.get(a) + "," +
                    "'" + participant.get(a) + "'," +
                    paid.get(a) + "," +
                    count.get(a) + "," +
                    "'" + sujet.get(a) + "'," +
                    "'" + result.get(a) + "'" +
                    ")");
        }
    }

    boolean checkEmpty(ArrayList<Integer> projetID,
                       ArrayList<String> participant,
                       ArrayList<Double> paid,
                       ArrayList<Double> count,
                       ArrayList<String> sujet,
                       ArrayList<String> result) {
        if (projetID.isEmpty() && participant.isEmpty() && paid.isEmpty() && count.isEmpty() && sujet.isEmpty() && result.isEmpty()) {
            return true;
        }
        return false;
    }


    //client nhan projetID
    private Emitter.Listener ondataprojetID = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("projetID");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            //lay mang projet ID
                            projetID.add(arrayProjetID.getInt(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //client nhan participant
    private Emitter.Listener ondataParticipant = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("participant");

                        for (int i = 0; i<arrayProjetID.length();i++) {

                            participant.add(arrayProjetID.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //client nhan paid
    private Emitter.Listener ondataPaid = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("paid");

                        for (int i = 0; i<arrayProjetID.length();i++) {

                            paid.add(arrayProjetID.getDouble(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //client nhan count
    private Emitter.Listener ondataCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("count");

                        for (int i = 0; i<arrayProjetID.length();i++) {

                            count.add(arrayProjetID.getDouble(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //client nhan sujet
    private Emitter.Listener ondataSujet = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("sujet");

                        for (int i = 0; i<arrayProjetID.length();i++) {

                           sujet.add(arrayProjetID.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //client nhan result
    private Emitter.Listener ondataResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("result");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                           result.add(arrayProjetID.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    ///////

    //client nhan list id
    private Emitter.Listener ondatalistID = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("datalist");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            ID.add(arrayProjetID.getInt(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //client nhan list
    private Emitter.Listener ondataList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("datalist");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            listprojet.add(arrayProjetID.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //client nhan description
    private Emitter.Listener ondataDesc = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("datalist");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            description.add(arrayProjetID.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //client nhan date
    private Emitter.Listener ondataDate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("datalist");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            date.add(arrayProjetID.getString(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //client nhan total
    private Emitter.Listener ondataTotal = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("datalist");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            total.add(arrayProjetID.getDouble(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //client nhan perso
    private Emitter.Listener ondataPerso = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray arrayProjetID = object.getJSONArray("datalist");

                        for (int i = 0; i<arrayProjetID.length();i++) {
                            perso.add(arrayProjetID.getDouble(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };



    //return array listprojet
    void GetDataListProjet(ArrayList<Integer> ID,
            ArrayList<String> listprojet,
            ArrayList<String> description,
            ArrayList<String> date,
            ArrayList<Double> total,
            ArrayList<Double> perso)
    {
        //get list projet
        Cursor dataprojet = database.GetData("SELECT * FROM listprojet");
        while (dataprojet.moveToNext()) {
            int id = dataprojet.getInt(0);
            String list = dataprojet.getString(1);
            String desc = dataprojet.getString(2);
            String datee = dataprojet.getString(3);
            double totall = dataprojet.getDouble(4);
            double totalperso = dataprojet.getDouble(5);
            ID.add(id);
            listprojet.add(list);
            description.add(desc);
            date.add(datee);
            total.add(totall);
            perso.add(totalperso);
        }
    }

    //return projet
    void GetDataProjet( ArrayList<Integer> projetID,
            ArrayList<String> participant,
            ArrayList<Double> paid,
            ArrayList<Double> count,
            ArrayList<String> sujet,
            ArrayList<String> result)
    {
        Cursor dataprojet = database.GetData("SELECT * FROM projet");
        while (dataprojet.moveToNext()) {
            int id = dataprojet.getInt(0);
            String part = dataprojet.getString(1);
            double paidn = dataprojet.getDouble(2);
            double countn = dataprojet.getDouble(3);
            String sujetn = dataprojet.getString(4);
            String resultn = dataprojet.getString(5);
           projetID.add(id);
           participant.add(part);
           paid.add(paidn);
           count.add(countn);
           sujet.add(sujetn);
           result.add(resultn);

        }
    }




    int sumArrayList(ArrayList<Integer> a) {
        int sum = 0;
        for (int i = 0; i< a.size(); i++) {
            sum = sum + a.get(i);
        }
        return sum;
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
                Intent ttt1 = new Intent(FifthActivity.this, MainActivity.class);
                startActivity(ttt1);
                break;
            case R.id.menuNewProjet:
                //Chuyen sang avtivity 3
                Intent ittSndMain = new Intent(FifthActivity.this, ThirdActivity.class);
                startActivity(ittSndMain);
                break;
            case R.id.menuSync:
                //Chuyen sang avtivity 5
                Intent ittto5 = new Intent(FifthActivity.this, FifthActivity.class);
                startActivity(ittto5);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

