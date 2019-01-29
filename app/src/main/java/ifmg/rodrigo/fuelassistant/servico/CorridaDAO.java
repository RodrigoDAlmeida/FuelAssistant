package ifmg.rodrigo.fuelassistant.servico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.modelo.Corrida;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class CorridaDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "corrida";

    private DBCore banco;
    private Context contexto;

    public CorridaDAO(Context context) {  //Constroi a partir da classe DBCore

        contexto = context;
       banco = new DBCore(context);

    }



    public long save(Corrida c) {   //Merge do Objeto no banco e retorna o id do item adicionado/atualizado


        long id = c.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();


        try {

            ContentValues values = new ContentValues();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");

            String dateString = sdf.format(c.getData());
            String inicioString = shf.format(c.getInicio());
            String fimString = shf.format(c.getFim());

            values.put("data", dateString);
            values.put("inicio", inicioString);
            values.put("fim", fimString);

            values.put("distanciaPercorrida",c.getDistanciaPercorrida());
            values.put("mediaAceleracao",c.getMediaAceleracao());
            values.put("codAuxiliar", c.getRegistroAtual());
            values.put("mediaConsumo", c.getMediaConsumo());



            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                String _id = String.valueOf(c.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update corrida set values = ... where _id=?
                int count = db.update(TABELA, values, "codigo=?", whereArgs);

                return count;
            } else {

                id = db.insert(TABELA, "", values);
                return id;
            }
        } finally {
            db.close();
        }
    }


    public void delete(Corrida mc) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(mc.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] registro.");

        } finally {
            db.close();
        }
    }


    public void limpaCorridaRegistro(Long codRegistro) {

        execSQL("DELETE FROM "+TABELA+" WHERE codAuxiliar="+codRegistro);

    }


    // Consulta a lista com todos os corridas
    public List<Corrida> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {

            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }

    public Corrida findById(Long id){

        SQLiteDatabase db = banco.getReadableDatabase();
        try{

        String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
        String[] selectionArgs = new String[]{"" + id};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return null;
        }

       return montaCorrida(cursor);

        } finally {
            db.close();
        }

       // return findBySql("SELECT * FROM "+TABELA+"where codigo = "+id).get(0);

    }


    public List<Corrida> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql,null);
            List<Corrida> corridas = new ArrayList<Corrida>();

            if (c.moveToFirst()) {
                do {
                    Corrida corrida = new Corrida();
                    corridas.add(montaCorrida(c));
                    
                } while (c.moveToNext());
            }
            return corridas;
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de coatatos
    private List<Corrida> toList(Cursor c) {
        List<Corrida> corridas = new ArrayList<Corrida>();

        if (c.moveToFirst()) {
            do {

                Corrida corrida = new Corrida();
                corridas.add(montaCorrida(c));

            } while (c.moveToNext());
        }
        return corridas;
    }

    // Executa um SQL
    private void execSQL(String sql) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {
            db.execSQL(sql);
        } finally {
            db.close();
        }
    }

    private Corrida montaCorrida(Cursor c){

        AuxiliarDAO aDAO = new AuxiliarDAO(contexto);
        SimpleDateFormat shf = new SimpleDateFormat("HH:mm:ss");

        Corrida corrida = new Corrida();
        corrida.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        corrida.setData(Date.valueOf(c.getString(c.getColumnIndex("data"))));

        try {

            corrida.setInicio(shf.parse(c.getString(c.getColumnIndex("inicio"))));
            corrida.setFim(shf.parse(c.getString(c.getColumnIndex("fim"))));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        corrida.setDistanciaPercorrida(c.getDouble(c.getColumnIndex("distanciaPercorrida")));
        corrida.setMediaAceleracao(c.getDouble(c.getColumnIndex("mediaAceleracao")));
        corrida.setMediaConsumo(c.getDouble(c.getColumnIndex("mediaConsumo")));
        corrida.setRegistroAtual((c.getLong(c.getColumnIndex("codAuxiliar"))));
        return corrida;


    }




}
