package ifmg.rodrigo.fuelassistant.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ifmg.rodrigo.fuelassistant.modelo.Fabricante;
import ifmg.rodrigo.fuelassistant.modelo.Usuario;
import ifmg.rodrigo.fuelassistant.other.PrefManager;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.FabricanteDAO;
import ifmg.rodrigo.fuelassistant.servico.ModeloCarroDAO;
import ifmg.rodrigo.fuelassistant.servico.TipoCombustivelDAO;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.servico.UsuarioDAO;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AuxiliarDAO aDAO = new AuxiliarDAO(this);
        FabricanteDAO fDAO = new FabricanteDAO(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_teste);

        if ((aDAO.findAll().size() == 0)&&(fDAO.findAll().size()==0)) {       //Primeiro Uso



            new InsertAsync().execute("");  //Cadastra no BD as info Iniciais


        }else {

            new carregaAsync().execute();
        }



    }


    public void fechar() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void fecharParaWelcome(){

        Intent intent = new Intent(this, WelcomeActivity.class);  //Chama Tela de boas vindas
        startActivity(intent);
        finish();
    }


    class carregaAsync extends AsyncTask<String, String, String> {
        //método executado antes do método da segunda thread doInBackground
        @Override
        protected void onPreExecute() {

        }

        //método que será executado em outra thread
        @Override
        protected String doInBackground(String... args) {
            SystemClock.sleep(TimeUnit.SECONDS.toMillis(1));

            return "";
        }

        //método executado depois da thread do doInBackground
        @Override
        protected void onPostExecute(String retorno) {
            fechar();
        }
    }


    /* -------------------------------------------------------
    SUBCLASSE RESPONSÁVEL POR CRIAR A SEGUNDA THREAD, OBJETIVANDO PROCESSAMENTO
    PARALELO AO DA THREAD DA INTERFACE GRÁFICA
     ----------------------------------------------------------*/
    class InsertAsync extends AsyncTask<String, String, String> {
        //método executado antes do método da segunda thread doInBackground
        @Override
        protected void onPreExecute() {

        }

        //método que será executado em outra thread
        @Override
        protected String doInBackground(String... args) {

            cadastraInformaçõesPadrões();
            SystemClock.sleep(TimeUnit.SECONDS.toMillis(2));

            return "";
        }

        //método executado depois da thread do doInBackground
        @Override
        protected void onPostExecute(String retorno) {

            fecharParaWelcome();

        }
    }


    public Context getContext() {
        return this;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void cadastraInformaçõesPadrões() {

        FabricanteDAO fDAO = new FabricanteDAO(this);
        ModeloCarroDAO mcDAO = new ModeloCarroDAO(this);
        TipoCombustivelDAO tcDAO = new TipoCombustivelDAO(this);


        fDAO.execSQL("INSERT INTO Fabricante (nome) VALUES ('Fiat'), ('Ford'), ('Chevrolet'), ('Volkswagen'), ('Renault'), ('Hyundai'), ('Toyota'), " +
                "('Honda'), ('Nissan'), ('Mitsubishi'), ('Citroën'), ('Peugeot'), ('Kia'), ('Mercedes-Benz'), ('BMW'), ('Audi'), ('Chery'), " +
                "('Land Rover'), ('JAC'), ('Suzuki'), ('GMC'), ('JEEP');");


        mcDAO.execSQL("INSERT INTO Modelo_Carro (nome, categoria, codFabricante) VALUES ('Mobi', 'HATCH', 1), ('Uno', 'HATCH', 1), ('Palio', 'HATCH', 1), " +   //FIAT
                "('Bravo', 'HATCH', 1), ('Punto', 'HATCH', 1), ('500', 'HATCH', 1), ('Abarth', 'HATCH', 1), ('Siena', 'SEDAN', 1), ('Linea', 'SEDAN', 1), " +
                "('Weekend', 'PICAPE', 1), ('Strada', 'PICAPE', 1), ('Toro', 'PICAPE', 1), ('Freemont', 'SUV', 1), ('Idea', 'VAN', 1), ('Doblo', 'VAN', 1), " +
                "('Fiorino', 'VAN', 1), ('Ducato Multi', 'VAN', 1), ('Ducato Cargo', 'VAN', 1), ('Ducato Passageiro', 'VAN', 1)," +
                "" +
                "('Ka', 'HATCH', 2), ('Ka+', 'SEDAN', 2), ('Fiesta', 'HATCH', 2), ('Fiesta Sedan', 'SEDAN', 2), ('Focus', 'HATCH', 2), ('Focus Fastback', 'SEDAN', 2), " +  //FORD
                "('Fusion', 'SEDAN', 2), ('Fusion Hybrid', 'SEDAN', 2), ('EcoSport', 'SUV', 2), ('Edge', 'SUV', 2), ('Ranger', 'PICAPE', 2)," +
                "" +
                "('Cobalt', 'SEDAN', 3), ('Cruze', 'SEDAN', 3), ('Cruze Sport6', 'HATCH', 3), ('Onix', 'HATCH', 3), ('Onix Activ', 'HATCH', 3), ('Prisma', 'SEDAN', 3), " +   //CHEVROLET
                "('Spin Activ', 'SEDAN', 3), ('Spin LTZ', 'HATCH', 3), ('Onix Joy', 'HATCH', 3), ('Prisma Joy', 'SEDAN', 3), ('Captiva', 'SUV', 3), ('Trailblazer', 'SUV', 3), " +
                "('Tracker', 'SUV', 3), ('Montana', 'PICAPE', 3), ('S10 Cabine Dupla', 'PICAPE', 3), ('S10 High Country', 'PICAPE', 3), ('S10 Cabine Simples', 'PICAPE', 3), ('Camaro', 'HATCH', 3)," +
                "" +
                "('Up!', 'HATCH', 4), ('Gol', 'HATCH', 4), ('Fox', 'HATCH', 4), ('CrossFox', 'HATCH', 4), ('Golf', 'HATCH', 4), ('Fusca', 'HATCH', 4), ('Polo', 'HATCH', 4), " +     //VOLKSWAGEM
                "('Voyage', 'SEDAN', 4), ('Jetta', 'SEDAN', 4), ('Passat', 'SEDAN', 4), ('CC', 'SEDAN', 4), ('Saveiro', 'PICAPE', 4), ('Amarok', 'PICAPE', 4), ('Tiguan', 'SUV', 4), " +
                "('Touareg', 'SUV', 4), ('SpaceFox', 'VAN', 4), ('Space Cross', 'VAN', 4), ('Golf Variant', 'VAN', 4)," +
                "" +
                "('Duster Oroch', 'PICAPE', 5), ('Duster', 'SUV', 5), ('Fluence', 'SEDAN', 5), ('R.S', 'HATCH', 5), ('Stepway', 'HATCH', 5), ('Sandero', 'HATCH', 5), ('Logan', 'SEDAN', 5)," +  //RENAUT
                " ('Clio', 'HATCH', 5), ('Kangoo', 'VAN', 5), ('Vitré', 'VAN', 5), ('Furgão', 'VAN', 5)," +
                "" +
                "('i30', 'HATCH', 6), ('Elantra', 'SEDAN', 6), ('Azera', 'SEDAN', 6), ('HB20', 'HATCH', 6), ('HB20X', 'HATCH', 6), ('HB20S', 'SEDAN', 6), ('Tucson', 'SUV', 6), ('ix35', 'SUV', 6), " + //HYUNDAI
                "('Santa Fe', 'SUV', 6), ('HR', 'VAN', 6)," +
                "" +
                "('Etios Hatch', 'HATCH', 7), ('Etios Sedan', 'SEDAN', 7), ('Etios SUV','SUV', 7), ('Corolla', 'SEDAN', 7), ('Hilux', 'PICAPE', 7), ('Hilux Cabine Simples', 'PICAPE', 7), ('SW4', 'SUV', 7)," +   //TOYOTA
                " ('RAV4', 'SUV', 7), ('Camry', 'SEDAN', 7), ('Prius', 'SEDAN', 7), ('Lexus', 'SEDAN', 7)," +
                "" +
                "('Fit', 'HATCH', 8), ('WR-V', 'SUV', 8), ('HR-V', 'HATCH', 8), ('CR-V', 'SUV', 8), ('City', 'SEDAN', 8), ('Civic', 'SEDAN', 8), ('Civic Si', 'SEDAN', 8), ('Accord', 'SEDAN', 8)," +  //HONDA
                "" +
                "('March', 'HATCH', 9), ('Versa', 'SEDAN', 9), ('Sentra', 'SEDAN', 9), ('Kicks', 'SUV', 9), ('Frontier', 'PICAPE', 9), ('GT-R', 'SEDAN', 9), " +       //NISSAN
                "" +
                "('Pajero', 'SUV', 10), ('Pajeto Outdoor', 'SUV', 10), ('Pajero Full', 'SUV', 10), ('Pajero Full 3D', 'SUV', 10), ('L200 Triton Sport', 'PICAPE', 10), ('L200 Triton HPE', 'PICAPE', 10), " +  //MITSUBISHI
                "('L200 Triton Outdoor', 'PICAPE', 10), ('L200 Triton GLX', 'PICAPE', 10), ('L200 Triton Savana', 'PICAPE', 10), ('ASX', 'SUV', 10), ('Outlander', 'SUV', 10), ('Lancer', 'SEDAN', 10), " +
                "('Lancer Evolution X', 'SEDAN', 10), ('L200 Triton GL', 'PICAPE', 10), ('Pajero HD', 'SUV', 10)," +
                "" +
                "('C3', 'HATCH', 11), ('Aircross', 'SUV', 11), ('C4 Lounge', 'SEDAN', 11), ('C4 Picasso', 'SUV', 11), ('Grand C4 Picasso', 'SUV', 11), ('DS 3', 'HATCH', 11), ('DS 4', 'HATCH', 11), ('DS 5', 'HATCH', 11), " +  //CITROEN
                "('Jumper Minibus', 'VAN', 11), ('Jumper Furgao', 'VAN', 11)," +
                "" +
                "('208', 'HATCH', 12), ('2008', 'SUV', 12), ('308', 'HATCH', 12), ('408', 'SEDAN', 12), ('Partner', 'VAN', 12), ('Boxer', 'VAN', 12)," +  //RENAUT
                "" +
                "('Picanto', 'HATCH', 13), ('Soul', 'HATCH', 13), ('Cerato', 'HATCH', 13), ('Optima', 'SEDAN', 13), ('Cadenza', 'SEDAN', 13), ('Quoris', 'SEDAN', 13), ('Sportage', 'SUV', 13), ('Sorento', 'SUV', 13), " +  //KIA
                "('Mohave', 'SUV', 13), ('Carnival', 'SUV', 13), ('Bongo', 'PICAPE', 13)," +
                "" +
                "('Classe A', 'HATCH', 14), ('Classe C', 'HATCH', 14), ('CLA', 'HATCH', 14), ('CLS', 'HATCH', 14), ('GLE', 'HATCH', 14), ('Classe C Estate', 'SUV', 14), ('MPV', 'HATCH', 14), ('SLC', 'SEDAN', 14)," +  //MERCEDES
                " ('SL', 'SEDAN', 14), ('Classe C Sedan', 'SEDAN', 14), ('Classe E', 'SEDAN', 14), ('Classe S', 'SEDAN', 14), ('GLA', 'SEDAN', 14), ('GLS', 'SUV', 14), ('GLC', 'SUV', 14), ('GLE', 'SUV', 14)," +
                "" +
                "('Serie 1', 'HATCH', 15), ('Serie 3', 'SEDAN', 15), ('Serie 5', 'SEDAN', 15), ('Serie 7', 'SEDAN', 15), ('M3 Sedan', 'SEDAN', 15), ('M5', 'SEDAN', 15), ('Serie 4 Cabrio', 'SEDAN', 15), " +       //BMW
                "('M4 Cabrio', 'SEDAN', 15), ('Serie 2 Coupe', 'SEDAN', 15), ('Serie 4 Gran Coupe', 'SEDAN', 15), ('M2 Coupe', 'SEDAN', 15), ('M4 Coupe', 'SEDAN', 15), ('M6 Gran Coupe', 'SEDAN', 15), " +
                "('Serie 3 Gran Turismo', 'SUV', 15), ('Z4 Roadster', 'SEDAN', 15), ('X4', 'SUV', 15), ('X6', 'SUV', 15), ('X6 M', 'SUV', 15), ('X1', 'SUV', 15), ('X3', 'SUV', 15), ('X5', 'SUV', 15), " +
                "('Serie 2 Active Tourer', 'SUV', 15), ('i3', 'HATCH', 15), ('i8', 'SEDAN', 15)," +
                "" +
                "('A1', 'HATCH', 16), ('A3', 'HATCH', 16), ('A3 Limousine', 'SEDAN', 16), ('A3 Cabriolet', 'SEDAN', 16), ('S3', 'HATCH', 16), ('S3 Limousine', 'SEDAN', 16), ('A4 Limousine', 'SEDAN', 16), " +     // AUDI
                "('A4 Avant', 'HATCH', 16), ('A5', 'HATCH', 16), ('A5 Sportback', 'HATCH', 16), ('A5 Cabriolet', 'SEDAN', 16), ('S5', 'HATCH', 16), ('A6 Limosine', 'SEDAN', 16), ('A6 Allroad Quattro', 'SUV', 16)," +
                " ('RS 6 Avant', 'HATCH', 16), ('A7', 'HATCH', 16), ('S7', 'HATCH', 16), ('RS 7', 'HATCH', 16), ('A8', 'SEDAN', 16), ('A8 Longo', 'SEDAN', 16), ('A8 W12', 'SEDAN', 16), ('S8', 'SEDAN', 16), " +
                "('Q3', 'HATCH', 16), ('RS Q3', 'HATCH', 16), ('Q5', 'HATCH', 16), ('SQ5', 'HATCH', 16), ('Q7', 'HATCH', 16), ('TT Roadster', 'HATCH', 16), ('R8 Coupe', 'HATCH', 16)," +
                "" +
                "('Arrizo 7', 'SEDAN', 17), ('Arrizo 5', 'SEDAN', 17), ('E5', 'SEDAN', 17), ('Arrizo 3', 'SEDAN', 17), ('Fulwin2 FL', 'SEDAN', 17), ('Fulwin2 FL HB', 'HATCH', 17), ('QQ', 'HATCH', 17)," +         //CHERY
                " ('Celer', 'HATCH', 17), ('Tiggo', 'SUV', 17)," +
                "" +
                "('Range Rover', 'SUV', 18), ('Range Rover Sport', 'SUV', 18), ('Range Rover Evoque', 'SUV', 18), ('Discovery', 'SUV', 18), ('Novo Discovery', 'SUV', 18), ('Discovery Sport', 'SUV', 18)," +       //LAND ROVER
                "" +
                "('T8', 'VAN', 19), ('T6', 'SUV', 19), ('T5', 'SUV', 19), ('J6', 'HATCH', 19), ('J5', 'SEDAN', 19), ('J3 Turin S', 'SEDAN', 19), ('J3S', 'SEDAN', 19), ('J2', 'HATCH', 19)," +                  // JAC
                "" +
                "('S-Cross', 'SUV', 20), ('Swift', 'HATCH', 20), ('Jimny', 'SUV', 20), ('Grand Vitara', 'SUV', 20)," +                                                                                          //SUZUKI
                "" +
                "('Canyon', 'PICAPE', 21), ('Sierra 1500', 'PICAPE', 21), ('Sierra Hd', 'PICAPE', 21), ('Terrain', 'SUV', 21), ('Acadia', 'SUV', 21), ('Yukon', 'SUV', 21), ('Denali Line', 'SUV', 21)," +     //GMC
                " ('Savana', 'VAN', 21),"+
                 "" +
                "('Compass', 'SUV', 22), ('Renegate', 'SUV', 22), ('Wrangler', 'SUV', 22), ('Cherokee', 'SUV', 22), ('Grand Cherokee', 'SUV', 22);");


        tcDAO.execSQL("INSERT INTO tipo_combustivel (nome) VALUES ('Gasolina'),('Alcool'),('Disel'),('Gasolina Aditivada'),('Misto')");

        UsuarioDAO uDAO = new UsuarioDAO(this);
        Usuario u = new Usuario();
        u.setNome(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)); //Busca o ID do dispositivo
        u.setCodigo(uDAO.save(u));

    }
}
