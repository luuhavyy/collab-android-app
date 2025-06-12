package com.luuhavyy.collabapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    public int imageRes;
    public String title;
    public String subtitle;
}