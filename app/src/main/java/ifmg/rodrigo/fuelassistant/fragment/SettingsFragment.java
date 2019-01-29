package ifmg.rodrigo.fuelassistant.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.activity.MainActivity;
import ifmg.rodrigo.fuelassistant.modelo.Carro;
import ifmg.rodrigo.fuelassistant.modelo.Registro;
import ifmg.rodrigo.fuelassistant.modelo.TipoCombustivel;
import ifmg.rodrigo.fuelassistant.modelo.Usuario;
import ifmg.rodrigo.fuelassistant.servico.CarroDAO;
import ifmg.rodrigo.fuelassistant.servico.RegistroDAO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    CheckBox modoSimulador;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // NOTIFICAÇÔES


        // FIM NOTIFICAÇôES


        // UNIDADE CONSUMO


        Spinner spUnidadeConsm = (Spinner) view.findViewById(R.id.spSettingsUnidadeConsum);

        List<String> tipoUnidadeConsumo = new ArrayList<>();
        tipoUnidadeConsumo.add(getString(R.string.KmPerPorcentagem));
        tipoUnidadeConsumo.add("Km/L");
        tipoUnidadeConsumo.add("MPG");
        tipoUnidadeConsumo.add("100Km/L");


        ArrayAdapter<String> adaptadorU = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, tipoUnidadeConsumo);
        adaptadorU.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidadeConsm.setAdapter(adaptadorU);


        // FIM UNIDADE CONSUMO


        //  MODO SIMULADOR

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Integer valorModo = sharedPref.getInt("modoSimulador", 0);

        modoSimulador = (CheckBox) view.findViewById(R.id.cbModoSimulador);
        modoSimulador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                RegistroDAO rDAO = new RegistroDAO(getActivity());
                Carro c = ((MainActivity)getActivity()).registroAtual.getCarro();
                TipoCombustivel tc = ((MainActivity)getActivity()).registroAtual.getTipoCombustivel();
                Usuario u = ((MainActivity)getActivity()).registroAtual.getUsuario();

                Registro r = new Registro();
                r.setData(Date.valueOf("2017-02-05"));
                r.setDistancia(500d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(40d);
                r.setConsumo(19.12d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-11"));
                r.setDistancia(720.3d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(48d);
                r.setConsumo(21.12d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-14"));
                r.setDistancia(652.7d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(45d);
                r.setConsumo(22.12d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-17"));
                r.setDistancia(442.7d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(41d);
                r.setConsumo(20.12d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-19"));
                r.setDistancia(442.7d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(48d);
                r.setConsumo(25.51d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-21"));
                r.setDistancia(832.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(58d);
                r.setConsumo(17.51d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-25"));
                r.setDistancia(912.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(61d);
                r.setConsumo(18.51d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-27"));
                r.setDistancia(212.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(45d);
                r.setConsumo(24.12d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-02-29"));
                r.setDistancia(612.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(51d);
                r.setConsumo(22.47d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-02"));
                r.setDistancia(412.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(41.2d);
                r.setConsumo(23.47d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-05"));
                r.setDistancia(512.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(45.5d);
                r.setConsumo(20.47d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-07"));
                r.setDistancia(712.72d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(55.5d);
                r.setConsumo(19.91d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-12"));
                r.setDistancia(352.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(47.5d);
                r.setConsumo(24.91d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-15"));
                r.setDistancia(522.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(47.5d);
                r.setConsumo(24.91d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-19"));
                r.setDistancia(472.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(29.5d);
                r.setConsumo(21.91d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-22"));
                r.setDistancia(272.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(49.5d);
                r.setConsumo(16.91d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-03-25"));
                r.setDistancia(272.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(39.5d);
                r.setConsumo(15.29d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);


                r = new Registro();
                r.setData(Date.valueOf("2017-03-29"));
                r.setDistancia(712.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(79.5d);
                r.setConsumo(13.29d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-04-01"));
                r.setDistancia(123.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(42.5d);
                r.setConsumo(25.29d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-04-04"));
                r.setDistancia(723.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(48.5d);
                r.setConsumo(24.14d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-04-06"));
                r.setDistancia(523.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(61.5d);
                r.setConsumo(22.54d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-04-09"));
                r.setDistancia(923.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(21.5d);
                r.setConsumo(21.44d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);

                r = new Registro();
                r.setData(Date.valueOf("2017-04-15"));
                r.setDistancia(423.41d);
                r.setCarro(c);
                r.setMediaAberturaBorboleta(45.5d);
                r.setConsumo(20.42d);
                r.setTipoCombustivel(tc);
                r.setUsuario(u);
                rDAO.save(r);




                if (modoSimulador.isChecked()) {
                    editor.putInt("modoSimulador", 1);
                    ((MainActivity) getActivity()).modoSimulador = true;
                } else {
                    editor.putInt("modoSimulador", 0);
                    ((MainActivity) getActivity()).modoSimulador = false;
                }

                editor.commit();
                if (((MainActivity) getActivity()).running) {
                    ((MainActivity) getActivity()).desconecta(false);
                }
            }
        });
        if (valorModo == 1) {
            modoSimulador.setChecked(true);
        } else {
            modoSimulador.setChecked(false);
        }


        TextView tvHelpSimulador = (TextView) view.findViewById(R.id.tvHelpSimulador);
        tvHelpSimulador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.AjudaSimulador))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();





            }
        });


        TextView tvModoSimulador = (TextView) view.findViewById(R.id.tvModoSimulador);
        tvModoSimulador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.AjudaSimulador))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        // FIM MODO SIMULADOR


        return view;

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
