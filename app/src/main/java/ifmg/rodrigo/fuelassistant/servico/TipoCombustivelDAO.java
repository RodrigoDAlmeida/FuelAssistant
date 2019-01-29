package ifmg.rodrigo.fuelassistant.servico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.modelo.TipoCombustivel;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class TipoCombustivelDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "tipo_combustivel";

    private DBCore banco;

    public TipoCombustivelDAO(Context context) {  //Constroi a partir da classe DBCore
       banco = new DBCore(context);
    }



    public long save(TipoCombustivel tc) {   //Merge do Objeto no banco e retorna o id do item adicionado/atualizado

        long id = tc.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put("nome", tc.getNome());

            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                String _id = String.valueOf(tc.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update tipoCombustivel set values = ... where _id=?
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


    public void delete(TipoCombustivel tc) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(tc.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] registro.");

        } finally {
            db.close();
        }
    }


    // Consulta a lista com todos os tipoCombustivels
    public List<TipoCombustivel> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {

            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }

    public TipoCombustivel findById(Long id){
       SQLiteDatabase db = banco.getReadableDatabase();

        try{
        String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
        String[] selectionArgs = new String[]{"" + id};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return null;
        }

       return montaTipoCombustivel(cursor);

        } finally {
            db.close();
        }


       // return findBySql("SELECT * FROM "+TABELA+"where codigo = "+id).get(0);

    }


    public List<TipoCombustivel> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql,null);
            List<TipoCombustivel> tipoCombustivels = new ArrayList<TipoCombustivel>();

            if (c.moveToFirst()) {
                do {
                    TipoCombustivel tipoCombustivel = new TipoCombustivel();
                    tipoCombustivels.add(montaTipoCombustivel(c));
                    
                } while (c.moveToNext());
            }
            return tipoCombustivels;
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de coatatos
    private List<TipoCombustivel> toList(Cursor c) {
        List<TipoCombustivel> tipoCombustivels = new ArrayList<TipoCombustivel>();

        if (c.moveToFirst()) {
            do {

                TipoCombustivel tipoCombustivel = new TipoCombustivel();
                tipoCombustivels.add(montaTipoCombustivel(c));

            } while (c.moveToNext());
        }
        return tipoCombustivels;
    }

    // Executa um SQL
    public void execSQL(String sql) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {
            db.execSQL(sql);
        } finally {
            db.close();
        }
    }

    private TipoCombustivel montaTipoCombustivel(Cursor c){

        TipoCombustivel tipoCombustivel = new TipoCombustivel();
        tipoCombustivel.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        tipoCombustivel.setNome(c.getString(c.getColumnIndex("nome")));
        return tipoCombustivel;

    }




}
