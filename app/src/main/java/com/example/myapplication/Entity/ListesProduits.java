package com.example.myapplication.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ListesProduits")
public class ListesProduits {

    @DatabaseField( columnName = "idListeProduits", generatedId = true )
    private int idListeProduits;

    @DatabaseField( canBeNull = false, foreign = true, foreignColumnName = "idListe", foreignAutoCreate = true )
    private Listes liste;

    @DatabaseField( canBeNull = false, foreign = true, foreignColumnName = "idProduit", foreignAutoCreate = true )
    private Produits produit;

    public ListesProduits() {
    }

    public ListesProduits(Listes liste, Produits produit) {
        this.liste = liste;
        this.produit = produit;
    }

    public int getIdListeProduits() {
        return idListeProduits;
    }

    public void setIdListeProduits(int idListeProduits) {
        this.idListeProduits = idListeProduits;
    }

    public Listes getListe() {
        return liste;
    }

    public void setListe(Listes liste) {
        this.liste = liste;
    }

    public Produits getProduit() {
        return produit;
    }

    public void setProduit(Produits produit) {
        this.produit = produit;
    }
}
