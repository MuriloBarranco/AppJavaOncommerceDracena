package com.example.oncommerce.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncommerce.R;
import com.example.oncommerce.adapter.AdapterProduto;
import com.example.oncommerce.helper.ConfiguracaoFirebase;
import com.example.oncommerce.helper.UsuarioFirebase;
import com.example.oncommerce.listener.RecyclerItemClickListener;
import com.example.oncommerce.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);


        //Configuraões iniciais
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();



        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("OnCommerce - Empresa");
        setSupportActionBar(toolbar);

        //Configurações recyclerview
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter( adapterProduto );

        //Recupera produtos para empresa
        recuperarProdutos();

        //Adiciona evento de clique no recyclerview
        recyclerProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Produto produtoSelecionado = produtos.get(position);
                                produtoSelecionado.remover();
                                Toast.makeText(EmpresaActivity.this,
                                        "Produto foi excluído com sucesso!",
                                        Toast.LENGTH_SHORT)
                                        .show();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child( idUsuarioLogado );
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add( ds.getValue(Produto.class) );
                }

                adapterProduto.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void inicializarComponentes(){
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto :
                abrirNovoProduto();
                break;
            case R.id.menuPedidos :
                abrirPedidos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void abrirPedidos(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
    }
    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

}