package com.cxq.factory;

/**
 * @author JoeZhou
 */
public class DataSourceFactory {
    public static DataSourcePoolByFactory getDataSource() {
        return new DataSourcePoolByFactory();
    }
}