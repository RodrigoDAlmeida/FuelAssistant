package ifmg.rodrigo.fuelassistant.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.activity.MainActivity;
import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.modelo.Corrida;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.CorridaDAO;
import ifmg.rodrigo.fuelassistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CorridasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CorridasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CorridasFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    CorridaDAO rDAO;
    AuxiliarDAO aDAO;

    List<Corrida> corridas;
    Auxiliar registroAtual;
    View view;



    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
    SimpleDateFormat hdf = new SimpleDateFormat("HH:mm:ss");
    DecimalFormat formato = new DecimalFormat("#.#");

    private OnFragmentInteractionListener mListener;

    public CorridasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CorridasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CorridasFragment newInstance(String param1, String param2) {
        CorridasFragment fragment = new CorridasFragment();
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
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_corridas, container, false);
        rDAO = new CorridaDAO(getActivity());


        registroAtual = ((MainActivity) getActivity()).registroAtual;
        atulizaLista(view);


        TextView tvCarro = (TextView) view.findViewById(R.id.tvCorridaCarro);
        TextView tvTipoComb = (TextView) view.findViewById(R.id.tvCorridaTipoCombus);
        TextView tvData = (TextView) view.findViewById(R.id.tvListagemRegistroCarro);
        TextView tvNvCom = (TextView) view.findViewById(R.id.tvCorridaNvComb);
        TextView tvDistanc = (TextView) view.findViewById(R.id.tvCorridaDistancia);
        TextView tvMedAcele = (TextView) view.findViewById(R.id.tvCorridaMedAcele);

        tvCarro.setText(registroAtual.getCarro().getModelo().getNome());
        tvTipoComb.setText(registroAtual.getTipoCombustivel().getNome());
        tvData.setText(sdf.format(registroAtual.getData()));
        tvNvCom.setText(formato.format(registroAtual.getNvCombustAnterior())+" %");
        tvDistanc.setText(formato.format(registroAtual.getDistancia())+" Km");
        tvMedAcele.setText(formato.format(registroAtual.getMediaAberturaBorboleta())+" %");


        return view;
    }


    public void atulizaLista(View view){

        corridas = registroAtual.getCorridas();
        ListView listView = (ListView) view.findViewById(R.id.lvCorridas);
        listView.setAdapter(new CorridaAdapter(getActivity()));
        listView.setOnItemClickListener(this);
    }


    public void onItemClick(AdapterView<?> parent, final View view, final int idx, long id){
        final Corrida c = this.corridas.get(idx);

        Toasty.info(getActivity(),"Consumo Medio: "+formato.format(corridas.get(Integer.valueOf(idx)).getMediaConsumo()), Toast.LENGTH_LONG,true).show();

        new AlertDialog.Builder(getActivity())           //CRIA O DIALOG ALERT
                .setIcon(R.drawable.ic_warning_black_48dp)
                .setTitle(getResources().getString(R.string.tituloAlterarCorrida))
                .setMessage(getResources().getString(R.string.alterarCorridas))
                .setPositiveButton(getResources().getString(R.string.Excluir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {


                            rDAO.delete(c);
                            atulizaLista(view);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                })
                .setNegativeButton(getResources().getString(R.string.Tranferir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {


                            //Tranferir corrida


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                })
                .setNeutralButton(getResources().getString(R.string.cancelar), null)
                .show();



        ListView listView = (ListView) getActivity().findViewById(R.id.lvCorridas);
        listView.setAdapter(new CorridaAdapter(getActivity()));
        listView.setOnItemClickListener(this);

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




    public class CorridaAdapter extends BaseAdapter {

        private final Context context;

        public CorridaAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return corridas != null? corridas.size():0;
        }

        @Override
        public Object getItem(int position) {
            return corridas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Infla a View
            View view = LayoutInflater.from(context).inflate(R.layout.listagem_corridas,parent,false);

            //Faz findViewById das views que precisam ser atualizadas


            TextView tvData = (TextView) view.findViewById(R.id.tvListagemRegistroCarro);
            TextView tvDistancia = (TextView) view.findViewById(R.id.tvCorridaDistancia);
            TextView tvMediaAcel = (TextView) view.findViewById(R.id.tvCorridaMediaAcel);
            TextView tvInicio = (TextView) view.findViewById(R.id.tvCorridaHoraInicio);
            TextView tvFim = (TextView) view.findViewById(R.id.tvCorridaHoraFim);



            Corrida corrida = corridas.get(position);

            tvDistancia.setText(formato.format(corrida.getDistanciaPercorrida())+" Km");
            tvMediaAcel.setText(formato.format(corrida.getMediaAceleracao())+" %");




            String dateString = sdf.format(corrida.getData());
            String inicioString = hdf.format(corrida.getInicio());
            String fimString = hdf.format(corrida.getFim());

            if(dateString.charAt(1)=='/'){      //Adiciona um 0 se a data tiver apenas 1 digito no dia

                String novaData = "0"+dateString;
                dateString = novaData;
            }


            if(dateString.charAt(4)=='/'){      //Adiciona um 0 se a data tiver apenas 1 digito no mes

                String novaData = dateString.substring(0,3)+"0"+dateString.substring(3);
                dateString = novaData;
            }


            tvData.setText(dateString);
            tvInicio.setText(inicioString);
            tvFim.setText(fimString);

            return view;

        }
    }





}
