package ifmg.rodrigo.fuelassistant.servico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.modelo.Fabricante;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class FabricanteDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "fabricante";

    private DBCore banco;

    public FabricanteDAO(Context context) {  //Constroi a partir da classe DBCore
       banco = new DBCore(context);
    }



    public long save(Fabricante mc) {   //Merge do Objeto no banco e retorna o id do item adicionado/atualizado

        long id = mc.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put("nome", mc.getNome());

            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                String _id = String.valueOf(mc.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update fabricante set values = ... where _id=?
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


    public void delete(Fabricante mc) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(mc.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] registro.");

        } finally {
            db.close();
        }
    }


    // Consulta a lista com todos os fabricantes
    public List<Fabricante> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {

            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }

    public Fabricante findById(Long id){

        SQLiteDatabase db = banco.getReadableDatabase();

        try{
        String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
        String[] selectionArgs = new String[]{"" + id};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return null;
        }

       return montaFabricante(cursor);

        } finally {
            db.close();
        }


       // return findBySql("SELECT * FROM "+TABELA+"where codigo = "+id).get(0);

    }


    public List<Fabricante> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();

        try {
            Cursor c = db.rawQuery(sql,null);
            List<Fabricante> fabricantes = new ArrayList<Fabricante>();

            if (c.moveToFirst()) {
                do {
                    Fabricante fabricante = new Fabricante();
                    fabricantes.add(montaFabricante(c));
                    
                } while (c.moveToNext());
            }
            return fabricantes;
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de coatatos
    private List<Fabricante> toList(Cursor c) {
        List<Fabricante> fabricantes = new ArrayList<Fabricante>();

        if (c.moveToFirst()) {
            do {

                Fabricante fabricante = new Fabricante();
                fabricantes.add(montaFabricante(c));

            } while (c.moveToNext());
        }
        return fabricantes;
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

    private Fabricante montaFabricante(Cursor c){

        Fabricante fabricante = new Fabricante();
        fabricante.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        fabricante.setNome(c.getString(c.getColumnIndex("nome")));
        return fabricante;

    }




}
