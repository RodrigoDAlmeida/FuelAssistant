package ifmg.rodrigo.fuelassistant.servico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ifmg.rodrigo.fuelassistant.modelo.CategoriaEnum;
import ifmg.rodrigo.fuelassistant.modelo.ModeloCarro;

/**
 * Created by Rodrigo on 15/08/2016.
 */
public class ModeloCarroDAO {


    private static final String TAG = "sql";
    private static final String TABELA = "modelo_carro";

    private DBCore banco;
    private FabricanteDAO fDB;

    public ModeloCarroDAO(Context context) {
        banco = new DBCore(context);
        fDB = new FabricanteDAO(context);
    }


    // Insere um novo Modelo, ou atualiza se já existe.
    public long save(ModeloCarro mc) {
        long id = mc.getCodigo();
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put("nome", mc.getNome());
            values.put("categoria", mc.getCategoria().toString());
            values.put("codFabricante", mc.getFabricante().getCodigo());


            if (id != 0) {//SE O ID É DIFERENTE DE 0 ATUALIZA,

                String _id = String.valueOf(mc.getCodigo());
                String[] whereArgs = new String[]{_id};

                // update modelocarro set values = ... where _id=?
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


    public int delete(ModeloCarro mc) {
        SQLiteDatabase db = banco.getWritableDatabase();
        try {

            int count = db.delete(TABELA, "codigo=?", new String[]{String.valueOf(mc.getCodigo())});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        } finally {
            db.close();
        }
    }


    // Consulta a lista com todos os Modelos
    public List<ModeloCarro> findAll() {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            // select * from modelocarro
            Cursor c = db.query(TABELA, null, null, null, null, null, null, null);

            return toList(c);
        } finally {
            db.close();
        }
    }

    public List<ModeloCarro> findByFabricante(Long codFabricante){

        String sql = "SELECT * FROM "+TABELA+" WHERE codFabricante="+codFabricante;
        return findBySql(sql);
    }


    // Consulta por sql testar depois
    public List<ModeloCarro> findBySql(String sql) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql, null);
            List<ModeloCarro> modelosCarro = new ArrayList<ModeloCarro>();

            if (c.moveToFirst()) {
                do {

                    modelosCarro.add(montaModeloCarro(c));

                } while (c.moveToNext());
            }
            return modelosCarro;
        } finally {
            db.close();
        }
    }

    public ModeloCarro findById(Long id) {

        SQLiteDatabase db = banco.getReadableDatabase();
        try {
            String sql = "SELECT * FROM " + TABELA + " WHERE codigo = ?";
            String[] selectionArgs = new String[]{"" + id};
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            cursor.moveToFirst();

            if (cursor.getCount() == 0) {
                return null;
            }

            return montaModeloCarro(cursor);

        } finally {
            db.close();
        }


    }


    // Lê o cursor e cria a lista de coatatos
    private List<ModeloCarro> toList(Cursor c) {
        List<ModeloCarro> modelosCarro = new ArrayList<ModeloCarro>();

        if (c.moveToFirst()) {
            do {

                modelosCarro.add(montaModeloCarro(c));


            } while (c.moveToNext());
        }
        return modelosCarro;
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

    private ModeloCarro montaModeloCarro(Cursor c) {

        ModeloCarro modelocarro = new ModeloCarro();

        modelocarro.setCodigo(c.getLong(c.getColumnIndex("codigo")));
        modelocarro.setNome(c.getString(c.getColumnIndex("nome")));

        switch (c.getString(c.getColumnIndex("categoria"))) {

            case "SEDAN":
                modelocarro.setCategoria(CategoriaEnum.SEDAN);
                break;
            case "HATCH":
                modelocarro.setCategoria(CategoriaEnum.HATCH);
                break;
            case "SUV":
                modelocarro.setCategoria(CategoriaEnum.SUV);
                break;
            case "PICAPE":
                modelocarro.setCategoria(CategoriaEnum.PICAPE);
                break;
            case "VAN":
                modelocarro.setCategoria(CategoriaEnum.VAN);
                break;

        }


        try {

            modelocarro.setFabricante(fDB.findById(c.getLong(c.getColumnIndex("codFabricante"))));
        } catch (Exception e) {

        }
        return modelocarro;
    }


}
