package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
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


public class PopupActivity extends Fragment {

    private static final String TAG = "PopupActivity";

    //POPUP XML
    private EditText textSearchProduitYes;
    private EditText textSearchProduitNo;
    private LinearLayout linearLayoutYes;
    private LinearLayout linearLayoutNo;
    private Button buttonRetour;
    private TextView textViewSelect;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.popup, container, false);
        String idRecette = getArguments().getString("idRecette");
        infoRecette(Integer.parseInt(idRecette));
        return view;
    }

    public void reloadPopupInfo(Recettes recette) {
        linearLayoutYes.removeAllViews();
        linearLayoutNo.removeAllViews();
        infoRecette(recette.getIdRecette());
    }

    public void infoRecette(int idRecette) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao( ContenirRecettes.class );
            Dao<Recettes, Integer> daoRecettes = linker.getDao( Recettes.class );
            Recettes recette = daoRecettes.queryForId(idRecette);
            List<ContenirRecettes> listContenirRecettes = daoContenirRecettes.queryForAll();

            textSearchProduitYes = view.findViewById(R.id.textSearchProduitYes);
            textSearchProduitNo = view.findViewById(R.id.textSearchProduitNo);
            linearLayoutYes = view.findViewById(R.id.linearLayoutYes);
            linearLayoutNo = view.findViewById(R.id.linearLayoutNo);
            buttonRetour = view.findViewById(R.id.buttonRetour);

            textViewSelect = view.findViewById(R.id.textViewSelect);
            textViewSelect.setText("Recette sélectionnée: "+recette.getLibelleRecette());

            buttonRetour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new RecettesActivity();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit();
                }
            });

            if(listContenirRecettes.size() == 0) {
                getProduitInfoPopup(recette, linearLayoutNo, false, listContenirRecettes);
            } else {
                getProduitInRecettePopup(recette, linearLayoutYes, listContenirRecettes);
                getProduitInfoPopup(recette, linearLayoutNo, true, listContenirRecettes);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void editQuantiteInRecette(ContenirRecettes contenirRecette, int quantite) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao( ContenirRecettes.class );

            contenirRecette.setQuantite(quantite);
            daoContenirRecettes.update(contenirRecette);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void getProduitInRecettePopup(Recettes recette, LinearLayout linearLayoutYes, List<ContenirRecettes> listContenirRecettes) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Produits, Integer> daoProduits = linker.getDao( Produits.class );
            List<Produits> listProduit = daoProduits.queryForAll();

            for(ContenirRecettes contenirRecette : listContenirRecettes) {
                if(contenirRecette.getRecette().getIdRecette() == recette.getIdRecette()) {
                    boolean verification = false;
                    for(Produits produit : listProduit) {
                        if(contenirRecette.getProduit().getIdProduit() == produit.getIdProduit()) {
                            verification = true;
                        }
                    }
                    if(verification) {
                        LinearLayout tableRow = new LinearLayout(getActivity().getApplicationContext());
                        TextView libelle = new TextView(getActivity().getApplicationContext());
                        EditText quantite = new EditText(getActivity().getApplicationContext());
                        CheckBox checkBox = new CheckBox(getActivity().getApplicationContext());

                        libelle.setText(contenirRecette.getProduit().getLibelle());
                        quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        quantite.setText(""+contenirRecette.getQuantite());
                        quantite.setHint("Quantité");
                        checkBox.setChecked(true);

                        libelle.setTextColor(Color.parseColor("#FFFFFF"));
                        quantite.setTextColor(Color.parseColor("#FFFFFF"));
                        quantite.setHintTextColor(Color.parseColor("#FFFFFF"));
                        checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean checked = checkBox.isChecked();
                                if (!checked){
                                    removeProduitRecette(contenirRecette.getProduit(), recette);
                                }
                            }
                        });

                        quantite.addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable s) {
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                boolean verification = true;
                                if(s.toString().equals("0")) {
                                    verification = false;
                                }
                                if(s.toString().length() == 0) {
                                    verification = false;
                                }
                                if(verification) {
                                    editQuantiteInRecette(contenirRecette, Integer.parseInt(s.toString()));
                                }
                            }
                        });

                        textSearchProduitYes.addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable s) {}
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                linearLayoutYes.removeAllViews();
                                if(s != "" || s != " ") {
                                    if((contenirRecette.getProduit().getLibelle().toLowerCase()).contains(s.toString().toLowerCase())) {
                                        libelle.setText(contenirRecette.getProduit().getLibelle());
                                        quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                        quantite.setText(""+contenirRecette.getQuantite());
                                        checkBox.setChecked(true);

                                        checkBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                boolean checked = checkBox.isChecked();
                                                if (!checked){
                                                    removeProduitRecette(contenirRecette.getProduit(), recette);
                                                }
                                            }
                                        });

                                        quantite.addTextChangedListener(new TextWatcher() {
                                            public void afterTextChanged(Editable s) {
                                            }

                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            }

                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                boolean verification = true;
                                                if(s.toString().equals("0")) {
                                                    verification = false;
                                                }
                                                if(s.toString().length() == 0) {
                                                    verification = false;
                                                }
                                                if(verification) {
                                                    editQuantiteInRecette(contenirRecette, Integer.parseInt(s.toString()));
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    libelle.setText(contenirRecette.getProduit().getLibelle());
                                    quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                                    quantite.setText(""+contenirRecette.getQuantite());
                                    checkBox.setChecked(true);

                                    checkBox.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean checked = checkBox.isChecked();
                                            if (!checked){
                                                removeProduitRecette(contenirRecette.getProduit(), recette);
                                            }
                                        }
                                    });

                                    quantite.addTextChangedListener(new TextWatcher() {
                                        public void afterTextChanged(Editable s) {
                                        }

                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            boolean verification = true;
                                            if(s.toString().equals("0")) {
                                                verification = false;
                                            }
                                            if(s.toString().length() == 0) {
                                                verification = false;
                                            }
                                            if(verification) {
                                                editQuantiteInRecette(contenirRecette, Integer.parseInt(s.toString()));
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        tableRow.addView(checkBox);
                        tableRow.addView(libelle);
                        tableRow.addView(quantite);
                        linearLayoutYes.addView(tableRow);
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void getProduitInfoPopup(Recettes recette, LinearLayout linearLayoutNo, boolean contains, List<ContenirRecettes> listContenirRecettes) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<Produits, Integer> daoProduits = linker.getDao( Produits.class );
            List<Produits> listProduit = daoProduits.queryForAll();

            for(Produits produit : listProduit) {
                if(contains) {
                    boolean verification = true;
                    for(ContenirRecettes contenirRecette : listContenirRecettes) {
                        if(contenirRecette.getRecette().getIdRecette() == recette.getIdRecette()) {
                            if(contenirRecette.getProduit().getIdProduit() == produit.getIdProduit()) {
                                verification = false;
                            }
                        }
                    }
                    if(verification) {
                        LinearLayout tableRow = new LinearLayout(getActivity().getApplicationContext());
                        TextView libelle = new TextView(getActivity().getApplicationContext());
                        EditText quantite = new EditText(getActivity().getApplicationContext());
                        CheckBox checkBox = new CheckBox(getActivity().getApplicationContext());

                        libelle.setText(produit.getLibelle());

                        libelle.setTextColor(Color.parseColor("#FFFFFF"));
                        quantite.setTextColor(Color.parseColor("#FFFFFF"));
                        quantite.setHintTextColor(Color.parseColor("#FFFFFF"));
                        quantite.setHint("Quantité");
                        quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean checked = checkBox.isChecked();
                                if (checked){
                                    int qtn;
                                    if(quantite.getText().toString().equals("0") || quantite.getText().toString().equals("")) {
                                        qtn = 1;
                                    } else {
                                        qtn = Integer.parseInt(quantite.getText().toString());
                                    }
                                    addProduitRecette(produit, recette, qtn);
                                }
                            }
                        });

                        textSearchProduitNo.addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable s) {}
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                linearLayoutNo.removeAllViews();
                                if(s != "" || s != " ") {
                                    if((produit.getLibelle().toLowerCase()).contains(s.toString().toLowerCase())) {
                                        libelle.setText(produit.getLibelle());
                                        quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

                                        checkBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                boolean checked = checkBox.isChecked();
                                                if (checked){
                                                    int qtn;
                                                    if(quantite.getText().toString().equals("0") || quantite.getText().toString().equals("")) {
                                                        qtn = 1;
                                                    } else {
                                                        qtn = Integer.parseInt(quantite.getText().toString());
                                                    }
                                                    addProduitRecette(produit, recette, qtn);
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    libelle.setText(produit.getLibelle());
                                    quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

                                    checkBox.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean checked = checkBox.isChecked();
                                            if (checked){
                                                int qtn;
                                                if(quantite.getText().toString().equals("0") || quantite.getText().toString().equals("")) {
                                                    qtn = 1;
                                                } else {
                                                    qtn = Integer.parseInt(quantite.getText().toString());
                                                }
                                                addProduitRecette(produit, recette, qtn);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        tableRow.addView(checkBox);
                        tableRow.addView(libelle);
                        tableRow.addView(quantite);
                        linearLayoutNo.addView(tableRow);
                    }
                } else {
                    LinearLayout tableRow = new LinearLayout(getActivity().getApplicationContext());
                    TextView libelle = new TextView(getActivity().getApplicationContext());
                    EditText quantite = new EditText(getActivity().getApplicationContext());
                    CheckBox checkBox = new CheckBox(getActivity().getApplicationContext());

                    libelle.setTextColor(Color.parseColor("#FFFFFF"));
                    quantite.setTextColor(Color.parseColor("#FFFFFF"));
                    quantite.setHintTextColor(Color.parseColor("#FFFFFF"));
                    quantite.setHint("Quantité");
                    quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

                    libelle.setText(produit.getLibelle());

                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = checkBox.isChecked();
                            if (checked){
                                int qtn;
                                if(quantite.getText().toString().equals("0") || quantite.getText().toString().equals("")) {
                                    qtn = 1;
                                } else {
                                    qtn = Integer.parseInt(quantite.getText().toString());
                                }
                                addProduitRecette(produit, recette, qtn);
                            }
                        }
                    });

                    textSearchProduitNo.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable s) {}
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            linearLayoutNo.removeAllViews();
                            if(s != "" || s != " ") {
                                if((produit.getLibelle().toLowerCase()).contains(s.toString().toLowerCase())) {
                                    libelle.setText(produit.getLibelle());
                                    quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

                                    checkBox.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean checked = checkBox.isChecked();
                                            if (checked){
                                                int qtn;
                                                if(quantite.getText().toString().equals("0") || quantite.getText().toString().equals("")) {
                                                    qtn = 1;
                                                } else {
                                                    qtn = Integer.parseInt(quantite.getText().toString());
                                                }
                                                addProduitRecette(produit, recette, qtn);
                                            }
                                        }
                                    });
                                }
                            } else {
                                libelle.setText(produit.getLibelle());
                                quantite.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

                                checkBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        boolean checked = checkBox.isChecked();
                                        if (checked){
                                            int qtn;
                                            if(quantite.getText().toString().equals("0") || quantite.getText().toString().equals("")) {
                                                qtn = 1;
                                            } else {
                                                qtn = Integer.parseInt(quantite.getText().toString());
                                            }
                                            addProduitRecette(produit, recette, qtn);
                                        }
                                    }
                                });
                            }
                        }
                    });
                    tableRow.addView(checkBox);
                    tableRow.addView(libelle);
                    tableRow.addView(quantite);
                    linearLayoutNo.addView(tableRow);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void addProduitRecette(Produits produit, Recettes recette, int quantite){
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao(ContenirRecettes.class);

            ContenirRecettes contenirRecette = new ContenirRecettes();
            contenirRecette.setQuantite(quantite);
            contenirRecette.setRecette(recette);
            contenirRecette.setProduit(produit);
            daoContenirRecettes.create(contenirRecette);

            reloadPopupInfo(recette);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }

    public void removeProduitRecette(Produits produit, Recettes recette) {
        DataBaseLinker linker = new DataBaseLinker(getActivity().getApplicationContext());
        try {
            Dao<ContenirRecettes, Integer> daoContenirRecettes = linker.getDao(ContenirRecettes.class);
            List<ContenirRecettes> listContenirRecettes = daoContenirRecettes.queryForAll();

            for(ContenirRecettes contenirRecettes : listContenirRecettes) {
                if(contenirRecettes.getRecette().getIdRecette() == recette.getIdRecette()) {
                    if(contenirRecettes.getProduit().getIdProduit() == produit.getIdProduit()) {
                        daoContenirRecettes.delete(contenirRecettes);
                        reloadPopupInfo(recette);
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        linker.close();
    }
}