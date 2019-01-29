package ifmg.rodrigo.fuelassistant.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.activity.RelatorioGrafico;
import ifmg.rodrigo.fuelassistant.modelo.Carro;
import ifmg.rodrigo.fuelassistant.modelo.TipoCombustivel;
import ifmg.rodrigo.fuelassistant.servico.CarroDAO;
import ifmg.rodrigo.fuelassistant.servico.TipoCombustivelDAO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RelatoriosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RelatoriosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelatoriosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    CarroDAO cDAO;
    Spinner comboCarro;
    List<Carro> carros;

    boolean opcaoComAno = false;

    TipoCombustivelDAO tcDAO;
    Spinner comboTipoCombust;
    List<TipoCombustivel> tiposCombust;

    Spinner comboAgrupamento;

    TextView tvAno;
    EditText etAno;


    Button gerarRelatorio;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RelatoriosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RelatoriosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RelatoriosFragment newInstance(String param1, String param2) {
        RelatoriosFragment fragment = new RelatoriosFragment();
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
        View view = inflater.inflate(R.layout.fragment_relatorios, container, false);




        tvAno = (TextView) view.findViewById(R.id.tvAno);
        etAno = (EditText) view.findViewById(R.id.etRelatorioAno);

        gerarRelatorio = (Button) view.findViewById(R.id.btRelatorioGerarRelatorio);
        gerarRelatorio.setOnClickListener(onGerarRelatorio());

        cDAO = new CarroDAO(getActivity());
        comboCarro = (Spinner) view.findViewById(R.id.spRelatorioVeiculo);
        carros = cDAO.findAll();
        carros = cDAO.findAll();
        String[] listaCarros = new String[carros.size()];
        String[] listaFabricantes = new String[carros.size()];

        for (int i = 0; i < listaCarros.length; i++) {  //Converte Lista de Carros para Lista de String
            listaCarros[i] = carros.get(i).getModelo().getNome();
            listaFabricantes[i] = carros.get(i).getModelo().getFabricante().getNome();  //Recupera a lista de fabricante para desenhar a logo no spinner
        }

        /*
        //Cria o adaptador p/  Carros
        ArrayAdapter<String> adaptadorC = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaCarros);
        adaptadorC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboCarro.setAdapter(adaptadorC);
*/


         //Monta o Adpater para o Spinner de Veiculos que contem imagens
        CustomAdapter customAdapter = new CustomAdapter(getContext(), listaCarros, listaFabricantes);
        comboCarro.setAdapter(customAdapter);

        //Cria o adaptador p/  TipoCombustivel
        tcDAO = new TipoCombustivelDAO(getActivity());
        comboTipoCombust = (Spinner) view.findViewById(R.id.spRelatorioTipoCombus);
        tiposCombust = tcDAO.findAll();
        String[] listaTiposCombust = new String[tiposCombust.size()];

        for (int i = 0; i < listaTiposCombust.length; i++) {
            listaTiposCombust[i] = tiposCombust.get(i).getNome();
        }


        ArrayAdapter<String> adaptadorTC = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaTiposCombust);
        adaptadorTC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboTipoCombust.setAdapter(adaptadorTC);


        comboAgrupamento = (Spinner) view.findViewById(R.id.spRelatorioAgrupamento);
        String[] agrupamentos = new String[]{"Todos", "Semanal", "Mensal", "Anual"};
        ArrayAdapter<String> adaptadorA = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, agrupamentos);
        adaptadorA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboAgrupamento.setAdapter(adaptadorA);


        comboAgrupamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {    //Habilita a caixa para inserir o ano caso as opções Semanal ou Mensal sejam selecionadas
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                int item = comboAgrupamento.getSelectedItemPosition();

                if ((item == 1) || (item == 2)) {

                    tvAno.setVisibility(View.VISIBLE);
                    etAno.setVisibility(View.VISIBLE);
                    opcaoComAno = true;
                } else {

                    tvAno.setVisibility(View.INVISIBLE);
                    etAno.setVisibility(View.INVISIBLE);
                    opcaoComAno = false;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        return view;
    }


    private View.OnClickListener onGerarRelatorio() {
        return new Button.OnClickListener() {
            public void onClick(View v) {


                Carro veiculoSelecionado = carros.get(comboCarro.getSelectedItemPosition());
                TipoCombustivel tipoCombSelecionado = tiposCombust.get(comboTipoCombust.getSelectedItemPosition());
                String agrupamento = comboAgrupamento.getSelectedItem().toString();

                try {

                    Intent intent = new Intent(getContext(), RelatorioGrafico.class);
                    Bundle params = new Bundle();
                    params.putString("carro", veiculoSelecionado.getCodigo() + "");
                    params.putString("tipoCombustivel", tipoCombSelecionado.getCodigo() + "");
                    params.putString("agrupamento", agrupamento);

                    if (opcaoComAno) {
                        Integer ano = Integer.valueOf(etAno.getText().toString());
                        params.putInt("ano", ano);
                    }

                    intent.putExtras(params);
                    startActivity(intent);

                } catch (Exception e) {

                    Toasty.error(getActivity(),getResources().getString(R.string.todosCamposDevemSerPreenchidos), Toast.LENGTH_SHORT,true).show();
                    Log.d("ERRO", e.toString());
                }


            }


        };
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



    class CustomAdapter extends BaseAdapter {
        Context context;
        int logos[];
        String[] veiculos;
        String[] fabricantes;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, String[] veiculos, String[] fabricantes) {
            this.context = applicationContext;
            this.logos = new int[veiculos.length];
            this.veiculos = veiculos;
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

            names.setText(veiculos[i]);
            return view;
        }
}
}

