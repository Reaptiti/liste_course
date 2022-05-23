package com.example.myapplication.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Recettes")
public class Recettes {

    @DatabaseField( columnName = "idRecette", generatedId = true )
    private int idRecette;

    @DatabaseField( columnName="libelleRecette")
    private String libelleRecette;

    public Recettes() {
    }

    public Recettes(String libelleRecette) {
        this.libelleRecette = libelleRecette;
    }

    public int getIdRecette() {
        return idRecette;
    }

    public void setIdRecette(int idRecette) {
        this.idRecette = idRecette;
    }

    public String getLibelleRecette() {
        return libelleRecette;
    }

    public void setLibelleRecette(String libelleRecette) {
        this.libelleRecette = libelleRecette;
    }
}
