package com.uittrippartner;

import java.text.NumberFormat;
import java.util.Locale;

public class HandleCurrency {
    public String handle(long price){
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(price);
    }
}
