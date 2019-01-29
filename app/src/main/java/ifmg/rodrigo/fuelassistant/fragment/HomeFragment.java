package ifmg.rodrigo.fuelassistant.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;

import java.text.DecimalFormat;

import es.dmoral.toasty.Toasty;
import ifmg.rodrigo.fuelassistant.activity.MainActivity;
import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.modelo.Corrida;
import ifmg.rodrigo.fuelassistant.modelo.Registro;
import ifmg.rodrigo.fuelassistant.other.AnimacaoCanvas;
import ifmg.rodrigo.fuelassistant.other.Veiculo;
import ifmg.rodrigo.fuelassistant.R;
import ifmg.rodrigo.fuelassistant.servico.RegistroDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    TextView carroAtual;
    TextView tipoCombustivelAtual;
    TextView distanciaPercorrida;
    TextView tvNvCombustAtual;
    TextView tvConsumoAnterior;

    TextView velPura;

 //   TextView tvConsumoInstantaneo;
  //  TextView tvConsumoInstantaneoMedio;
    TextView tvAceleracao;

    ImageView ivNvInicial;
    ImageView ivNvAtual;
    ImageView ivDesconectado;

    ProgressBar pbCarregando;

    RegistroDAO rDAO;


    //Componentes que alternam de invisivel pra visivel

    ImageView ivParado;
    private SpeedometerGauge speedometer;


    DecimalFormat formato = new DecimalFormat("#.#");


    //Variaveis OBD
    Veiculo veiculo = new Veiculo();


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


        View view = inflater.inflate(R.layout.fragment_home, container, false);


        rDAO = new RegistroDAO(getActivity());
        speedometer = (SpeedometerGauge) view.findViewById(R.id.speedometer);   //Configura o Velocimetro
        speedometer.setMaxSpeed(300);
        speedometer.setMajorTickStep(30);
        speedometer.setMinorTicks(2);
        speedometer.setLabelTextSize(20);
        speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });


        carroAtual = (TextView) view.findViewById(R.id.tvVeiculoAtual);
        tipoCombustivelAtual = (TextView) view.findViewById(R.id.tvTipoCombustivel);


        distanciaPercorrida = (TextView) view.findViewById(R.id.tvDistanciaPercorrida);  //Customiza a fonte
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/digital7.ttf");
        distanciaPercorrida.setTypeface(custom_font);

        tvNvCombustAtual = (TextView) view.findViewById(R.id.tvNvCombustAtual);
        tvConsumoAnterior = (TextView) view.findViewById(R.id.tvConsumoAnterior);
        ivNvAtual = (ImageView) view.findViewById(R.id.ivNvAtual);
        pbCarregando = (ProgressBar) view.findViewById(R.id.pbCarregando);
        tvNvCombustAtual.setText("--");
      //  tvConsumoInstantaneo = (TextView) view.findViewById(R.id.tvConsumoInstantaneo);
       // tvConsumoInstantaneoMedio = (TextView) view.findViewById(R.id.tvConsumoInstantaneoMedio);
        tvAceleracao = (TextView) view.findViewById(R.id.tvAceleracao);
        velPura = (TextView) view.findViewById(R.id.tvVelocidadePura);


        resetaConsumoInstantaneo(((MainActivity)getActivity()).registroAtual);

        ivParado = (ImageView) view.findViewById(R.id.ivParado);
        ivDesconectado = (ImageView) view.findViewById(R.id.ivDesconectado);
        pbCarregando = (ProgressBar) view.findViewById(R.id.pbCarregando);


        ((MainActivity)getActivity()).atualizaFragment(this);  //Envia uma cópia da instancia para que a Activity pai possa invocar os metodos deste fragment

        if(((MainActivity) getActivity()).running){       //Caso já esteja conectado, exibe a interface Online

            conectaSemAnimacao();
        }


        return view;
    }


public void setaPuraVel(String vel){

    this.velPura.setText(vel);
}

public void resetaConsumoInstantaneo(Auxiliar registroAtual){

   // tvConsumoInstantaneo.setText("-- Km/L");
    //tvConsumoInstantaneoMedio.setText(formato.format(registroAtual.getMediaConsumoTotal())+" Km/L");
}


    public void atualizaInformacoes(Auxiliar registroAtual) {
        try {

            atualizaCampos(registroAtual);


         //   tvConsumoInstantaneoMedio.setText(formato.format(registroAtual.getMediaConsumoTotal())+" Km/L");

            carroAtual.setText(registroAtual.getCarro().getModelo().getNome());
            tipoCombustivelAtual.setText(registroAtual.getTipoCombustivel().getNome().toString());

        } catch (Exception e) {
            toast(e.getMessage());
            return;
        }
    }

    public void atualizaCampos(Auxiliar registroAtual, Double nvAtual) {



        Double distanciaTotal = registroAtual.getDistancia();
        distanciaPercorrida.setText(formato.format(distanciaTotal) + " Km");

        tvNvCombustAtual.setText(formato.format(nvAtual) + "%");
        atualizaIconeCombustivel(nvAtual);

        try {
            if (((MainActivity) getActivity()).running) {

                Double combustiConsumido = registroAtual.getNvCombustAnterior() - nvAtual;

                if ((distanciaTotal > 0) && (combustiConsumido > 0)) {

                    Double consumoAtual = distanciaTotal / (combustiConsumido);
                    tvConsumoAnterior.setText(formato.format(consumoAtual) + " Km/%");
                } else {

                    tvConsumoAnterior.setText("-- Km/%");
                }

            } else {
                tvConsumoAnterior.setText("-- Km/%");
            }
        }catch (Exception e){

        }



    }



    public void atualizaCampos(Auxiliar registroAtual) {

        Double distanciaTotal = registroAtual.getDistancia();
        distanciaPercorrida.setText(formato.format(distanciaTotal) + " Km");
        tvNvCombustAtual.setText(" -- %");
        atualizaIconeCombustivel(-1d);

        try {
            if (((MainActivity) getActivity()).running) {

                Double combustiConsumido = registroAtual.getNvCombustAnterior() - veiculo.getNvCombustivel();


                if ((distanciaTotal > 0) && (combustiConsumido > 0)) {

                    Double consumoAtual = registroAtual.getDistancia() / combustiConsumido;
                    tvConsumoAnterior.setText(formato.format(consumoAtual) + " Km/%");
                } else {

                    tvConsumoAnterior.setText("-- Km/%");
                }

            } else {
                tvConsumoAnterior.setText("-- Km/%");
            }
        }catch (Exception e){

        }


    }

    public void desconecta() {

        if(ivDesconectado.getVisibility()== View.INVISIBLE){
            AnimacaoCanvas.show(ivDesconectado, 2000);
            speedometer.clearColoredRanges();

        }

        ivParado.setImageResource(R.drawable.off);
        tvNvCombustAtual.setText(" -- %");
        ivNvAtual.setImageResource(R.drawable.g50_black);
        tvAceleracao.setText(" -- %");

    }



    public void conecta(){

        AnimacaoCanvas.hide(ivDesconectado, 2000);
        Toasty.success(getActivity() ,getResources().getString(R.string.conectadoSucesso),Toast.LENGTH_LONG, true).show();
        conectaSemAnimacao();
        atualizaCampos(((MainActivity) getActivity()).registroAtual);


    }



    public void conectaSemAnimacao(){   //Função é chamada quando o fragment Home é invocado enquanto o Veiculo estiver conectado ao App

        ivDesconectado.setVisibility(View.INVISIBLE);

        Veiculo.Estado veiculoEstado = ((MainActivity)getActivity()).veiculo.getEstado();

        if(veiculoEstado == Veiculo.Estado.PARADO){

            ivParado.setImageResource(R.drawable.parado);
        }else{
            if(veiculoEstado == Veiculo.Estado.MOVIMENTO){

                ivParado.setImageResource(R.drawable.corrida);
            }else{

                ivParado.setImageResource(R.drawable.abastecendo);
            }
        }


        // Configure value range colors
        speedometer.addColoredRange(5, 140, Color.GREEN);
        speedometer.addColoredRange(140, 180, Color.YELLOW);
        speedometer.addColoredRange(180, 400, Color.RED);



    }



    public void atualizaIconeCombustivel(Double nvCombusAtual) {

        //Altera a imagem do ponteiro de combustivel

        if (nvCombusAtual == -1) {

            ivNvAtual.setImageResource(R.drawable.g50_black);

        }else{

            if (nvCombusAtual.intValue() > 83) {

                ivNvAtual.setImageResource(R.drawable.g100);

            } else {

                if (nvCombusAtual.intValue() > 66) {

                    ivNvAtual.setImageResource(R.drawable.g83);
                } else {

                    if (nvCombusAtual.intValue() > 50) {

                        ivNvAtual.setImageResource(R.drawable.g66);
                    } else {

                        if (nvCombusAtual.intValue() > 33) {

                            ivNvAtual.setImageResource(R.drawable.g50);
                        } else {

                            if (nvCombusAtual.intValue() > 16) {

                                ivNvAtual.setImageResource(R.drawable.g33);
                            } else {

                                ivNvAtual.setImageResource(R.drawable.g0);
                            }


                        }
                    }
                }
            }
        }
    }


    public void alteraAnimacao(char anim) {

        switch (anim) {

            case 'P': {

                ivParado.setImageResource(R.drawable.parado);

                break;
            }

            case 'M': {

                ivParado.setImageResource(R.drawable.corrida);

                break;
            }

            case 'A': {

                ivParado.setImageResource(R.drawable.abastecendo);

                break;
            }


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

    private void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void atualizaVelocimetro(Integer velocidadeAtual,Double aceleracao) {

        speedometer.setSpeed(velocidadeAtual, 500, 0);
        tvAceleracao.setText(" "+formato.format(aceleracao)+" %");

    }

    public void atualizaDistanciaEVolumeAbastecido(double distancia, double nvCombustivelAnterior ,double nvCombustivelAtual, Corrida corrida) {

        distanciaPercorrida.setText(formato.format(distancia) + " Km");
        tvNvCombustAtual.setText(formato.format(nvCombustivelAtual) + "%");
        atualizaIconeCombustivel(nvCombustivelAtual);

     //   tvConsumoInstantaneo.setText(formato.format(corrida.consumoAtual)+" Km/L");
      //  tvConsumoInstantaneoMedio.setText(formato.format(corrida.mediaConsumoParcial())+" Km/L");

        Double consumoAtual = distancia / (nvCombustivelAnterior - nvCombustivelAtual);
        tvConsumoAnterior.setText(formato.format(consumoAtual)+" Km/%");
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
