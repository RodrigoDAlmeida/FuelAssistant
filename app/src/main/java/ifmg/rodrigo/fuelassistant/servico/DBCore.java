package ifmg.rodrigo.fuelassistant.servico;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rodrigo on 08/09/2016.
 */
public class DBCore extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "fas";
    private static final int VERSAO_BANCO = 1;


    public DBCore(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Criando as Tabelas no SQLITE


        // TABELA FABRICANTE
        db.execSQL("create table if not exists fabricante " +
                "(codigo integer primary key autoincrement , " +
                "nome text);");

        //TABELA MODELO CARRO
        db.execSQL("create table if not exists modelo_carro " +
                "(codigo integer primary key autoincrement, " +
                "nome text, " +
                "categoria text, " +
                "codFabricante integer, " +
                "FOREIGN KEY ('codFabricante') REFERENCES fabricante (codigo));");


        //TABELA CARRO
        db.execSQL("create table if not exists carro " +
                "(codigo integer primary key autoincrement, " +
                "anoFabricacao integer, " +
                "codModeloCarro integer, " +
                "FOREIGN KEY ('codModeloCarro') REFERENCES modelo_carro (codigo));");

        //TABELA TIPO DE COMBUSTIVEL
        db.execSQL("create table if not exists tipo_combustivel " +
                "(codigo integer primary key autoincrement, " +
                "nome text);");

        //TABELA USUARIO
        db.execSQL("create table if not exists usuario " +
                "(codigo integer primary key autoincrement, " +
                "nome text);");



        //TABELA AUXILIAR
        db.execSQL("create table if not exists auxiliar" +
                "(codigo integer primary key autoincrement, " +
                "distanciaPercorrida real, " +
                "data text," +
                "combustivelInicial real, "+
                "codTipoCombustivel integer not null, " +
                "codCarro integer not null, " +
                "codUsuario integer not null, " +
                "FOREIGN KEY ('codTipoCombustivel') REFERENCES tipo_combustivel (codigo), " +
                "FOREIGN KEY ('codCarro') REFERENCES carro (codigo), " +
                "FOREIGN KEY ('codUsuario') REFERENCES usuario (codigo));");



        //TABELA REGISTRO
        db.execSQL("create table if not exists registro " +
                "(codigo integer primary key autoincrement, " +
                "data text," +
                "distancia real, " +
                "consumo real, " +
                "mediaAceleracao real, " +
                "mediaConsumo real, "+
                "codTipoCombustivel integer not null, " +
                "codCarro integer not null, " +
                "codUsuario integer not null, " +
                "FOREIGN KEY ('codTipoCombustivel') REFERENCES tipo_combustivel (codigo), " +
                "FOREIGN KEY ('codCarro') REFERENCES carro (codigo), " +
                "FOREIGN KEY ('codUsuario') REFERENCES usuario (codigo));");


        //TABELA CORRIDA (NOVA)

        db.execSQL("create table if not exists corrida " +
                "(codigo integer primary key autoincrement, " +
                "data text, "+
                "inicio text, " +
                "fim text, " +
                "distanciaPercorrida real, " +
                "mediaAceleracao real, "+
                "mediaConsumo real, "+
                "codAuxiliar integer not null, "+
                "FOREIGN KEY ('codAuxiliar') REFERENCES auxiliar (codigo));");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
