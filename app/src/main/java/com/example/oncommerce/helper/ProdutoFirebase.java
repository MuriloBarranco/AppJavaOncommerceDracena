package com.example.oncommerce.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProdutoFirebase {

    public static String getIdProduto(){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();

    }

    public static FirebaseUser getProdutoAtual(){
        FirebaseAuth produto = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return produto.getCurrentUser();
    }


    public static boolean atualizarTipoProduto(String tipo){

        try {

            FirebaseUser user = getProdutoAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(profile);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}

