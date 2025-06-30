package com.luuhavyy.collabapp.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.viewmodels.ProductViewModel;
import com.luuhavyy.collabapp.ui.viewmodels.UserViewModel;

public class AddToCartDialogFragment extends DialogFragment {
    private int quantity = 1;
    private float price = 0;
    private ProductViewModel productViewModel;
    private UserViewModel userViewModel = new UserViewModel();

    public static AddToCartDialogFragment newInstance(String productId) {
        AddToCartDialogFragment fragment = new AddToCartDialogFragment();
        Bundle args = new Bundle();
        args.putString("product_id", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_to_cart, container, false);

        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        ImageView btnClose = view.findViewById(R.id.btn_close);
        ImageButton btnMinus = view.findViewById(R.id.btn_minus);
        ImageButton btnPlus = view.findViewById(R.id.btn_plus);
        Button btnAddToCart = view.findViewById(R.id.btn_add_to_cart);

        // Lấy productId
        String productId = getArguments() != null ? getArguments().getString("product_id") : null;

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        productViewModel.fetchProductDetail(productId, () -> {});

        productViewModel.getProductLiveData().observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                price = product.getPrice();
                updatePrice(tvPrice);
            }
        });

        // Quantity logic
        tvQuantity.setText(String.valueOf(quantity));
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updatePrice(tvPrice);
            }
        });
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updatePrice(tvPrice);
        });

        btnClose.setOnClickListener(v -> dismiss());

        btnAddToCart.setOnClickListener(v -> {
            String authId = FirebaseAuth.getInstance().getUid();
            if (authId == null) {
                Toast.makeText(getContext(), "Please login to use cart!", Toast.LENGTH_SHORT).show();
                return;
            }
            productViewModel.addToCartByAuthId(authId, productId, price, quantity,
                    () -> {
                        // Push activity "Added to Cart" sau khi add cart thành công
                        userViewModel.logUserActivityByAuthId(authId, "Added to Cart", productId);

                        Toast.makeText(getContext(), "Added " + quantity + " to cart!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    },
                    () -> Toast.makeText(getContext(), "Failed to add to cart!", Toast.LENGTH_SHORT).show()
            );
        });

        return view;
    }

    private void updatePrice(TextView tvPrice) {
        float total = price * quantity;
        tvPrice.setText("VNĐ " + (int)total);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
