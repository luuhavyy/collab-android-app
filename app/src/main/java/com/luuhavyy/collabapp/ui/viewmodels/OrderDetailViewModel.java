package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.repository.OrderRepository;
import com.luuhavyy.collabapp.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailViewModel extends ViewModel {
    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productListLiveData = new MutableLiveData<>();
    private final ProductRepository productRepository = new ProductRepository();
    public LiveData<List<Product>> getProductListLiveData() { return productListLiveData; }
    private final OrderRepository orderRepository = new OrderRepository();

    public LiveData<Order> getOrderLiveData() {
        return orderLiveData;
    }

    public void setOrder(Order order) {
        orderLiveData.setValue(order);
        fetchProductsForOrder(order);
    }

    private void fetchProductsForOrder(Order order) {
        List<Product> products = new ArrayList<>();
        List<Order.ProductItem> items = order.getProducts();
        if (items == null || items.isEmpty()) {
            productListLiveData.postValue(products);
            return;
        }
        final int total = items.size();
        final int[] count = {0};

        for (Order.ProductItem item : items) {
            productRepository.fetchProductById(item.getProductid(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product p = snapshot.getValue(Product.class);
                    if (p != null) products.add(p);
                    count[0]++;
                    if (count[0] == total) {
                        productListLiveData.postValue(products);
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {
                    count[0]++;
                    if (count[0] == total) {
                        productListLiveData.postValue(products);
                    }
                }
            });
        }
    }

    public void fetchOrderById(String orderId) {
        orderRepository.getOrderById(orderId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                orderLiveData.postValue(order);
                if (order != null) {
                    fetchProductsForOrder(order);
                } else {
                    productListLiveData.postValue(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                orderLiveData.postValue(null);
                productListLiveData.postValue(null);
            }
        });
    }
}
