package ifmg.rodrigo.fuelassistant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.modelo.Registro;
import ifmg.rodrigo.fuelassistant.modelo.TipoCombustivel;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.TipoCombustivelDAO;

public class TrocaCombustivelActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener{


    List<TipoCombustivel> combustiveis;
    Long codAuxiliar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troca_combustivel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        codAuxiliar  = Long.valueOf(args.getString("codAuxiliar"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.tituloAddNovoCombustivel));
                final EditText input = new EditText(getContext());
                builder.setMessage(R.string.mgsAddNovoCombustivel);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton(getString(R.string.Concluido), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nome = input.getText().toString();


                        if(!nome.isEmpty()) {       //Salva no BD
                                                    //Atualiza Lista

                            TipoCombustivel novoCombustivel = new TipoCombustivel();
                            novoCombustivel.setNome(nome);
                            TipoCombustivelDAO tcDAO = new TipoCombustivelDAO(getContext());
                            tcDAO.save(novoCombustivel);


                            atualizaListaCombustivel();
                            Toasty.success(getContext(), getString(R.string.CombustivelAddSucesso), Toast.LENGTH_SHORT, true).show();
                        }else{

                            Toasty.error(getContext(), getString(R.string.CombustivelAddErro), Toast.LENGTH_SHORT, true).show();
                        }



                    }
                });
                builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });

                atualizaListaCombustivel();


    }

    public Activity getContext(){

        return this;
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



    public void atualizaListaCombustivel(){

        TipoCombustivelDAO tcDAO = new TipoCombustivelDAO(getContext());
        combustiveis = tcDAO.findAll();
        ListView listView = (ListView) findViewById(R.id.lvTiposCombustivel);
        listView.setAdapter(new CombustivelAdapter(this));
        listView.setOnItemClickListener(this);
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int idx, long id) {

        TipoCombustivel tp = this.combustiveis.get(idx);

        AuxiliarDAO aDAO = new AuxiliarDAO(this);
        Auxiliar registro = aDAO.findById(codAuxiliar);
        registro.setTipoCombustivel(tp);
        aDAO.save(registro);

        Toasty.info(this,getResources().getString(R.string.CombutisvelAlterado)+" "+tp.getNome(),Toast.LENGTH_SHORT,true).show();



        Intent intent = new Intent();
        setResult(2,intent);
        finish();

    }

    public class CombustivelAdapter extends BaseAdapter {

        private final Context context;

        public CombustivelAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return combustiveis != null? combustiveis.size():0;
        }

        @Override
        public Object getItem(int position) {
            return combustiveis.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Infla a View
            View view = LayoutInflater.from(context).inflate(R.layout.listagem_combustiveis,parent,false);



            TextView tvNome = (TextView) view.findViewById(R.id.tvListagemCombustivelNome);
            TipoCombustivel combustivel = combustiveis.get(position);
            tvNome.setText(combustivel.getNome());

            return view;

        }
    }

}
