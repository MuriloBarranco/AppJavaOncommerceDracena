package com.example.oncommerce.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.oncommerce.R;
import com.example.oncommerce.helper.ConfiguracaoFirebase;
import com.example.oncommerce.helper.UsuarioFirebase;
import com.example.oncommerce.model.Produto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao,
            editProdutoPreco;
    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;
    private ImageView imagePerfilProduto;
    private String urlImagemSelecionada = "";
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        /*Configurações iniciais*/

        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();


        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        imagePerfilProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        recuperarDadosProduto();

        }



        private void recuperarDadosProduto() {

            DatabaseReference produtoRef = firebaseRef
                    .child("produtos")
                    .child(idUsuarioLogado);
           produtoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        Produto produto = dataSnapshot.getValue(Produto.class);
                        editProdutoNome.setText(produto.getNome());
                        editProdutoDescricao.setText(produto.getDescricao());
                        editProdutoPreco.setText(produto.getPreco().toString());


                        urlImagemSelecionada = produto.getUrlImagem();
                        if (urlImagemSelecionada != "") {
                            Picasso.get()
                                    .load(urlImagemSelecionada)
                                    .into(imagePerfilProduto);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    public void validarDadosProduto(View view){

        //Valida se os campos foram preenchidos
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();


        if( !nome.isEmpty()){
            if( !descricao.isEmpty()){
                if( !preco.isEmpty()) {

                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.setUrlImagem(urlImagemSelecionada);


                    produto.salvar();

                    finish();

                    exibirMensagem("Produto salvo com sucesso!");



                }else{
                        exibirMensagem("Digite um preço para o produto");
                    }
                }else{
                    exibirMensagem("Digite uma Descrição para o produto");
                }
            }else{
                exibirMensagem("Digite um nome para o produto");
            }

        }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if( imagem != null){

                    imagePerfilProduto.setImageBitmap( imagem );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    String filename = UUID.randomUUID().toString();
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("produtos")
                            .child(idUsuarioLogado + filename + "jpeg");


                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();


                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }




    private void inicializarComponentes(){
        editProdutoDescricao = findViewById(R.id.editUsuarioEndereco);
        editProdutoNome = findViewById(R.id.editUsuarioNome);
        editProdutoPreco = findViewById(R.id.editUsuarioNumero);
        imagePerfilProduto = findViewById(R.id.imageview3);


    }

}


