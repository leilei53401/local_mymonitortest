package com.voole.ad.utils.datasources;

public class DataSourceTypeHolder {
	
	private static final ThreadLocal<String> typeHolder = new ThreadLocal<String>();  
	  
    public static void setCustomerType(String customerType) {  
        typeHolder.set(customerType);  
    }  
  
    public static String getCustomerType() {  
        return typeHolder.get();  
    }  
  
    public static void clearCustomerType() {  
        typeHolder.remove();  
    }  
}
