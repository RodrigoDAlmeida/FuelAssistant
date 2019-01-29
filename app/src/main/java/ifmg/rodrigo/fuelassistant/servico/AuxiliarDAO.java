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

import ifmg.rodrigo.fuelassistant.modelo.Auxiliar;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class AuxiliarDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "auxiliar";

    private DBCore banco;

    CarroDAO cDAO;
    UsuarioDAO uDAO;
    TipoCombustivelDAO tcDAO;
    CorridaDAO coDAO;

    public AuxiliarDAO(Context context) {  //Constroi a partir da classe DBCore
       banco = new DBCore(context);
        cDAO = new CarroDAO(context);
        uDAO = new UsuarioDAO(context);
        tcDAO = new TipoCombustivelDAO(context);
        coDAO = new CorridaDAO(context);
    }




    public long save(Auxiliar a) {   //Merge do Objeto no banco e retorna o id do item adicionado/atualizado

        long id = a.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();
        try {


            ContentValues values = new ContentValues();

            values.put("combustivelInicial", a.getNvCombustAnterior());
            values.put("codTipoCombustivel", a.getTipoCombustivel().getCodigo());
            values.put("codCarro", a.getCarro().getCodigo());
            values.put("codUsuario", a.getUsuario().getCodigo());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            String dateString = sdf.format(a.getData());
            values.put("data", dateString);

            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                String _id = String.valueOf(a.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update auxiliar set values = ... where _id=?
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


    public Auxiliar getRegistroAtualByCarro(Long codCarro){

        String sql = "SELECT * FROM "+TABELA+" WHERE codCarro="+codCarro;
        List<Auxiliar> list = findBySql(sql);

        if(list.size() == 0){
            return null;
        }else{

            return list.get(0);
        }

    }

    public void delete(Auxiliar r) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(r.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] auxiliar.");

        } finally {
            db.close();
        }
    }


    // Consulta a lista com todos os auxiliars
    public List<Auxiliar> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {

            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }

    public Auxiliar findById(Long id){
       SQLiteDatabase db = banco.getReadableDatabase();

        try {
        String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
        String[] selectionArgs = new String[]{"" + id};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return null;
        }

       return montaAuxiliar(cursor);

        } finally {
            db.close();
        }

       // return findBySql("SELECT * FROM "+TABELA+"where codigo = "+id).get(0);

    }


    public List<Auxiliar> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql,null);
            List<Auxiliar> auxiliars = new ArrayList<Auxiliar>();

            if (c.moveToFirst()) {
                do {
                    Auxiliar auxiliar = new Auxiliar();
                    auxiliars.add(montaAuxiliar(c));
                    
                } while (c.moveToNext());
            }
            return auxiliars;
        } finally {
            db.close();
        }
    }

    // Lê o cursor e cria a lista de coatatos
    private List<Auxiliar> toList(Cursor c) {

        List<Auxiliar> auxiliars = new ArrayList<Auxiliar>();
        if (c.moveToFirst()) {
            do {

                auxiliars.add(montaAuxiliar(c));

            } while (c.moveToNext());
        }
        return auxiliars;
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

    private Auxiliar montaAuxiliar(Cursor c){


        Auxiliar auxiliar = new Auxiliar();
        auxiliar.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        auxiliar.setData(Date.valueOf(c.getString(c.getColumnIndex("data"))));
        auxiliar.setNvCombustAnterior(c.getDouble(c.getColumnIndex("combustivelInicial")));

        auxiliar.setCarro(cDAO.findById(c.getLong(c.getColumnIndex("codCarro"))));
        auxiliar.setTipoCombustivel(tcDAO.findById(c.getLong(c.getColumnIndex("codTipoCombustivel"))));
        auxiliar.setUsuario(uDAO.findById(c.getLong(c.getColumnIndex("codUsuario"))));


        auxiliar.setCorridas(coDAO.findBySql("SELECT * FROM CORRIDA WHERE codAuxiliar = "+auxiliar.getCodigo()+";"));

        return auxiliar;

    }




}
