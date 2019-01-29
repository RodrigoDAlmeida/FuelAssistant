package ifmg.rodrigo.fuelassistant.servico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.modelo.Registro;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class RegistroDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "registro";

    private DBCore banco;

    CarroDAO cDAO;
    UsuarioDAO uDAO;
    TipoCombustivelDAO tcDAO;

    public RegistroDAO(Context context) {  //Constroi a partir da classe DBCore
        banco = new DBCore(context);
        cDAO = new CarroDAO(context);
        uDAO = new UsuarioDAO(context);
        tcDAO = new TipoCombustivelDAO(context);
    }


    public long save(Registro r) {   //Merge do Objeto no banco e retorna o id do item adicionado/atualizado

        long id = r.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();
        try {


            ContentValues values = new ContentValues();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            String dateString = sdf.format(r.getData());
            values.put("data", dateString);
            values.put("distancia", r.getDistancia());
            values.put("consumo", r.getConsumo());
            values.put("mediaAceleracao", r.getMediaAberturaBorboleta());
            values.put("mediaConsumo", r.getMediaConsumo());
            values.put("codTipoCombustivel", r.getTipoCombustivel().getCodigo());
            values.put("codCarro", r.getCarro().getCodigo());
            values.put("codUsuario", r.getUsuario().getCodigo());

            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                System.out.println("Entrou !!");
                String _id = String.valueOf(r.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update registro set values = ... where _id=?
                int count = db.update(TABELA, values, "codigo=?", whereArgs);

                return count;
            } else {

                id = db.insert(TABELA, "", values);
                System.out.println("Deu certo !! \n " + values);
                return id;
            }
        } finally {
            db.close();
        }
    }


    public void delete(Registro r) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(r.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] registro.");

        } finally {
            db.close();
        }
    }

    public List<Registro> findByCarro(Long codCarro){

        String sql = "SELECT * FROM "+TABELA+" WHERE codCarro="+codCarro;
        return findBySql(sql);
    }


    // Consulta a lista com todos os registros
    public List<Registro> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {

            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }

    public Registro findById(Long id) {

        SQLiteDatabase db = banco.getReadableDatabase();
        try{
        String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
        String[] selectionArgs = new String[]{"" + id};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return null;
        }

        return montaRegistro(cursor);

        } finally {
            db.close();
        }

    }

    public Double findUltimoConsumoPorVeiculo(Long codCarro){
/*
        SQLiteDatabase db = banco.getReadableDatabase();
        try {

            String sql = "SELECT MAX(data),consumo FROM " + TABELA + " WHERE codCarro = ?";
            String[] selectionArgs = new String[]{"" + codCarro};
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            cursor.moveToFirst();

            return Double.valueOf(cursor.getDouble(cursor.getColumnIndex("consumo")));
        }finally {
            db.close();
        }


        */

        List<Registro> registros = findBySql("SELECT MAX(data),consumo FROM " + TABELA + " WHERE codCarro ="+codCarro);
        return registros.get(0).getConsumo();
    }



    public Date findUltimaDataPorVeiculo(Long idCarro) {
        SQLiteDatabase db = banco.getReadableDatabase();
        String sql = "SELECT MAX(data) FROM " + TABELA + " WHERE codCarro = ?";
        String[] selectionArgs = new String[]{"" + idCarro};
        try {
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            cursor.moveToFirst();


        if (cursor.getCount() == 0) {
            return null;
        }


        return Date.valueOf(cursor.getString(cursor.getColumnIndex("MAX(data)")));

        }   finally {
            db.close();
        }

        // return findBySql("SELECT * FROM "+TABELA+"where codigo = "+id).get(0);

    }


    public List<Registro> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql, null);
            List<Registro> registros = new ArrayList<Registro>();

            if (c.moveToFirst()) {
                do {
                    Registro registro = new Registro();
                    registros.add(montaRegistro(c));

                } while (c.moveToNext());
            }
            return registros;
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de coatatos
    private List<Registro> toList(Cursor c) {
        List<Registro> registros = new ArrayList<Registro>();

        if (c.moveToFirst()) {
            do {

                Registro registro = new Registro();
                registros.add(montaRegistro(c));

            } while (c.moveToNext());
        }
        return registros;
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

    private Registro montaRegistro(Cursor c) {

        Registro registro = new Registro();
        registro.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        registro.setData(Date.valueOf(c.getString(c.getColumnIndex("data"))));
        registro.setConsumo(c.getDouble(c.getColumnIndex("consumo")));
        registro.setDistancia(c.getDouble(c.getColumnIndex("distancia")));
        registro.setMediaAberturaBorboleta(c.getDouble(c.getColumnIndex("mediaAceleracao")));
        registro.setMediaConsumo(c.getDouble(c.getColumnIndex("mediaConsumo")));

        registro.setCarro(cDAO.findById(c.getLong(c.getColumnIndex("codCarro"))));
        registro.setTipoCombustivel(tcDAO.findById(c.getLong(c.getColumnIndex("codTipoCombustivel"))));
        registro.setUsuario(uDAO.findById(c.getLong(c.getColumnIndex("codUsuario"))));


        return registro;

    }

}
