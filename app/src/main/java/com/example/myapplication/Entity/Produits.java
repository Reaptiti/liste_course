package com.example.myapplication.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Produits")
public class Produits {

    @DatabaseField( columnName = "idProduit", generatedId = true )
    private int idProduit;

    @DatabaseField( columnName="libelle")
    private String libelle;

    public Produits() {
    }

    public Produits(String libelle) {
        this.libelle = libelle;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
