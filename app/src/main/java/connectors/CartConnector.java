package connectors;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import models.Cart;
import models.CartItem;

public class CartConnector {
    private DatabaseReference cartRef;

    public CartConnector() {
        cartRef = FirebaseDatabase.getInstance().getReference("cart");
    }

    public void getCartForUser(String userId, ValueEventListener listener) {
        cartRef.orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(listener);
    }

    public void updateCartItem(String cartId, List<CartItem> items) {
        cartRef.child(cartId).child("products").setValue(items);
    }

    public void updateCartTotal(String cartId, double totalAmount) {
        cartRef.child(cartId).child("totalamount").setValue(totalAmount);
    }

    public void applyPromotion(String cartId, String promotionId) {
        cartRef.child(cartId).child("promotionid").setValue(promotionId);
    }
}