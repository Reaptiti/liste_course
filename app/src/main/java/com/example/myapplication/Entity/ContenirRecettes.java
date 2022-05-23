package com.example.myapplication.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ContenirRecettes")
public class ContenirRecettes {

    @DatabaseField( columnName = "idContenirRecettes", generatedId = true )
    private int idContenirRecettes;

    @DatabaseField( columnName="quantite")
    private int quantite;

    @DatabaseField( canBeNull = false, foreign = true, foreignColumnName = "idRecette", foreignAutoCreate = true )
    private Recettes recette;

    @DatabaseField( canBeNull = false, foreign = true, foreignColumnName = "idProduit", foreignAutoCreate = true )
    private Produits produit;

    public ContenirRecettes() {
    }

    public ContenirRecettes(int quantite, Recettes recette, Produits produit) {
        this.quantite = quantite;
        this.recette = recette;
        this.produit = produit;
    }

    public int getIdContenirRecettes() {
        return idContenirRecettes;
    }

    public void setIdContenirRecettes(int idContenirRecettes) {
        this.idContenirRecettes = idContenirRecettes;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Recettes getRecette() {
        return recette;
    }

    public void setRecette(Recettes recette) {
        this.recette = recette;
    }

    public Produits getProduit() {
        return produit;
    }

    public void setProduit(Produits produit) {
        this.produit = produit;
    }
}
