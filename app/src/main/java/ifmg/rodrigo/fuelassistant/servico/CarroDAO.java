package ifmg.rodrigo.fuelassistant.servico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;
import ifmg.rodrigo.fuelassistant.modelo.Carro;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class CarroDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "carro";

    DBCore banco;
    ModeloCarroDAO mcDAO;


    public CarroDAO(Context context) {
        banco = new DBCore(context);
        mcDAO = new ModeloCarroDAO(context);
    }




    // Insere um novo CONTATO, ou atualiza se já existe.
    public long save(Carro mc) {
        long id = mc.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();
        try {


            ContentValues values = new ContentValues();
             values.put("anoFabricacao", mc.getAno());
             values.put("codModeloCarro", mc.getModelo().getCodigo());

            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                String _id = String.valueOf(mc.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update carro set values = ... where _id=?
                int count = db.update(TABELA, values, "codigo=?", whereArgs);

                return count;
            } else { // SE O ID FOR 0, SIGNIFICA QUE NÃO TEM ID, ASSIM VAI INSERIR O DADO
                // insert into carro values (...)
                id = db.insert(TABELA, "", values);
                return id;
            }
        } finally {
            db.close();
        }
    }

    // Deleta o CONTATO
    public int delete(Carro mc) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {
            // delete from carro where _id=?
            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(mc.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        } finally {
            db.close();
        }
    }


    // Consulta a lista com todos os carros
    public List<Carro> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            // select * from carro
            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }




    public Carro findById(Long id){
        SQLiteDatabase db = banco.getReadableDatabase();

        try{
        String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
        String[] selectionArgs = new String[]{"" + id};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return null;
        }

       return montaCarro(cursor);
        } finally {
            db.close();
        }
    }

    // Consulta por sql testar depois
    public List<Carro> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql,null);
            List<Carro> carros = new ArrayList<Carro>();

            if (c.moveToFirst()) {
                do {
                    Carro carro = new Carro();
                    carros.add(montaCarro(c));
                    
                } while (c.moveToNext());
            }
            return carros;
        } finally {
            db.close();
        }
    }




    // Lê o cursor e cria a lista de coatatos
    private List<Carro> toList(Cursor c) {
        List<Carro> carros = new ArrayList<Carro>();

        if (c.moveToFirst()) {
            do {

                Carro carro = new Carro();
                carros.add(montaCarro(c));

            } while (c.moveToNext());
        }
        return carros;
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

    public Carro montaCarro(Cursor c){

        Carro carro = new Carro();
        carro.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        carro.setAno(c.getInt(c.getColumnIndex("anoFabricacao")));
        carro.setModelo(mcDAO.findById(c.getLong(c.getColumnIndex("codModeloCarro"))));
        return carro;

    }




}
