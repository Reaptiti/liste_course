package com.example.myapplication.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ListesRecettes")
public class ListesRecettes {

    @DatabaseField( columnName = "idListesRecettes", generatedId = true )
    private int idListesRecettes;

    @DatabaseField( canBeNull = false, foreign = true, foreignColumnName = "idListe", foreignAutoCreate = true )
    private Listes liste;

    @DatabaseField( canBeNull = false, foreign = true, foreignColumnName = "idRecette", foreignAutoCreate = true )
    private Recettes recette;

    public ListesRecettes() {
    }

    public ListesRecettes(Listes liste, Recettes recette) {
        this.liste = liste;
        this.recette = recette;
    }

    public int getIdListesRecettes() {
        return idListesRecettes;
    }

    public void setIdListesRecettes(int idListesRecettes) {
        this.idListesRecettes = idListesRecettes;
    }

    public Listes getListe() {
        return liste;
    }

    public void setListe(Listes liste) {
        this.liste = liste;
    }

    public Recettes getRecette() {
        return recette;
    }

    public void setRecette(Recettes recette) {
        this.recette = recette;
    }
}
