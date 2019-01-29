package ifmg.rodrigo.fuelassistant.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.activity.MainActivity;
import ifmg.rodrigo.fuelassistant.modelo.Carro;
import ifmg.rodrigo.fuelassistant.modelo.Registro;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.CarroDAO;
import ifmg.rodrigo.fuelassistant.servico.RegistroDAO;
import ifmg.rodrigo.fuelassistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrosFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    RegistroDAO rDAO;
    CarroDAO cDAO;
    List<Carro> veiculos;
    List<Registro> registros;

    View view;

    Integer veiculoSelecionado;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressBar pbRegistros;
    Spinner spVeiculo;
    ArrayAdapter<String> adaptadorF;
    ListView listView;


    private OnFragmentInteractionListener mListener;

    public RegistrosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrosFragment newInstance(String param1, String param2) {
        RegistrosFragment fragment = new RegistrosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_registros, container, false);
        pbRegistros = (ProgressBar) view.findViewById(R.id.pbRegistros);
        spVeiculo = (Spinner) view.findViewById(R.id.spRegistrosFiltroVeiculo);

        listView = (ListView) view.findViewById(R.id.lvRegistros);

        new carregaAsync().execute();


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                new AlertDialog.Builder(getActivity())           //CRIA O DIALOG ALERT
                        .setIcon(R.drawable.ic_warning_black_48dp)
                        .setTitle(getResources().getString(R.string.tituloExcluirRegistro))
                        .setMessage(getResources().getString(R.string.msgExcluirRegistro))
                        .setPositiveButton(getResources().getString(R.string.Excluir), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    Registro r = registros.get(pos);
                                    rDAO.delete(r);
                                    atualizaListaRegistros(view);
                                    Toasty.success(getActivity(), getResources().getString(R.string.RegistroExcluido), Toast.LENGTH_SHORT, true).show();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.Cancelar), null)
                        .show();


                return true;
            }
        });


        return view;

    }


    class carregaAsync extends AsyncTask<String, String, String> {
        //método executado antes do método da segunda thread doInBackground
        @Override
        protected void onPreExecute() {

        }

        //método que será executado em outra thread
        @Override
        protected String doInBackground(String... args) {

            rDAO = new RegistroDAO(getActivity());
            registros = rDAO.findAll();

            cDAO = new CarroDAO(getActivity());
            veiculos = cDAO.findAll();


            String[] listaVeiculos = new String[veiculos.size() + 1];
            listaVeiculos[0] = getResources().getString(R.string.Todos);
            System.out.println(veiculos.size());

            for (int i = 0; i < veiculos.size(); i++) {
                listaVeiculos[i + 1] = veiculos.get(i).getModelo().getNome() + "  " + veiculos.get(i).getAno();
            }


            adaptadorF = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaVeiculos);
            adaptadorF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            return "";
        }

        //método executado depois da thread do doInBackground
        @Override
        protected void onPostExecute(String retorno) {
            spVeiculo.setAdapter(adaptadorF);
            spVeiculo.setOnItemSelectedListener(RegistrosFragment.this);


            listView.setAdapter(new RegistroAdapter(getActivity()));
            listView.setOnItemClickListener(RegistrosFragment.this);

            pbRegistros.setVisibility(View.INVISIBLE);
        }
    }


    /* -------------------------------------------------------
    SUBCLASSE RESPONSÁVEL POR CRIAR A SEGUNDA THREAD, OBJETIVANDO PROCESSAMENTO
    PARALELO AO DA THREAD DA INTERFACE GRÁFICA
     ----------------------------------------------------------*/


    public void onItemClick(AdapterView<?> parent, View view, int idx, long id) {


        Toasty.info(getActivity(),"Consumo Medio: "+registros.get(Integer.valueOf(id+"")).getMediaConsumo(),Toast.LENGTH_LONG,true).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        if (position != 0) {      //Se for a posição 0, então busca todos Registros de todos veículos

            registros = rDAO.findByCarro(position + 0l);
        } else {

            registros = rDAO.findAll();
        }

        atualizaListaRegistros(view);

    }

    public void atualizaListaRegistros(View view) {

        ListView listView = (ListView) view.findViewById(R.id.lvRegistros);
        listView.setAdapter(new RegistroAdapter(getActivity()));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class RegistroAdapter extends BaseAdapter {

        private final Context context;

        public RegistroAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return registros != null ? registros.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return registros.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Infla a View
            View view = LayoutInflater.from(context).inflate(R.layout.listagem_registros, parent, false);

            //Faz findViewById das views que precisam ser atualizadas


            TextView tvCarro = (TextView) view.findViewById(R.id.tvListagemRegistroCarro);
            TextView tvConsumo = (TextView) view.findViewById(R.id.tvListagemRegistrosConsumo);
            TextView tvAceleracao = (TextView) view.findViewById(R.id.tvListagemRegistrosMedAcel);
            TextView tvData = (TextView) view.findViewById(R.id.tvListagemRegistrosData);
            TextView tvTipoCombust = (TextView) view.findViewById(R.id.tvListagemRegistrosTCombustivel);


            Registro registro = registros.get(position);

            DecimalFormat formato = new DecimalFormat("#.##");

            tvCarro.setText(registro.getCarro().getModelo().getNome());
            tvConsumo.setText(formato.format(registro.getConsumo()) + "Km/%");
            tvAceleracao.setText(formato.format(registro.getMediaAberturaBorboleta()) + "%");
            tvTipoCombust.setText(registro.getTipoCombustivel().getNome().toString());


            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            String dateString = sdf.format(registro.getData());


            if (dateString.charAt(1) == '/') {      //Adiciona um 0 se a data tiver apenas 1 digito no dia

                String novaData = "0" + dateString;
                dateString = novaData;
            }


            if (dateString.charAt(4) == '/') {      //Adiciona um 0 se a data tiver apenas 1 digito no mes

                String novaData = dateString.substring(0, 3) + "0" + dateString.substring(3);
                dateString = novaData;
            }


            tvData.setText(dateString);

            return view;

        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
