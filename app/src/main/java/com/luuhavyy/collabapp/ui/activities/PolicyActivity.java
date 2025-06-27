package com.luuhavyy.collabapp.ui.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.luuhavyy.collabapp.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class PolicyActivity extends AppCompatActivity {

    private final Map<String, CharSequence> policyData = new LinkedHashMap<String, CharSequence>() {{
        put("Shipping", getFormattedShippingContent());
        put("Return policy", "Return policy content here...");
        put("Service", "Service content here...");
        put("Assurance", "Assurance content here...");
        put("Security", "Security content here...");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        LinearLayout policyContainer = findViewById(R.id.policy_container);
        for (Map.Entry<String, CharSequence> entry : policyData.entrySet()) {
            policyContainer.addView(createPolicyItem(entry.getKey(), entry.getValue()));
        }
    }

    private CharSequence getFormattedShippingContent() {
        String content =
                "Thank you for shopping with us! We’re committed to delivering your order quickly, safely, and affordably. Please review our shipping policy below:\n\n" +
                        "1. Processing Time\n" +
                        "All orders are processed within 1–2 business days (excluding weekends and holidays).\n" +
                        "You will receive a confirmation email with tracking information once your order has been shipped.\n\n" +
                        "2. Shipping Rates & Delivery Estimates\n" +
                        "We offer standard and express shipping options at checkout.\n" +
                        "Estimated delivery times:\n" +
                        "In HCM: 1-2 business days\n" +
                        "Others: 3-5 days\n" +
                        "Note: Delivery times may vary depending on your location and external factors (e.g., weather, carrier delays).\n\n" +
                        "3. Shipping Locations\n" +
                        "We currently ship nationwide.\n" +
                        "For international shipping, please contact our support team.\n\n" +
                        "4. Order Tracking\n" +
                        "Once shipped, you’ll receive a tracking number via email to monitor your delivery.\n" +
                        "You can also track your order anytime via our website’s Order Tracking page.\n\n" +
                        "5. Shipping Issues\n" +
                        "If your package is delayed, lost, or damaged during transit, please contact us at support@yourstore.com within 5 days of the expected delivery date.\n\n" +
                        "6. Incorrect Shipping Information\n" +
                        "Please ensure your shipping details are correct. We are not responsible for orders shipped to incorrectly provided addresses.\n\n" +
                        "\uD83D\uDCEC Still have questions? Contact our friendly support team — we’re happy to help!";

        String[] boldTitles = {
                "1. Processing Time",
                "2. Shipping Rates & Delivery Estimates",
                "3. Shipping Locations",
                "4. Order Tracking",
                "5. Shipping Issues",
                "6. Incorrect Shipping Information"
        };

        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        for (String title : boldTitles) {
            int start = content.indexOf(title);
            if (start >= 0) {
                ssb.setSpan(new StyleSpan(Typeface.BOLD), start, start + title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ssb;
    }

    private View createPolicyItem(String title, CharSequence content) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(0, 28, 0, 28);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView tvIcon = new TextView(this);
        tvIcon.setText("▼");
        tvIcon.setTextSize(18);
        tvIcon.setPadding(16, 0, 0, 0);

        header.addView(tvTitle);
        header.addView(tvIcon);

        TextView tvContent = new TextView(this);
        tvContent.setText(content);
        tvContent.setTextSize(15);
        tvContent.setVisibility(View.GONE);
        tvContent.setPadding(0, 0, 0, 40);

        header.setOnClickListener(v -> {
            if (tvContent.getVisibility() == View.GONE) {
                tvContent.setVisibility(View.VISIBLE);
                tvIcon.setText("▲");
            } else {
                tvContent.setVisibility(View.GONE);
                tvIcon.setText("▼");
            }
        });

        header.setBackgroundResource(android.R.drawable.list_selector_background);

        root.addView(header);
        root.addView(tvContent);

        return root;
    }
}
