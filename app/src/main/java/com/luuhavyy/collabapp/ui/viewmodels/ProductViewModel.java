package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.repository.ProductRepository;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository = new ProductRepository();
    @Getter
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private ValueEventListener productListener;

    public void listenToProductsRealtime(LoadingHandlerUtil.TaskCallback callback) {
        if (productListener != null) {
            callback.onComplete();
            return;
        }

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    List<Product> productList = new ArrayList<>();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Product product = item.getValue(Product.class);
                        if (product != null) productList.add(product);
                    }
                    productsLiveData.setValue(productList);
                } else {
                    productsLiveData.setValue(new ArrayList<>());
                }
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                productsLiveData.setValue(null);
                callback.onComplete();
            }
        };

        repository.listenToProductsRealtime(productListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (productListener != null) {
            repository.removeProductListener(productListener);
        }
    }
}