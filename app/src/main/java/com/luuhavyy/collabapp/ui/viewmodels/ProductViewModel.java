package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.ProductFilterSort;
import com.luuhavyy.collabapp.data.repository.ProductRepository;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public void fetchProductsWithFilter(ProductFilterSort filterSort, final LoadingHandlerUtil.TaskCallback callback) {
        if (productListener != null) {
            callback.onComplete();
            return;
        }

        repository.fetchProductsWithFilter(filterSort, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> filtered = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    if (product == null) continue;

                    // Filter category
                    boolean matchFrame = filterSort.frameGlasses && "cat001".equals(product.getCategoryid());
                    boolean matchSun = filterSort.sunglasses && "cat002".equals(product.getCategoryid());
                    boolean noTypeChecked = !filterSort.frameGlasses && !filterSort.sunglasses;

                    if (matchFrame || matchSun || noTypeChecked) {
                        filtered.add(product);
                    }
                }

                // Reset sort
                if (filterSort.sortBy != null) {
                    switch (filterSort.sortBy) {
                        case "price_asc":
                            Collections.sort(filtered, Comparator.comparing(Product::getPrice));
                            break;
                        case "price_desc":
                            Collections.sort(filtered, (a, b) -> Float.compare(b.getPrice(), a.getPrice()));
                            break;
                        case "name_asc":
                            Collections.sort(filtered, Comparator.comparing(Product::getName));
                            break;
                        case "name_desc":
                            Collections.sort(filtered, (a, b) -> b.getName().compareToIgnoreCase(a.getName()));
                            break;
                    }
                }

                productsLiveData.setValue(filtered);
                if (callback != null) callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) callback.onComplete();
            }
        });
    }

    public void searchProductByName(String keyword, LoadingHandlerUtil.TaskCallback callback) {
        if (productListener != null) {
            callback.onComplete();
            return;
        }

        repository.searchProductByName(keyword, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    if (product != null && product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                        result.add(product);
                    }
                }
                productsLiveData.setValue(result);
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onComplete();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (productListener != null) {
            repository.removeProductListener(productListener);
            productListener = null;
        }
    }
}