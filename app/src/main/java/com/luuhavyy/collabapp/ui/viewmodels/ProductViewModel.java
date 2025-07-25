package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.ProductFilterSort;
import com.luuhavyy.collabapp.data.model.Review;
import com.luuhavyy.collabapp.data.model.ReviewWithUser;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.repository.ProductRepository;
import com.luuhavyy.collabapp.data.repository.ReviewRepository;
import com.luuhavyy.collabapp.data.repository.UserRepository;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository = new ProductRepository();
    private final UserRepository userRepository = new UserRepository();
    @Getter
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private ValueEventListener productListener;
    private final MutableLiveData<Product> productLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Review>> reviewListLiveData = new MutableLiveData<>();
    private final ReviewRepository reviewRepository = new ReviewRepository();

    public LiveData<Product> getProductLiveData() {
        return productLiveData;
    }

    public LiveData<List<Review>> getReviewListLiveData() {
        return reviewListLiveData;
    }

    private final MutableLiveData<List<ReviewWithUser>> reviewWithUserLiveData = new MutableLiveData<>();

    public LiveData<List<ReviewWithUser>> getReviewWithUserLiveData() {
        return reviewWithUserLiveData;
    }


    public void fetchReviewsWithUser(String productId) {
        reviewRepository.getReviewsByProductId(productId, new ReviewRepository.ReviewCallback() {
            @Override
            public void onReviewsLoaded(List<Review> reviews) {
                if (reviews == null || reviews.isEmpty()) {
                    reviewWithUserLiveData.postValue(Collections.emptyList());
                    return;
                }
                Set<String> userIds = new HashSet<>();
                for (Review r : reviews) userIds.add(r.getUserid());

                // Map userId -> User
                Map<String, User> userMap = new HashMap<>();
                // Dùng CountDownLatch để biết khi nào lấy xong hết user
                final int total = userIds.size();
                final int[] count = {0};

                for (String userId : userIds) {
                    userRepository.getUserById(userId, new UserRepository.UserCallback() {
                        @Override
                        public void onUserLoaded(User user) {
                            userMap.put(userId, user);
                            count[0]++;
                            if (count[0] == total) {
                                // Đã lấy xong tất cả user
                                List<ReviewWithUser> reviewWithUserList = new ArrayList<>();
                                for (Review review : reviews) {
                                    User u = userMap.get(review.getUserid());
                                    reviewWithUserList.add(new ReviewWithUser(review, u));
                                }
                                reviewWithUserLiveData.postValue(reviewWithUserList);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            // Lấy user lỗi, vẫn tăng count
                            count[0]++;
                            if (count[0] == total) {
                                List<ReviewWithUser> reviewWithUserList = new ArrayList<>();
                                for (Review review : reviews) {
                                    User u = userMap.get(review.getUserid());
                                    reviewWithUserList.add(new ReviewWithUser(review, u));
                                }
                                reviewWithUserLiveData.postValue(reviewWithUserList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                // Có thể postValue rỗng hoặc báo lỗi
                reviewWithUserLiveData.postValue(Collections.emptyList());
            }
        });
    }

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
            repository.removeProductListener(productListener);
            productListener = null;
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
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    // Ignore case & contains
                    if (product != null && product.getName() != null
                            && product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                        result.add(product);
                    }
                }
                productsLiveData.setValue(result); // Hoặc callback trả kết quả
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onComplete();
            }
        });
    }

    public void fetchProductDetail(String productId, LoadingHandlerUtil.TaskCallback callback) {
        repository.fetchProductById(productId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                productLiveData.setValue(product);
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                productLiveData.setValue(null);
                callback.onComplete();
            }
        });
    }

    public void fetchReviewsByProductId(String productId) {
        reviewRepository.getReviewsByProductId(productId, new ReviewRepository.ReviewCallback() {
            @Override
            public void onReviewsLoaded(List<Review> reviews) {
                reviewListLiveData.postValue(reviews);
            }

            @Override
            public void onError(String error) {
                // Có thể log lỗi hoặc show Toast
            }
        });
    }

    public void addToCartByAuthId(String authId, String productId, float price, int quantity, Runnable onSuccess, Runnable onError) {
        userRepository.loadUserIdByAuthId(authId, userId -> {
            if (userId != null) {
                addToCart(userId, productId, price, quantity, onSuccess, onError);
            } else {
                if (onError != null) onError.run();
            }
        });
    }

    public void addToCart(String userId, String productId, float price, int quantity, Runnable onSuccess, Runnable onError) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart");
        // 1. Tìm cart của user
        cartRef.orderByChild("userid").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseReference userCartRef;
                        String cartId;

                        // Lấy 3 số cuối hoặc toàn bộ nếu userId < 3
                        String cartSuffix = userId.length() > 3
                                ? userId.substring(userId.length() - 3)
                                : userId;
                        cartId = "cart" + cartSuffix;

                        String now = OffsetDateTime.now(ZoneOffset.ofHours(7))
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                        if (snapshot.exists()) {
                            // User đã có cart, lấy cartId đầu tiên
                            DataSnapshot cartSnap = null;
                            for (DataSnapshot child : snapshot.getChildren()) {
                                cartSnap = child;
                                break;
                            }
                            cartId = cartSnap.getKey();
                            userCartRef = cartRef.child(cartId);
                        } else {
                            // Chưa có cart, tạo mới
                            userCartRef = cartRef.child(cartId);
                            Map<String, Object> cart = new HashMap<>();
                            cart.put("cartid", cartId);
                            cart.put("userid", userId);
                            cart.put("createdat", now);
                            cart.put("promotionid", "");
                            cart.put("totalamount", 0);
                            cart.put("updatedat", now);
                            userCartRef.setValue(cart);
                        }

                        // 2. Thêm hoặc cập nhật sản phẩm trong giỏ
                        DatabaseReference productsRef = userCartRef.child("products");
                        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot allProdSnap) {
                                boolean existed = false;
                                String existedKey = null;

                                // 1. Kiểm tra sản phẩm đã có chưa
                                for (DataSnapshot prodChild : allProdSnap.getChildren()) {
                                    String pid = prodChild.child("productid").getValue(String.class);
                                    if (pid != null && pid.equals(productId)) {
                                        existed = true;
                                        existedKey = prodChild.getKey();
                                        Integer oldQuantity = prodChild.child("quantity").getValue(Integer.class);
                                        int newQuantity = (oldQuantity != null ? oldQuantity : 0) + quantity;
                                        prodChild.getRef().child("quantity").setValue(newQuantity);
                                        break;
                                    }
                                }

                                // 2. Nếu chưa tồn tại, thêm mới với key là số tiếp theo
                                if (!existed) {
                                    // Lấy số lượng hiện tại để làm key mới
                                    long count = allProdSnap.getChildrenCount();
                                    String prodKey = String.valueOf(count); // key = tổng số sản phẩm hiện có (0-based, tức là thêm mới sẽ là next số)
                                    Map<String, Object> prod = new HashMap<>();
                                    prod.put("productid", productId);
                                    prod.put("price", price);
                                    prod.put("quantity", quantity);
                                    prod.put("selected", false);
                                    productsRef.child(prodKey).setValue(prod);
                                }

                                // Cập nhật updatedat
                                userCartRef.child("updatedat").setValue(now);
                                onSuccess.run();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onError.run();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onError.run();
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