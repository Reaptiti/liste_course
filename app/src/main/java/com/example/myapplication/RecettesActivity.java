package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapplication.Entity.ContenirRecettes;
import com.example.myapplication.Entity.Listes;
import com.example.myapplication.Entity.ListesRecettes;
import com.example.myapplication.Entity.Produits;
import com.example.myapplication.Entity.Recettes;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.query.In;

import java.sql.SQLException;
import java.util.List;


public class RecettesActivity extends Fragment {

    private static final String TAG = "RecettesActivity";
    private LinearLayout tableLayoutRecettes;
    private EditText searchBar;
    private EditText editTextSearch;
    private Button buttonCreateRecette;
    private LinearLayout tableLayoutProduits;
    private TextView textViewSelectRecette;

    //POPUP XML
    private AlertDialog.Builder infoRecettePopup;
    private AlertDialog dialog;
    private EditText textSearchProduitYes;
    private EditText textSearchProduitNo;
    private LinearLayout linearLayoutYes;
    private LinearLayout linearLayoutNo;
    private Button buttonRetour;
    private TextView textViewSelect;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recettes_main, container, false);
        tableLayoutRecettes = view.findViewById(R.id.tableLayoutRecettes);

        getRecettes();
        createRecette();

        return view;
    }

    public void createRecette() {
        buttonCreateRecette = view.findViewById(R.id.buttonCreateRecette);
        buttonCreateRecette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecette();
            }
        });
    }

    public void addRecette() {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {

            Dao<Produits, Integer> daoProduits = linker.getDao( Produits.class );
            Dao<Recettes, Integer> daoRecettes = linker.getDao( Recettes.class );
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao( ContenirRecettes.class );

            AlertDialog.Builder editPopup = new AlertDialog.Builder(getContext());
            editPopup.setTitle("Voulez-vous créer une recette ?");

            //TableLayout CREATE

            TableLayout tableLayout = new TableLayout(getActivity().getApplicationContext());

            // CREATION LIBELLE RECETTE

            TableRow tableRowLibelle = new TableRow(getActivity().getApplicationContext());

            TextView infoLibelle = new TextView(getActivity().getApplicationContext());
            infoLibelle.setText("Nom: ");
            infoLibelle.setTextSize(18);
            infoLibelle.setTextColor(Color.parseColor("#FFFFFF"));
            tableRowLibelle.addView(infoLibelle);

            EditText createLibelle = new EditText(getActivity().getApplicationContext());
            createLibelle.setTextSize(18);
            createLibelle.setHint("Nom de votre recette");
            createLibelle.setTextColor(Color.parseColor("#FFFFFF"));
            createLibelle.setHintTextColor(Color.parseColor("#FFFFFF"));
            tableRowLibelle.addView(createLibelle);

            tableLayout.addView(tableRowLibelle);

            // ADD VIEW IN POPUP EDIT

            editPopup.setView(tableLayout);

            //deletePopup.setMessage("Cliquez sur oui ou non");
            editPopup.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { ;
                    try {
                        Recettes recette = new Recettes();
                        recette.setLibelleRecette(createLibelle.getText().toString());
                        daoRecettes.create(recette);

                        reloadPageAll();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
            editPopup.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.i(TAG, "Recette non crée");
                }
            });
            editPopup.show();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void getRecettes() {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Recettes, Integer> daoRecettes = linker.getDao( Recettes.class );
            Dao<Produits, Integer> daoProduits = linker.getDao( Produits.class );
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao( ContenirRecettes.class );

            List<Recettes> listRecette = daoRecettes.queryForAll();
            List<Produits> listProduit = daoProduits.queryForAll();
            List<ContenirRecettes> listContenirRecettes = daoContenirRecettes.queryForAll();

            if(listRecette.size() != 0) {
                for (Recettes recette: listRecette) {
                    LinearLayout tableRecetteName = new LinearLayout(getActivity().getApplicationContext());

                    // LIBELLE DE LA RECETTE

                    TextView libelleRecette = new TextView(getActivity().getApplicationContext());
                    libelleRecette.setText(recette.getLibelleRecette());
                    libelleRecette.setTextColor(Color.parseColor("#FFFFFF"));
                    tableRecetteName.addView(libelleRecette);

                    // BUTTON REMOVE DE LA RECETTE

                    Button buttonEdit = new Button(getActivity().getApplicationContext());
                    buttonEdit.setText("MODIFIER");
                    buttonEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editRecettePopup(recette);
                        }
                    });

                    tableRecetteName.addView(buttonEdit);

                    // BUTTON REMOVE DE LA RECETTE

                    Button buttonRemove = new Button(getActivity().getApplicationContext());
                    buttonRemove.setText("SUPPRIMER");
                    buttonRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeRecettePopup(recette);
                        }
                    });

                    tableRecetteName.addView(buttonRemove);

                    // BUTTON INFO DE LA RECETTE

                    Button buttonInfo = new Button(getActivity().getApplicationContext());
                    buttonInfo.setText("DETAILS");
                    buttonInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            infoRecette(recette);
                        }
                    });

                    tableRecetteName.addView(buttonInfo);

                    tableLayoutRecettes.addView(tableRecetteName);
                }

            } else {
                LinearLayout nothingRecette = new LinearLayout(getActivity().getApplicationContext());

                TextView infoText = new TextView(getActivity().getApplicationContext());
                infoText.setText("Aucune recette n'est disponibe, vous pouvez en créer une !");
                infoText.setTextColor(Color.parseColor("#FFFFFF"));
                nothingRecette.addView(infoText);

                tableLayoutRecettes.addView(nothingRecette);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void removeRecettePopup(Recettes recette) {
        AlertDialog.Builder infoRecettePopup = new AlertDialog.Builder(getContext());
        infoRecettePopup.setTitle("Etes vous sur de vouloir supprimer "+recette.getLibelleRecette()+" ?");

        infoRecettePopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeRecette(recette);
            }
        });

        infoRecettePopup.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Non");
            }
        });

        infoRecettePopup.show();
    }

    public void removeRecette(Recettes recette) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Recettes, Integer> daoRecettes = linker.getDao( Recettes.class );
            Dao<ListesRecettes, Integer> daoListesRecettes = linker.getDao( ListesRecettes.class );
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao( ContenirRecettes.class );
            Dao<Listes, Integer> daoListes = linker.getDao( Listes.class );

            List<ContenirRecettes> listContenirRecettes = daoContenirRecettes.queryForAll();
            List<ListesRecettes> listListesRecettes = daoListesRecettes.queryForAll();
            List<Listes> listListes = daoListes.queryForAll();

            for(ContenirRecettes contenirRecette : listContenirRecettes) {
                if (contenirRecette.getRecette().getIdRecette() == recette.getIdRecette()) {
                    daoContenirRecettes.delete(contenirRecette);
                }
            }

            for(ListesRecettes listesRecettes : listListesRecettes) {
                if(listesRecettes.getRecette().getIdRecette() == recette.getIdRecette()) {
                    for(Listes listes : listListes) {
                        if(listes.getIdListe() == listesRecettes.getListe().getIdListe()) {
                            daoListesRecettes.delete(listesRecettes);
                            daoListes.delete(listes);
                        }
                    }
                }
            }

            daoRecettes.delete(recette);

            reloadPageAll();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void editRecettePopup(Recettes recette) {
        AlertDialog.Builder updateRecettePopup = new AlertDialog.Builder(getContext());
        updateRecettePopup.setTitle("Voulez vous mettre à jour "+recette.getLibelleRecette()+" ?");

        LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());

        TextView libelle = new TextView(getActivity().getApplicationContext());
        libelle.setText("Nom de la recette: ");
        libelle.setTextColor(Color.parseColor("#FFFFFF"));
        linearLayout.addView(libelle);

        EditText libelleText = new EditText(getActivity().getApplicationContext());
        libelleText.setText(recette.getLibelleRecette());
        libelleText.setTextColor(Color.parseColor("#FFFFFF"));
        libelleText.setHintTextColor(Color.parseColor("#FFFFFF"));
        linearLayout.addView(libelleText);

        updateRecettePopup.setView(linearLayout);

        updateRecettePopup.setPositiveButton("Mettre à jour", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editRecette(recette, libelleText.getText().toString());
            }
        });

        updateRecettePopup.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Retour");
            }
        });

        updateRecettePopup.show();
    }

    public void editRecette(Recettes recette, String libelle) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Recettes, Integer> daoRecettes = linker.getDao( Recettes.class );

            Recettes recetteChange = daoRecettes.queryForId(recette.getIdRecette());
            recetteChange.setLibelleRecette(libelle);
            daoRecettes.update(recetteChange);

            reloadPageAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void reloadPageAll() {
        tableLayoutRecettes.removeAllViews();
        getRecettes();
        createRecette();
    }

    public void infoRecette(Recettes recette) {
        Bundle bundle = new Bundle();
        bundle.putString("idRecette", ""+recette.getIdRecette());

        Fragment fragment = new PopupActivity();
        fragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}