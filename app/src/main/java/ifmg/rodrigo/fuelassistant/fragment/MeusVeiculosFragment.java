package ifmg.rodrigo.fuelassistant.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.activity.MainActivity;
import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.servico.AuxiliarDAO;
import ifmg.rodrigo.fuelassistant.servico.RegistroDAO;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.modelo.Carro;
import ifmg.rodrigo.fuelassistant.servico.CarroDAO;
import ifmg.rodrigo.fuelassistant.servico.TipoCombustivelDAO;
import ifmg.rodrigo.fuelassistant.servico.UsuarioDAO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeusVeiculosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeusVeiculosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeusVeiculosFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    CarroDAO cDAO;
    RegistroDAO rDAO;
    List<Carro> carros;

    Button addCarros;
    ListView listView;

    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");



    public MeusVeiculosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeusVeiculosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeusVeiculosFragment newInstance(String param1, String param2) {
        MeusVeiculosFragment fragment = new MeusVeiculosFragment();
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
        final View view =  inflater.inflate(R.layout.fragment_meus_veiculos, container, false);


        cDAO = new CarroDAO(getActivity());
        rDAO = new RegistroDAO(getActivity());

        carros = cDAO.findAll();
        listView = (ListView) view.findViewById(R.id.lvListagemCarros);
        listView.setAdapter(new CarroAdapter(getActivity()));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                new AlertDialog.Builder(getActivity())           //CRIA O DIALOG ALERT
                        .setIcon(R.drawable.ic_warning_black_48dp)
                        .setTitle(getResources().getString(R.string.tituloExcluirVeiculo))
                        .setMessage(getResources().getString(R.string.msgExcluirVeiculo))
                        .setPositiveButton(getResources().getString(R.string.Excluir), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Carro c = carros.get(pos);

                                if(((MainActivity)getActivity()).registroAtual.getCarro().getCodigo() == c.getCodigo()){  //Se o carro que será exluido for o selecionado atual, seleciona o primeiro outro carro da lista

                                    ((MainActivity)getActivity()).registroAtual = new AuxiliarDAO(getActivity()).findAll().get(0);
                                }

                                if(carros.size() !=1) {

                                    try {

                                        cDAO.delete(c);
                                        atualizaLista();
                                        Toasty.success(getActivity(), getResources().getString(R.string.VeiculoExcluido), Toast.LENGTH_SHORT, true).show();


                                        AuxiliarDAO aDAO = new AuxiliarDAO(getActivity());
                                        aDAO.delete(aDAO.getRegistroAtualByCarro(c.getCodigo()));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    Toasty.error(getActivity(), getResources().getString(R.string.MsgErroDeveHaverPeloMenosUmVeiculo), Toast.LENGTH_SHORT, true).show();
                                }

                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.Cancelar),null)
                        .show();



                return true;
            }
        });

        ((MainActivity)getActivity()).fab.callOnClick();
        return view;
    }

    public void atualizaLista(){

        carros = cDAO.findAll();
        listView.setAdapter(new CarroAdapter(getActivity()));

    }

    @Override
    public void onResume() {
        super.onResume();
        atualizaLista();
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



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int idx, long id) {
        Carro c = this.carros.get(idx);

        AuxiliarDAO aDAO = new AuxiliarDAO(getActivity());

        if(aDAO.getRegistroAtualByCarro(c.getCodigo()) == null){    //Se não há registro ainda para este veículo

            UsuarioDAO uDAO = new UsuarioDAO(getActivity());
            TipoCombustivelDAO tcDAO = new TipoCombustivelDAO(getActivity());

            Auxiliar novoRegistro = new Auxiliar();
            novoRegistro.setCarro(c);
            novoRegistro.setData(new Date());
            novoRegistro.setNvCombustAnterior(0);
            novoRegistro.setTipoCombustivel(tcDAO.findAll().get(0));
            novoRegistro.setUsuario(uDAO.findAll().get(0));
            novoRegistro.setCodigo(aDAO.save(novoRegistro));


        }

        Auxiliar registroAtual = aDAO.getRegistroAtualByCarro(c.getCodigo());
        ((MainActivity) getActivity()).registroAtual = registroAtual;

        Toasty.info(getActivity(),getResources().getString(R.string.VeiculoAlterado)+":  "+c.getModelo().getNome(),Toast.LENGTH_SHORT,true).show();



        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("registroAtual", Integer.valueOf(registroAtual.getCodigo()+""));
        editor.commit();

        ((MainActivity) getActivity()).atualizaComponentes();
        getActivity().onBackPressed();


    }


    public class CarroAdapter extends BaseAdapter {

        private final Context context;

        public CarroAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return carros != null ? carros.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return carros.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Infla a View
            View view = LayoutInflater.from(context).inflate(R.layout.listagem_carros, parent, false);

            //Faz findViewById das views que precisam ser atualizadas

            ImageView ivLogo = (ImageView) view.findViewById(R.id.ivListagemCarroLogo); 
            TextView tvModelo = (TextView) view.findViewById(R.id.tvListagemCarroModelo);
            TextView tvCategoria = (TextView) view.findViewById(R.id.tvVeiculosCategoria);
            TextView tvAno = (TextView) view.findViewById(R.id.tvListagemCarroAno);
            TextView tvUltimoRegistro = (TextView) view.findViewById(R.id.tvVeiculosDataUltimoRegistro);


            Carro carro = carros.get(position);
            tvModelo.setText(carro.getModelo().getNome());
            tvCategoria.setText(carro.getModelo().getCategoria().toString());
            tvAno.setText(String.valueOf(carro.getAno()));

            Date ultimoRegistro = null;
            try {

                ultimoRegistro = rDAO.findUltimaDataPorVeiculo(carro.getCodigo());

            }catch (Exception e){

            }
            if(ultimoRegistro != null){

                tvUltimoRegistro.setText(getResources().getString(R.string.ultimoRegistro)+": "+sdf.format(ultimoRegistro));
            }else {

                tvUltimoRegistro.setText("");
            }


            switch (carro.getModelo().getFabricante().getNome().toUpperCase()){      //Adiciona a logo 
                case "FORD": {
                    ivLogo.setImageResource(R.drawable.logo_ford);
                    break;
                }

                case "FIAT": {
                    ivLogo.setImageResource(R.drawable.logo_fiat);
                    break;
                }

                case "CHEVROLET": {
                    ivLogo.setImageResource(R.drawable.logo_chevrolet);
                    break;
                }

                case "VOLKSWAGEN": {
                    ivLogo.setImageResource(R.drawable.logo_volkswagen);
                    break;
                }

                case "RENAULT": {
                    ivLogo.setImageResource(R.drawable.logo_renault);
                    break;
                }

                case "HYUNDAI": {
                    ivLogo.setImageResource(R.drawable.logo_hyundai);
                    break;
                }

                case "TOYOTA": {
                    ivLogo.setImageResource(R.drawable.logo_toyota);
                    break;
                }

                case "HONDA": {
                    ivLogo.setImageResource(R.drawable.logo_honda);
                    break;
                }

                case "NISSAN": {
                    ivLogo.setImageResource(R.drawable.logo_nissan);
                    break;
                }

                case "MITSUBISHI": {
                    ivLogo.setImageResource(R.drawable.logo_mitsubishi);
                    break;
                }

                case "CITROËN": {
                    ivLogo.setImageResource(R.drawable.logo_citroen);
                    break;
                }

                case "PEUGEOT": {
                    ivLogo.setImageResource(R.drawable.logo_peugeot);
                    break;
                }

                case "KIA": {
                    ivLogo.setImageResource(R.drawable.logo_kia);
                    break;
                }

                case "MERCEDES-BENZ": {
                    ivLogo.setImageResource(R.drawable.logo_mercedes);
                    break;
                }

                case "BMW": {
                    ivLogo.setImageResource(R.drawable.logo_bmw);
                    break;
                }

                case "AUDI": {
                    ivLogo.setImageResource(R.drawable.logo_audi);
                    break;
                }

                case "CHERY": {
                    ivLogo.setImageResource(R.drawable.logo_chery);
                    break;
                }


                case "LAND ROVER": {
                    ivLogo.setImageResource(R.drawable.logo_landrover);
                    break;
                }


                case "JAC": {
                    ivLogo.setImageResource(R.drawable.logo_jacmotors);
                    break;
                }

                case "SUZUKI": {
                    ivLogo.setImageResource(R.drawable.logo_suzuki);
                    break;
                }

                case "GMC": {
                    ivLogo.setImageResource(R.drawable.logo_gmc);
                    break;
                }

                case "JEEP": {
                    ivLogo.setImageResource(R.drawable.logo_jeep);
                    break;
                }

                default:{
                    ivLogo.setImageResource(R.drawable.ic_help_black_24dp);
                }


            }

            return view;

        }


    }



}
