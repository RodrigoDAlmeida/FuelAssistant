package ifmg.rodrigo.fuelassistant.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.fragment.CorridasFragment;
import ifmg.rodrigo.fuelassistant.fragment.MeusVeiculosFragment;
import ifmg.rodrigo.fuelassistant.fragment.RegistrosFragment;
import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.modelo.Corrida;
import ifmg.rodrigo.fuelassistant.modelo.Registro;
import ifmg.rodrigo.fuelassistant.other.Abastecimento;
import ifmg.rodrigo.fuelassistant.other.Veiculo;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.CorridaDAO;
import ifmg.rodrigo.fuelassistant.servico.RegistroDAO;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.fragment.HomeFragment;
import ifmg.rodrigo.fuelassistant.fragment.RelatoriosFragment;
import ifmg.rodrigo.fuelassistant.fragment.SettingsFragment;
import ifmg.rodrigo.fuelassistant.other.CircleTransform;

public class MainActivity extends AppCompatActivity implements Runnable {

    //Interfaces


    ProgressBar pbCarregando;
    TextView tvCarregando;

    //Variaveis Controle:

    Double nvAntesAbastecimento;
    boolean posFab = false;
    long codRegistroAtual;


    //Bluetooth
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    String enderecoDispositivo = "";
    boolean bluetoothAtivado;
    BluetoothSocket socket = null;
    Boolean msgConectado = false;


    //Variaveis OBD
    public Veiculo veiculo = new Veiculo();
    Corrida corrida = new Corrida();
    Abastecimento abastecimento = new Abastecimento();


    private Handler handler;
    public boolean running;

    ThrottlePositionCommand throttlePosition = new ThrottlePositionCommand();
    SpeedCommand speedCommand = new SpeedCommand();
    FuelLevelCommand fuel = new FuelLevelCommand();
    MassAirFlowCommand massAirFlowCommand = new MassAirFlowCommand();
    EngineCoolantTemperatureCommand temp = new EngineCoolantTemperatureCommand();

    //Variaveis de Controle dos Módulos


    public boolean modoSimulador = false;
    public boolean modoCompatibilidade = false;
    public boolean primeiraEtapa = true;
    RegistroDAO rDAO;
    AuxiliarDAO aDAO;
    CorridaDAO coDAO;
    Integer qntErrosPacotes = 0;
    public Auxiliar registroAtual;


    FragmentTransaction ft;
    HomeFragment fragmentAtual;
    Fragment frag;

    //Bottom bar e Nav Menu
    //Bottom Bar:

    private AHBottomNavigation bottomNavigation;
    AHBottomNavigationItem bb_item1;
    AHBottomNavigationItem bb_item2;
    AHBottomNavigationItem bb_item3;

    //NavMenu:

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    public FloatingActionButton fab;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_VEICULOS = "veiculos";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragmentAtual when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rDAO = new RegistroDAO(this);
        aDAO = new AuxiliarDAO(this);
        coDAO = new CorridaDAO(this);

        pbCarregando = (ProgressBar) findViewById(R.id.pbCarregando);
        tvCarregando = (TextView) findViewById(R.id.tvConectando);


        constroiBottomBar();
        constroiNavBar(savedInstanceState);

        try {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAtivado = true;
            }
        } catch (Exception e) {

            Toasty.error(this, getString(R.string.semBluetooth), Toast.LENGTH_LONG, true).show();
        }

        Intent intent = getIntent();

        if (intent.hasExtra("codAuxiliar")) {

            codRegistroAtual = Long.valueOf(intent.getStringExtra("codAuxiliar"));
            registroAtual = aDAO.findById(codRegistroAtual);

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("registroAtual", Integer.valueOf(registroAtual.getCodigo() + ""));
            editor.commit();

        } else {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            codRegistroAtual = sharedPref.getInt("registroAtual", 0);
            registroAtual = aDAO.findById(codRegistroAtual);

            if (Integer.valueOf(sharedPref.getInt("modoSimulador", 0)) == 1) {

                modoSimulador = true;
            }


        }


    }

    public void atualizaFragment(HomeFragment frag) {

        fragmentAtual = frag;
        atualizaComponentes();
    }


    public void atualizaComponentes() {


        try {

            if (registroAtual == null) {

                registroAtual = aDAO.findById(codRegistroAtual);
            }

            fragmentAtual.atualizaInformacoes(registroAtual);

        } catch (Exception e) {

        }


    }


    public void ligaBluetooth(BluetoothAdapter btAdapter) {


        if (!verificaCompatibilidadeBluetooth(btAdapter)) {
            toast("Seu dispositivo não suporta aplicações Bluetooth");
            return;
        }

        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            toast("Solicitando ativação do Bluetooth...");


        } else {
            toast("Bluetooth ON");
            bluetoothAtivado = true;
        }

    }


    public void selecionaDispositivo() {

        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            BluetoothDevice device = null;
            for (int ii = 0; ii < pairedDevices.size(); ii++) {
                device = (BluetoothDevice) pairedDevices.toArray()[ii];
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));


        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                enderecoDispositivo = devices.get(position).toString();

                handler = new Handler();
                new conectaTask().execute(enderecoDispositivo);

            }
        });

        alertDialog.setTitle("Alterar o dispositivo Bluetooth");
        alertDialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (registroAtual == null) {

            try {
                aDAO = new AuxiliarDAO(this);
                registroAtual = aDAO.findById(codRegistroAtual);

            } catch (Exception e) {

            }
        }

        atualizaComponentes();
    }

    public void desconecta(boolean falha) {

        if (running) {

            bb_item1.setTitle(getResources().getString(R.string.Conectar));
            bb_item1.setDrawable(R.drawable.ic_bluetooth);

            bottomNavigation.removeAllItems();
            bottomNavigation.addItem(bb_item1);
            bottomNavigation.addItem(bb_item2);
            bottomNavigation.addItem(bb_item3);


        }

        running = false;
        primeiraEtapa = true;
        modoCompatibilidade = false;

        try {
            fragmentAtual.desconecta();
        } catch (Exception e) {
            Log.d("ERRO", e.getMessage().toString());
        }


    }

    public void retornaParaTelaPrincipal() {

        onBackPressed();
    }


    private void conecta() {

        running = true;
        bb_item1.setTitle(getResources().getString(R.string.Desconectar));
        bb_item1.setDrawable(R.drawable.ic_bluetooth_off);

        Intent intent = getIntent();
        if (intent.getIntExtra("modoSimulador", 0) == 1) {

            modoSimulador = true;
        }

        bottomNavigation.removeAllItems();
        bottomNavigation.addItem(bb_item1);
        bottomNavigation.addItem(bb_item2);
        bottomNavigation.addItem(bb_item3);


        try {
            fragmentAtual.conecta();
        } catch (Exception e) {
            Log.d("ERRO", e.getMessage().toString());
        }


    }

    public void imprimeErroOBD(Long codErro) {
        if (codErro == 0)
            Toasty.error(this, getResources().getString(R.string.NaoFoiPossivelConectar), Toast.LENGTH_LONG, true).show();
        if (codErro == -1)
            Toasty.error(this, getResources().getString(R.string.NaoFoiPossivelConectar), Toast.LENGTH_LONG, true).show();
        if (codErro == -2)
            Toasty.error(this, getResources().getString(R.string.ScannerDesativado), Toast.LENGTH_LONG, true).show();
        if (codErro == -3)
            Toasty.error(this, getResources().getString(R.string.MsgErroValvulaAcel), Toast.LENGTH_LONG, true).show();
        if (codErro == -4)
            Toasty.error(this, getResources().getString(R.string.MsgErroLeituraCombustivel), Toast.LENGTH_LONG, true).show();

    }


    private class conectaTask extends AsyncTask<String, Integer, Long> {

        @Override
        protected void onPreExecute() {
            pbCarregando.setVisibility(View.VISIBLE);
            tvCarregando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Long doInBackground(String... params) {

            return conectaBluetooth(params[0]);

        }

        @Override
        protected void onPostExecute(Long result) {
            pbCarregando.setVisibility(View.INVISIBLE);
            tvCarregando.setVisibility(View.INVISIBLE);


            if ((result < 1)) {         // Falhou no socketBluetooth

                desconecta(true);
                imprimeErroOBD(result);


                try {
                    socket.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }


            }


        }
    }

    @Override
    //Atualiza o Tipo de Combustivel na tela depois que a Activity de troca Combustivel é encerrada
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            registroAtual = aDAO.findById(registroAtual.getCodigo());
            fragmentAtual.atualizaInformacoes(registroAtual);
        }
    }

    public Long conectaBluetooth(String deviceAddress) {


        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {

            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            socket.connect();

            // Comandos para startar a conversação
            try {

                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                handler.post(this);

                running = true;         //SUCESSO
                veiculo = new Veiculo();
                msgConectado = true;
                return 1L;


            } catch (Exception e) {

                Log.d("[TESTE]", "Erro Bluetooh: " + e.getMessage());
                return (-1L);
            }

        } catch (Exception x) {

            Log.d("[TESTE]", "Erro Socket: " + x.getMessage());
            return 0L;
        }


    }


    @Override
    public void run() {

        if (running) {
            // Repetir depois de 1 milesimo de segundo
            handler.postDelayed(this, 1000);

            if (primeiraEtapa) {        //Se for a primeira vez do laço após a conexão, os pinos OBD serão testados
                testaOBD();
                primeiraEtapa = false;

            } else {

                if (msgConectado) {
                    conecta();
                    msgConectado = false;
                }

                if (modoSimulador) {  //Se for modo simulação, atualiza o valor de nv Combustivel com valor de Throttle

                    atualizaInformacoesOBD(1);

                } else {

                    if (modoCompatibilidade) {

                        atualizaInformacoesOBD(2);

                    } else {

                        atualizaInformacoesOBD(0);
                    }

                }

                fragmentAtual.atualizaVelocimetro(veiculo.getVelocidadeAtual(), veiculo.getAberturaBorboleta());
                fragmentAtual.atualizaCampos(registroAtual, veiculo.getNvCombustivel());


                if (registroAtual.getNvCombustAnterior() == 0) {                    /* Quando o nvCombust anterior for = 0, significa que é a primeira
                                                                                    conexão bem sucedida deste veiculo e a verificação começará
                                                                                a partir deste ponto */

                    registroAtual.setNvCombustAnterior(veiculo.getNvCombustivel());

                }


                switch (veiculo.getEstado()) {     //MAQUINA DE ESTADOS

                    case PARADO: {                  //SE ESTIVER NO ESTADO PARADO, VERIFICA-SE SE PODE SER FEITA A TRANSIÇÃO PARA UM OUTRO ESTADO

                        if (veiculo.isMovimento()) {
                            veiculo.setEstado(Veiculo.Estado.MOVIMENTO);
                            corrida = new Corrida();
                            corrida.iniciar(registroAtual.getCodigo());                      //Começa uma nova corrida
                            Log.d("[TESTE]", "Veiculo começou uma corrida !! ");
                            fragmentAtual.alteraAnimacao('M');

                        }

                        if (veiculo.isAbastecendo()) {

                            nvAntesAbastecimento = veiculo.nvCombustAntAnterior;
                            veiculo.setEstado(Veiculo.Estado.ABASTECENDO);
                            abastecimento.iniciar(veiculo.getNvCombustivel());
                            Log.d("[TESTE]", "Veiculo começou um Abastecimento !! ");
                            //toast("Abastecendo...");
                            fragmentAtual.alteraAnimacao('A');
                        }

                        fragmentAtual.atualizaCampos(registroAtual, veiculo.getNvCombustivel());
                        break;
                    }

                    case MOVIMENTO: {
                        if (veiculo.isMovimento()) {

                            corrida.capturarVelocidade(veiculo.getVelocidadeAtual(), veiculo.getAberturaBorboleta(), veiculo.getMassaAr());       //Captura a velocidade atual e a medida da abert. acelerac.
                            //Atualiza os valores das Views
                            fragmentAtual.atualizaDistanciaEVolumeAbastecido(registroAtual.getDistancia() + corrida.distanciaParcial(), registroAtual.getNvCombustAnterior(), veiculo.getNvCombustivel(), corrida);


                        } else {

                            corrida.finalizar();                                //Encerra a corrida e armazena os valores capturados no BD.

                            coDAO.save(corrida);            //Adiciona a nova corrida e atualiza o registro temporário
                            aDAO.save(registroAtual);
                            registroAtual.getCorridas().add(corrida);

                            Log.d("[TESTE]", "Corrida Terminada !  --- Dist. Percorrida  " + corrida.distanciaPercorrida + " Km");

                            veiculo.setEstado(Veiculo.Estado.PARADO);
                            fragmentAtual.alteraAnimacao('P');
                            fragmentAtual.atualizaCampos(registroAtual, veiculo.getNvCombustivel());
                            fragmentAtual.resetaConsumoInstantaneo(registroAtual);
                        }

                        break;
                    }

                    case ABASTECENDO: {

                        abastecimento.atualizaValor(veiculo.getNvCombustivel());
                        fragmentAtual.atualizaCampos(registroAtual, veiculo.getNvCombustivel());

                        if (abastecimento.isEstavel()) {

                            abastecimento.finalizar(veiculo.getNvCombustivel());
                            Log.d("[TESTE]", "Abastecimento Completo ! Total de Nv Abastecido: " + abastecimento.nvTotalAbastecido);


                            Registro r = new Registro();   //Prepara para gravar um novo registro no banco:
                            r.setUsuario(registroAtual.getUsuario());
                            r.setCarro(registroAtual.getCarro());
                            r.setTipoCombustivel(registroAtual.getTipoCombustivel());
                            r.setData(registroAtual.getData());
                            r.setMediaAberturaBorboleta(registroAtual.getMediaAberturaBorboleta());

                            r.setMediaConsumo(registroAtual.getMediaConsumoTotal());   // 2º Algoritmo

                            r.setDistancia(registroAtual.getDistancia());
                            r.setConsumo(registroAtual.getDistancia() / (registroAtual.getNvCombustAnterior() - nvAntesAbastecimento)); //Calcula o consumo a partir da distanciaPercorrida obtida no decorrer da analise e a quantidade de combustivel obtida anteriormente.

                            Log.d("[TESTE]", "GRAVANDO NO BD -> Nv. Abastecido: " + registroAtual.getNvCombustAnterior() + "  Distancia Total: " + registroAtual.getDistancia() + " Consumo Total:  " + r.getConsumo());
                            rDAO.save(r); //Persiste o regitro

                            //Zera os valores da análise
                            registroAtual.reiniciaValores();
                            coDAO.limpaCorridaRegistro(registroAtual.getCodigo());
                            registroAtual.setNvCombustAnterior(Double.valueOf(veiculo.getNvCombustivel())); //Prepara o regitro auxiliar para a proxima analise
                            registroAtual.setData(new java.util.Date()); //Data do Abastecimento


                            aDAO.save(registroAtual);
                            veiculo.setEstado(Veiculo.Estado.PARADO);
                            fragmentAtual.alteraAnimacao('P');
                            fragmentAtual.atualizaInformacoes(registroAtual);
                            Toasty.info(this, getContext().getString(R.string.AbastecimentoCompleto), Toast.LENGTH_LONG, true).show();


                        }


                        break;
                    }


                }


            }
        }
    }

    private void atualizaInformacoesOBD(int modo) {

        try {

            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
            int velocidade = speedCommand.getMetricSpeed();
            veiculo.setVelocidadeAtual(velocidade);

            qntErrosPacotes = 0;

        } catch (Exception e) {
            Log.d("ERRO", "OBD: Perdeu pacote de Velocidade Atual " + e.getMessage());


            try {

                String formated = e.getMessage().substring(e.getMessage().length()-22);
                int velocidade = 0;


                    String valorHexa = formated.substring(4, 6);
                    velocidade = Integer.parseInt(valorHexa, 16);


                veiculo.setVelocidadeAtual(velocidade);
                qntErrosPacotes = 0;

               //
                // qntErrosPacotes++;
            }catch (Exception e1){

                Log.d("ERRO", "Erro 2 "+ e1.getMessage());

            }
        }

        try {
            throttlePosition.run(socket.getInputStream(), socket.getOutputStream());
            veiculo.setAberturaBorboleta(throttlePosition.getPercentage());

        } catch (Exception e) {
            Log.d("ERRO", "OBD: Perdeu pacote de Throttle Position");
            qntErrosPacotes++;
        }

        try {
            massAirFlowCommand.run(socket.getInputStream(), socket.getOutputStream());
            veiculo.setMassaAr(massAirFlowCommand.getMAF());

        } catch (Exception e) {
            Log.d("ERRO", "OBD: Perdeu pacote de MAF");
            qntErrosPacotes++;
        }


        if (modo == 0) { // Modo Padrão (Fuel Level PIN)
            try {
                fuel.run(socket.getInputStream(), socket.getOutputStream());
                veiculo.setNvCombustivel(fuel.getFuelLevel());


            } catch (Exception e) {
                Log.d("ERRO", "OBD: Perdeu pacote de Nv Combustivel");
                qntErrosPacotes++;
            }
        }

        if (modo == 1) { // Modo Simulador (NvCombustivel = PID de Throttle

            try {
                temp.run(socket.getInputStream(), socket.getOutputStream());
                veiculo.setNvCombustivel(temp.getTemperature());
            }catch (Exception e){

                Log.d("ERRO", "OBD: Perdeu pacote de Temp Oleo "+e.getMessage());
                qntErrosPacotes++;
            }

        }

        if (qntErrosPacotes > 5) {

            imprimeErroOBD(-2l);

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            desconecta(true);

        }


    }

    private void testaOBD() {

        int erro = 0;
        try {
            //Extrai os valores do socket enviados pelo scanner Bluetooth

            if (modoSimulador) {   //Se for modo simulador, não buscar a informação do combustível (Fuel)


                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                massAirFlowCommand.run(socket.getInputStream(), socket.getOutputStream());
                erro = 1;
                throttlePosition.run(socket.getInputStream(), socket.getOutputStream());


            } else {


               // speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                erro = 1;
                throttlePosition.run(socket.getInputStream(), socket.getOutputStream());
                erro = 2;
                fuel.run(socket.getInputStream(), socket.getOutputStream());
                erro = 3;
               // massAirFlowCommand.run(socket.getInputStream(), socket.getOutputStream());
            }


        } catch (Exception x) {


            Log.d("[TESTE]", "Erro na captura  OBD" + x.getMessage());

            if (erro == 0) {
                Toasty.error(this, getResources().getString(R.string.ScannerDesativado) + " (" + x.getMessage() + ")", Toast.LENGTH_LONG, true).show();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                desconecta(false);

            }
            if (erro == 1) {
                Toasty.error(this, getResources().getString(R.string.MsgErroValvulaAcel) + " (" + x.getMessage() + ")", Toast.LENGTH_LONG, true).show();

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                desconecta(false);
            }

            if (erro == 3) {

                Toasty.info(this, getResources().getString(R.string.VeiculoSemSuporteMAF) + " (" + x.getMessage() + ")", Toast.LENGTH_SHORT, true).show();

            }

            if (erro == 2) {

                boolean possuiMAF = true;

                try {
                    massAirFlowCommand.run(socket.getInputStream(), socket.getOutputStream());

                } catch (Exception e) {
                    Log.d("Erro", "Erro OBD " + e);
                    possuiMAF = false;
                }


                if (!possuiMAF) {

                    modoCompatibilidade = true;
                    new AlertDialog.Builder(getContext())           //CRIA O DIALOG ALERT
                            .setIcon(R.drawable.ic_warning_black_48dp)
                            .setTitle(getString(R.string.MsgErroLeituraCombustivel))
                            .setMessage(getString(R.string.msgLigarCompatibilidade))
                            .setPositiveButton(getResources().getString(R.string.Sim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }

                            })
                            .setNegativeButton(getResources().getString(R.string.Nao), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    desconecta(false);
                                    modoCompatibilidade = false;


                                }

                            })
                            .show();
                } else {
                    //CASO NÃO POSSUA NEM O SENSOR DE NV COMBUSTIVEL NEM O MAF, NAO PODE PROSSEGUIR:

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    running = false;


                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.ic_warning_black_48dp)
                            .setTitle(getString(R.string.TituloSemSuporteNVCombustEMAF))
                            .setMessage(getString(R.string.msgSemSuporteNVCombustEMAF))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    desconecta(false);
                                }

                            })
                            .show();
                }
            }


        }


    }


    public void chamaMeusVeiculos(View view) {

        navItemIndex = 5;
        CURRENT_TAG = TAG_VEICULOS;
        loadHomeFragment();

    }


    public void onClickMeusVeiculos(View view) {

        chamaMeusVeiculos(view);

    }

    public void onClickCombustiveis(View view) {

        Intent intent = new Intent(this, TrocaCombustivelActivity.class);
        Bundle params = new Bundle();
        params.putString("codAuxiliar", registroAtual.getCodigo() + "");
        intent.putExtras(params);

        startActivityForResult(intent, 2);

    }


    public Boolean verificaCompatibilidadeBluetooth(BluetoothAdapter btAdapter) {
        return (btAdapter != null);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void toastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {   //Quando rotaciona a tela


        if (registroAtual == null) {
            try {
                registroAtual = aDAO.findById(codRegistroAtual);
                toggleFab();


            } catch (Exception e) {

            }
        }

        atualizaComponentes();
        super.onStart();

    }

    private void constroiNavBar(Bundle savedInstanceState) {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);

        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (posFab) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Action", null).show();
                    posFab = false;

                } else {
                    Bundle params = new Bundle();
                    params.putString("primeiroUso", "false");
                    Intent intent = new Intent(getContext(), AdicionaVeiculos.class);
                    intent.putExtras(params);
                    startActivity(intent);


                }


            }
        });


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }


    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website


        txtName.setText("Fuel Assistant");

        imgNavHeaderBg.setImageResource(R.drawable.nav_menu_header_bg);


        // Loading profile image
        Glide.with(this).load(R.drawable.logo288)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

    /***
     * Returns respected fragmentAtual that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            toggleFab();

            return;
        }


        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment f = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, f, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();


            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:
                // Corridas
                CorridasFragment corridasFragment = new CorridasFragment();
                return corridasFragment;
            case 2:
                // Registros
                RegistrosFragment registrosFragment = new RegistrosFragment();
                return registrosFragment;
            case 3:
                // notifications fragmentAtual
                RelatoriosFragment relatoriosFragment = new RelatoriosFragment();
                return relatoriosFragment;

            case 4:
                // settings fragmentAtual
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case 5:
                //Meus Veículos
                MeusVeiculosFragment meusVeiculosFragment = new MeusVeiculosFragment();
                return meusVeiculosFragment;


            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragmentAtual
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }


        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if ((navItemIndex != 0)) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragmentAtual is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragmentAtual is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            navItemIndex = 4;
            CURRENT_TAG = TAG_SETTINGS;
            loadHomeFragment();

            return true;
        }

        // user is in notifications fragmentAtual
        // and selected 'Mark all as Read'
        if (id == R.id.action_about) {

            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
        }

        // user is in notifications fragmentAtual
        // and selected 'Clear All'
        if (id == R.id.activity_privacy_policy) {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (CURRENT_TAG == TAG_VEICULOS) {
            fab.show();
            posFab = true;
        } else {
            fab.hide();
            posFab = false;
        }
    }

    private void constroiBottomBar() {

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);       //Constroi a BottomBar
        bb_item1 = new AHBottomNavigationItem(getResources().getString(R.string.Conectar), R.drawable.ic_bluetooth);
        bb_item2 = new AHBottomNavigationItem(getResources().getString(R.string.MeusVeiculos), R.drawable.ic_cars);
        bb_item3 = new AHBottomNavigationItem(getResources().getString(R.string.Ajuda), R.drawable.ic_help_black_24dp);

        bottomNavigation.addItem(bb_item1);
        bottomNavigation.addItem(bb_item2);
        bottomNavigation.addItem(bb_item3);


        bottomNavigation.setAccentColor(Color.WHITE);
        bottomNavigation.setInactiveColor(Color.WHITE);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {

            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {


                if (position == 0) {            //Clicou no Conectar Bluetooth


                    if (running == false) {                  //Se for para conectar com o dispositivo
                        AuxiliarDAO aDAO = new AuxiliarDAO(getContext());

                        if (aDAO.findAll().size() != 0) {        //Se houver pelo menos 1 veiculo cadastrado
                            if (!bluetoothAdapter.isEnabled()) {
                                ligaBluetooth(bluetoothAdapter);
                            } else {

                                bluetoothAtivado = true;
                                selecionaDispositivo();
                            }
                        } else {
                            toast("É necessário cadastrar pelo menos um veículo para continuar");
                        }
                    } else {   //Se for para DESCONECTAR DO DISPOSITIVO


                        String mensagem = "";               //PERSONALIZA A MENSAGEM

                        if (veiculo.getEstado().equals(Veiculo.Estado.PARADO)) {
                            mensagem = getResources().getString(R.string.DesconexaoParado);
                        }

                        if (veiculo.getEstado().equals(Veiculo.Estado.MOVIMENTO)) {
                            mensagem = getResources().getString(R.string.DesconexaoMovimento);
                        }

                        if (veiculo.getEstado().equals(Veiculo.Estado.ABASTECENDO)) {
                            mensagem = getResources().getString(R.string.DesconexaoAbastecendo);
                        }


                        new AlertDialog.Builder(getContext())           //CRIA O DIALOG ALERT
                                .setIcon(R.drawable.ic_warning_black_48dp)
                                .setTitle(getResources().getString(R.string.DesconexaoTitulo))
                                .setMessage(mensagem)
                                .setPositiveButton(getResources().getString(R.string.Sim), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            socket.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        desconecta(false);
                                    }

                                })
                                .setNegativeButton(getResources().getString(R.string.Nao), null)
                                .show();


                    }

                }

                if (position == 1) {            //Clicou no Meus Veículos


                    navItemIndex = 5;
                    CURRENT_TAG = TAG_VEICULOS;
                    loadHomeFragment();


                }

                if (position == 2) {            //Clicou em AJUDA


                    //   Intent intent = new Intent(getContext(), Relatorios.class);
                    //  startActivity(intent);


                }


                return wasSelected;
            }
        });
    }

    public Context getContext() {
        return this;
    }
}
