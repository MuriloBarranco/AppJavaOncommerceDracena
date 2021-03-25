package com.example.oncommerce.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncommerce.R;
import com.example.oncommerce.adapter.AdapterProduto;
import com.example.oncommerce.helper.ConfiguracaoFirebase;
import com.example.oncommerce.helper.UsuarioFirebase;
import com.example.oncommerce.model.Empresa;
import com.example.oncommerce.model.ItemPedido;
import com.example.oncommerce.model.Pedido;
import com.example.oncommerce.model.Produto;
import com.example.oncommerce.model.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ActivityChat extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresa;
    private TextView textNomeEmpresa;
    private TextView textCelular;

    private Empresa empresaSelecionada;
    private AlertDialog dialog;
    private TextView textCarrinhoQtd, textCarrinhoTotal;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private int metodoPagamento;
    private Button buttonChat;
    private List<Empresa> empresas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Recuperar empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

            textNomeEmpresa.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdUsuario();
            textCelular.setText("Celular:  " + empresaSelecionada.getCelular());

            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresa);

            Button button = findViewById(R.id.buttonAcesso2);
            final EditText editText_cel = findViewById(R.id.editTextCel);
            final EditText editText_msg = findViewById(R.id.editTextMsg);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cel = editText_cel.getText().toString();
                    String msg = editText_msg.getText().toString();

                    boolean installed = appInstalledOrNot("com.whatsapp");

                    if (installed) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+55" + cel + "&text=" + msg));
                        startActivity(intent);
                    }else {
                        Toast.makeText(ActivityChat.this, "WhatsApp não instalado no seu aparelho", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void inicializarComponentes() {
        imageEmpresa = findViewById(R.id.imageEmpresa);
        textNomeEmpresa = findViewById(R.id.textNomeEmpresa);
        textCelular = findViewById(R.id.textviewcelular);

    }

    private boolean appInstalledOrNot(String url) {
        PackageManager packageManager = getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;

        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}



