package ifmg.rodrigo.fuelassistant.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.modelo.Carro;
import ifmg.rodrigo.fuelassistant.modelo.Fabricante;
import ifmg.rodrigo.fuelassistant.modelo.ModeloCarro;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.CarroDAO;
import ifmg.rodrigo.fuelassistant.servico.FabricanteDAO;
import ifmg.rodrigo.fuelassistant.servico.ModeloCarroDAO;
import ifmg.rodrigo.fuelassistant.servico.TipoCombustivelDAO;
import ifmg.rodrigo.fuelassistant.servico.UsuarioDAO;


public class AdicionaVeiculos extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CarroDAO cDAO;
    ModeloCarroDAO mcDAO;
    FabricanteDAO fDAO;

    Fabricante fabricanteSelecionado;


    List<ModeloCarro> modelos;
    List<Fabricante> fabricantes;

    Spinner comboModeloCarro;
    Spinner spFabricante;
    EditText etAnoFabricacao;
    TextInputLayout ilAnoFabricação;


    Button btCancela;
    Button btConfirma;

    boolean primeiroUso = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_veiculos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        if(args.getString("primeiroUso").equals("true")){

            primeiroUso = true;
        }


        cDAO = new CarroDAO(this);
        fDAO = new FabricanteDAO(this);
        mcDAO = new ModeloCarroDAO(this);

        spFabricante = (Spinner) findViewById(R.id.spAddCarrosFabricante);
        comboModeloCarro = (Spinner) findViewById(R.id.spAddCarrosModelo);
        btCancela = (Button) findViewById(R.id.btAddCarroCancela);
        btConfirma = (Button) findViewById(R.id.btAddCarroConfirma);
        etAnoFabricacao = (EditText) findViewById(R.id.etAddCarroAno);
        ilAnoFabricação = (TextInputLayout) findViewById(R.id.ilAddCarroAno);
        etAnoFabricacao.addTextChangedListener(new MyTextWatcher(etAnoFabricacao));

        fabricantes = fDAO.findAll();
        fabricanteSelecionado = fabricantes.get(0);

        modelos = mcDAO.findByFabricante(fabricanteSelecionado.getCodigo());


        String[] listaFabricantes = new String[fabricantes.size()];


        for (int i = 0; i < listaFabricantes.length; i++) {  //Converte Lista de Fabricantes para Lista de String
            listaFabricantes[i] = fabricantes.get(i).getNome();
        }


        spFabricante.setOnItemSelectedListener(this);   //Monta o Adpater para o Spinner de Fabricates que contem imagens
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listaFabricantes);
        spFabricante.setAdapter(customAdapter);

        atualizaListaModelos();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {


        fabricanteSelecionado = fabricantes.get(position);
        modelos = mcDAO.findByFabricante(fabricanteSelecionado.getCodigo());
        atualizaListaModelos();

    }

    public void atualizaListaModelos(){


        String[] listaModelos = new String[modelos.size()];
        for (int i = 0; i < listaModelos.length; i++) {  //Converte Lista de Modelos para Lista de String
            listaModelos[i] = modelos.get(i).getNome();
        }

        //Cria o adaptador p/ Modelos Carros
        ArrayAdapter<String> adaptadorF = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaModelos);
        adaptadorF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboModeloCarro.setAdapter(adaptadorF);

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void onClickCancelar(View view) {

        finish();
    }


    public void onClickConcluir(View view) {

        Calendar cal = Calendar.getInstance();

        try {

            Integer anoFabricacao = Integer.valueOf(etAnoFabricacao.getText().toString());

            System.out.println("(1) TESTE EM ADD CARRO:   " + anoFabricacao + " > 1950 e < " + cal.get(Calendar.YEAR));
            if ((anoFabricacao > 1950) && (anoFabricacao < (cal.get(Calendar.YEAR) + 1))) {         //Restrição de 1950 à Ano Atual + 1


                ModeloCarro modelo = modelos.get(comboModeloCarro.getSelectedItemPosition());

                Carro novoCarro = new Carro();
                novoCarro.setAno(Integer.valueOf(etAnoFabricacao.getText().toString()));
                novoCarro.setModelo(modelo);
                novoCarro.setCodigo(cDAO.save(novoCarro));

                AuxiliarDAO aDAO = new AuxiliarDAO(this);
                UsuarioDAO uDAO = new UsuarioDAO(this);
                TipoCombustivelDAO tcDAO = new TipoCombustivelDAO(this);

                Auxiliar novoRegistro = new Auxiliar();
                novoRegistro.setCarro(novoCarro);
                novoRegistro.setData(new Date());
                novoRegistro.setNvCombustAnterior(0);
                novoRegistro.setTipoCombustivel(tcDAO.findAll().get(0));
                novoRegistro.setUsuario(uDAO.findAll().get(0));
                novoRegistro.setCodigo(aDAO.save(novoRegistro));

                Toasty.success(this,getString(R.string.VeiculoCadastradoSucesso),Toast.LENGTH_SHORT,true).show();

                if(primeiroUso){


                    Auxiliar registroAtual = aDAO.getRegistroAtualByCarro(novoCarro.getCodigo());

                    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("registroAtual", Integer.valueOf(registroAtual.getCodigo()+""));
                    editor.putInt("modoSimulador", 0);
                    editor.commit();


                    Intent intent = new Intent(this, MainActivity.class);
                    Bundle args = new Bundle();
                    args.putString("codAuxiliar", registroAtual.getCodigo() + "");
                    intent.putExtras(args);

                    startActivity(intent);

                }


                finish();

            } else {

                ilAnoFabricação.setError(getResources().getString(R.string.AnoInvalido));
                requestFocus(etAnoFabricacao);
            }
        } catch (Exception e) {

            Log.d("ERRO", e.getMessage());
        }

    }

    public boolean validarAno() {

        Calendar cal = Calendar.getInstance();
        try {
            if (etAnoFabricacao.getText().toString() != "") {
                Integer anoFabricacao = Integer.valueOf(etAnoFabricacao.getText().toString());

                System.out.println("(2) TESTE EM ADD CARRO:   " + anoFabricacao + " > 1950 e < " + cal.get(Calendar.YEAR));

                if ((anoFabricacao > 1950) && (anoFabricacao < (cal.get(Calendar.YEAR) + 2))) {

                    ilAnoFabricação.setErrorEnabled(false);

                    return true;
                } else {
                    ilAnoFabricação.setError(getResources().getString(R.string.AnoInvalido));
                    requestFocus(etAnoFabricacao);
                    return false;

                }

            }


        }catch (Exception e){

        }
        return false;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        protected MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

            validarAno();
        }
    }

}





class CustomAdapter extends BaseAdapter {
    Context context;
    int logos[];
    String[] fabricantes;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, String[] fabricantes) {
        this.context = applicationContext;
        this.logos = new int[fabricantes.length];
        this.fabricantes = fabricantes;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return logos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_customizado, null);
        ImageView icon = (ImageView) view.findViewById(R.id.ivSpinnerLogoFabricante);
        TextView names = (TextView) view.findViewById(R.id.ivSpinnerNomeFabricante);



        switch (fabricantes[i].toUpperCase()){      //Adiciona a logo no nos itens do Spinner
            case "FORD": {
                icon.setImageResource(R.drawable.logo_ford);
                break;
            }

            case "FIAT": {
                icon.setImageResource(R.drawable.logo_fiat);
                break;
            }

            case "CHEVROLET": {
                icon.setImageResource(R.drawable.logo_chevrolet);
                break;
            }

            case "VOLKSWAGEN": {
                icon.setImageResource(R.drawable.logo_volkswagen);
                break;
            }

            case "RENAULT": {
                icon.setImageResource(R.drawable.logo_renault);
                break;
            }

            case "HYUNDAI": {
                icon.setImageResource(R.drawable.logo_hyundai);
                break;
            }

            case "TOYOTA": {
                icon.setImageResource(R.drawable.logo_toyota);
                break;
            }

            case "HONDA": {
                icon.setImageResource(R.drawable.logo_honda);
                break;
            }

            case "NISSAN": {
                icon.setImageResource(R.drawable.logo_nissan);
                break;
            }

            case "MITSUBISHI": {
                icon.setImageResource(R.drawable.logo_mitsubishi);
                break;
            }

            case "CITROËN": {
                icon.setImageResource(R.drawable.logo_citroen);
                break;
            }

            case "PEUGEOT": {
                icon.setImageResource(R.drawable.logo_peugeot);
                break;
            }

            case "KIA": {
                icon.setImageResource(R.drawable.logo_kia);
                break;
            }

            case "MERCEDES-BENZ": {
                icon.setImageResource(R.drawable.logo_mercedes);
                break;
            }

            case "BMW": {
                icon.setImageResource(R.drawable.logo_bmw);
                break;
            }

            case "AUDI": {
                icon.setImageResource(R.drawable.logo_audi);
                break;
            }

            case "CHERY": {
                icon.setImageResource(R.drawable.logo_chery);
                break;
            }


            case "LAND ROVER": {
                icon.setImageResource(R.drawable.logo_landrover);
                break;
            }


            case "JAC": {
                icon.setImageResource(R.drawable.logo_jacmotors);
                break;
            }

            case "SUZUKI": {
                icon.setImageResource(R.drawable.logo_suzuki);
                break;
            }

            case "GMC": {
                icon.setImageResource(R.drawable.logo_gmc);
                break;
            }

            case "JEEP": {
                icon.setImageResource(R.drawable.logo_jeep);
                break;
            }


            default:{
                icon.setImageResource(R.drawable.ic_help_black_24dp);
            }


        }

        names.setText(fabricantes[i]);
        return view;
    }
}
