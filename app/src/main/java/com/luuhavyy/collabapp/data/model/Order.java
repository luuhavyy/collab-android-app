package com.luuhavyy.collabapp.data.model;
import java.util.List;
import java.util.Map;

public class Order {

        private String orderid;
        private String userid;
        private List<ProductItem> products;
        private String status;
        private double totalamount;
        private double promovalue;
        private double totalafterpromo;
        private String orderdate;
        private ShippingDetails shippingdetails;
        private String paymentmethod;

        // Nested class for each product in the order
        public static class ProductItem {
            private String productid;
            private int quantity;
            private double price;
            private boolean allowreview;

            public ProductItem() {
            }

            public ProductItem(String productid, int quantity, double price, boolean allowreview) {
                this.productid = productid;
                this.quantity = quantity;
                this.price = price;
                this.allowreview = allowreview;
            }

            public String getProductid() {
                return productid;
            }

            public void setProductid(String productid) {
                this.productid = productid;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public boolean isAllowreview() {
                return allowreview;
            }

            public void setAllowreview(boolean allowreview) {
                this.allowreview = allowreview;
            }
        }

        // Nested class for shipping details
        public static class ShippingDetails {
            private String address;
            private String phonenumber;
            private String estimatedDelivery;

            public ShippingDetails() {
            }

            public ShippingDetails(String address, String phonenumber, String estimatedDelivery) {
                this.address = address;
                this.phonenumber = phonenumber;
                this.estimatedDelivery = estimatedDelivery;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getPhonenumber() {
                return phonenumber;
            }

            public void setPhonenumber(String phonenumber) {
                this.phonenumber = phonenumber;
            }

            public String getEstimatedDelivery() {
                return estimatedDelivery;
            }

            public void setEstimatedDelivery(String estimatedDelivery) {
                this.estimatedDelivery = estimatedDelivery;
            }
        }

        public Order() {
        }

    public Order(String orderid, String userid, List<ProductItem> products, String status, double totalamount, double promovalue, double totalafterpromo, String orderdate, ShippingDetails shippingdetails, String paymentmethod) {
        this.orderid = orderid;
        this.userid = userid;
        this.products = products;
        this.status = status;
        this.totalamount = totalamount;
        this.promovalue = promovalue;
        this.totalafterpromo = totalafterpromo;
        this.orderdate = orderdate;
        this.shippingdetails = shippingdetails;
        this.paymentmethod = paymentmethod;
    }

    public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public List<ProductItem> getProducts() {
            return products;
        }

        public void setProducts(List<ProductItem> products) {
            this.products = products;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public double getTotalamount() {
            return totalamount;
        }

        public void setTotalamount(double totalamount) {
            this.totalamount = totalamount;
        }

        public double getPromovalue() {
            return promovalue;
        }

        public void setPromovalue(double promovalue) {
            this.promovalue = promovalue;
        }

        public double getTotalafterpromo() {
            return totalafterpromo;
        }

        public void setTotalafterpromo(double totalafterpromo) {
            this.totalafterpromo = totalafterpromo;
        }

        public String getOrderdate() {
            return orderdate;
        }

        public void setOrderdate(String orderdate) {
            this.orderdate = orderdate;
        }

        public ShippingDetails getShippingdetails() {
            return shippingdetails;
        }

        public void setShippingdetails(ShippingDetails shippingdetails) {
            this.shippingdetails = shippingdetails;
        }

        public String getPaymentmethod() {
            return paymentmethod;
        }

        public void setPaymentmethod(String paymentmethod) {
            this.paymentmethod = paymentmethod;
        }
}
