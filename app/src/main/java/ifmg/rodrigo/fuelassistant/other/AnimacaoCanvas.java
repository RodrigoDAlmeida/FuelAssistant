package ifmg.rodrigo.fuelassistant.other;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Rodrigo on 28/02/2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AnimacaoCanvas {

    public static void show(View view, long animDuracao){

        //extrai as coordenadas do meio da imagem

        int cx = (view.getLeft()+ view.getRight())/2;
        int cy = (view.getLeft()+ view.getRight())/2;

        //define o arco para a aniação

        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        //cria a animação

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        //inicia a animacao

        view.setVisibility(View.VISIBLE);
        anim.setDuration(animDuracao);
        anim.start();


    }


    public static void hide(final View view, long animDuracao){

        //extrai as coordenadas do meio da imagem

        int cx = (view.getLeft()+ view.getRight())/2;
        int cy = (view.getLeft()+ view.getRight())/2;

        //define o arco para a animação

        int initialRadius = view.getHeight();

        //cria a animação

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);


        //inicia a animacao

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

        anim.setDuration(animDuracao);
        anim.start();

    }


}
