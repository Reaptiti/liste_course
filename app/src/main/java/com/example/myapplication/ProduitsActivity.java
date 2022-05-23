package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapplication.Entity.ContenirRecettes;
import com.example.myapplication.Entity.ListesProduits;
import com.example.myapplication.Entity.Produits;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class ProduitsActivity extends Fragment {

    private static final String TAG = "ProduitsActivity";
    private TableLayout produitsTableLayout;
    private EditText editTextSearch;
    private Button buttonCreateProduit;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.produits_main, container, false);
        produitsTableLayout = view.findViewById(R.id.produitsTableLayout);

        buttonCreate();
        getEditSearch();

        return view;
    }

    public void buttonCreate() {
        buttonCreateProduit = view.findViewById(R.id.buttonCreateProduit);
        buttonCreateProduit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPopupCreate();
            }
        });
    }

    public void getPopupCreate() {
        AlertDialog.Builder createPopup = new AlertDialog.Builder(getContext());
        createPopup.setTitle("Voulez-vous créer un produit ?");

        //TableLayout EDIT

        TableLayout tableLayout = new TableLayout(getActivity().getApplicationContext());

        // MODIFICATION LIBELLE PRODUIT

        TableRow tableRowLibelle = new TableRow(getActivity().getApplicationContext());

        TextView infoLibelle = new TextView(getActivity().getApplicationContext());
        infoLibelle.setText("Nom: ");
        infoLibelle.setTextSize(18);
        infoLibelle.setTextColor(Color.parseColor("#FFFFFF"));
        tableRowLibelle.addView(infoLibelle);

        EditText createLibelle = new EditText(getActivity().getApplicationContext());
        createLibelle.setTextSize(18);
        createLibelle.setHint("Nom de votre produit");
        createLibelle.setTextColor(Color.parseColor("#FFFFFF"));
        createLibelle.setHintTextColor(Color.parseColor("#FFFFFF"));
        tableRowLibelle.addView(createLibelle);

        tableLayout.addView(tableRowLibelle);

        // ADD VIEW IN POPUP EDIT

        createPopup.setView(tableLayout);

        //deletePopup.setMessage("Cliquez sur oui ou non");
        createPopup.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { ;
                createProduit(createLibelle.getText().toString());
            }
        });
        createPopup.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Produit non crée");
            }
        });
        createPopup.show();
    }

    public void createProduit(String libelle) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Produits, Integer> daoProduits = linker.getDao(Produits.class);

            Produits produit = new Produits();
            produit.setLibelle(libelle);
            daoProduits.create(produit);

            reloadProduitsLayout();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void reloadProduitsLayout() {
        produitsTableLayout.removeAllViews();
        getEditSearch();
    }

    public void getProduits(Produits produit) {
        TableRow tableRowProduit = new TableRow(getActivity().getApplicationContext());

        TextView value = new TextView(getActivity().getApplicationContext());
        value.setText(produit.getLibelle());
        value.setTextColor(Color.parseColor("#FFFFFF"));
        tableRowProduit.addView(value);

        int idProduit = produit.getIdProduit();

        Button deleteProduit = new Button(getActivity().getApplicationContext());
        deleteProduit.setText("SUPPRIMER");
        deleteProduit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduit(idProduit, tableRowProduit);
            }
        });

        tableRowProduit.addView(deleteProduit);

        Button editProduit = new Button(getActivity().getApplicationContext());
        editProduit.setText("MODIFIER");
        editProduit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProduit(idProduit, tableRowProduit);
            }
        });

        tableRowProduit.addView(editProduit);

        produitsTableLayout.addView(tableRowProduit);
    }

    public void getEditSearch() {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Produits, Integer> daoProduits = linker.getDao(Produits.class);

            List<Produits> produits = daoProduits.queryForAll();

            for (Produits produit: produits) {
                getProduits(produit);
            }

            editTextSearch = view.findViewById(R.id.editTextSearch);

            editTextSearch.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    produitsTableLayout.removeAllViews();
                    if(s != "") {
                        for (Produits produit: produits) {
                            if((produit.getLibelle().toLowerCase()).contains(s.toString().toLowerCase())) {
                                getProduits(produit);
                            }
                        }
                    } else {
                        for (Produits produit: produits) {
                            getProduits(produit);
                        }
                    }
                }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void editProduit(int idProduit, TableRow tableRowProduit) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {

            Dao<Produits, Integer> daoProduits = linker.getDao(Produits.class);

            Produits produit = daoProduits.queryForId(idProduit);

            if (produit != null) {
                AlertDialog.Builder editPopup = new AlertDialog.Builder(getContext());
                editPopup.setTitle("Voulez vous modifier "+produit.getLibelle()+" ?");

                //TableLayout EDIT

                TableLayout tableLayout = new TableLayout(getActivity().getApplicationContext());

                // MODIFICATION LIBELLE PRODUIT

                TableRow tableRowLibelle = new TableRow(getActivity().getApplicationContext());

                TextView infoLibelle = new TextView(getActivity().getApplicationContext());
                infoLibelle.setText("Libelle: ");
                infoLibelle.setTextColor(Color.parseColor("#FFFFFF"));
                tableRowLibelle.addView(infoLibelle);

                EditText changeLibelle = new EditText(getActivity().getApplicationContext());
                changeLibelle.setText(produit.getLibelle());
                changeLibelle.setTextColor(Color.parseColor("#FFFFFF"));
                changeLibelle.setHintTextColor(Color.parseColor("#FFFFFF"));
                tableRowLibelle.addView(changeLibelle);

                tableLayout.addView(tableRowLibelle);

                // ADD VIEW IN POPUP EDIT

                editPopup.setView(tableLayout);

                //deletePopup.setMessage("Cliquez sur oui ou non");
                editPopup.setPositiveButton("Mettre à jour", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { ;
                        createProduit(idProduit, changeLibelle.getText().toString());
                    }
                });
                editPopup.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Produit non mis à jour");
                    }
                });
                editPopup.show();

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void createProduit(int idProduit, String libelle) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Produits, Integer> daoProduits = linker.getDao(Produits.class);

            Produits produit = daoProduits.queryForId(idProduit);

            produit.setLibelle(libelle);
            daoProduits.update(produit);

            reloadProduitsLayout();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void removeP(List<ContenirRecettes> listContenirRecettes, List<ListesProduits> listCoursesProduits, Produits produit, TableRow tableRowProduit) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {

            Dao<Produits, Integer> daoProduits = linker.getDao(Produits.class);
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao(ContenirRecettes.class);
            Dao<ListesProduits, Integer> daoListesProduits = linker.getDao(ListesProduits.class);

            for(ContenirRecettes contenirRecette: listContenirRecettes) {
                Produits produitSelect = contenirRecette.getProduit();
                if(produitSelect.getIdProduit() == produit.getIdProduit()) {
                    daoContenirRecettes.delete(contenirRecette);
                }
            }

            for(ListesProduits liste: listCoursesProduits) {
                Produits produitSelect = liste.getProduit();
                if(produitSelect.getIdProduit() == produit.getIdProduit()) {
                    daoListesProduits.delete(liste);
                }
            }

            daoProduits.delete(produit);

            Log.i(TAG, "Vous avez suprimmé !");

            reloadProduitsLayout();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void deleteProduit(int idProduit, TableRow tableRowProduit) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Produits, Integer> daoProduits = linker.getDao(Produits.class);
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao(ContenirRecettes.class);
            Dao<ListesProduits, Integer> daoListes = linker.getDao(ListesProduits.class);

            List<ContenirRecettes> listContenirRecettes = daoContenirRecettes.queryForAll();
            List<ListesProduits> listCourses = daoListes.queryForAll();

            Produits produit = daoProduits.queryForId(idProduit);
            if (produit != null) {
                AlertDialog.Builder deletePopup = new AlertDialog.Builder(getContext());
                deletePopup.setTitle("Etes vous sur de vouloir supprimer "+produit.getLibelle()+" ?");
                //deletePopup.setMessage("Cliquez sur oui ou non");
                deletePopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeP(listContenirRecettes, listCourses, produit, tableRowProduit);
                    }
                });
                deletePopup.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Vous n'avez pas suprimmé !");
                    }
                });
                deletePopup.show();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }
}
