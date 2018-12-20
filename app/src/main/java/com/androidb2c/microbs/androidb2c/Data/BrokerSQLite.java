package com.androidb2c.microbs.androidb2c.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.androidb2c.microbs.androidb2c.Model.CartProduct;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.Model.SearchSuggestion;
import com.androidb2c.microbs.androidb2c.Utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class BrokerSQLite {

    private static final int _dbVersion = 1;
    private static final String _dbName = "B2C.db";

    private static final String _tblCustomer = "Customer";
    private static final String _tblCartDetails = "CartDetails";
    private static final String _tblSearchSuggestions = "SearchSuggestions";


    private DbHelper _dbHelper;
    private final Context _dbContext;
    private static Context context;
    private SQLiteDatabase _dbDatabase = null;



    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, _dbName, null, _dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //KUPAC TABELA
            db.execSQL(" CREATE TABLE " + _tblCustomer + " (" +
                    " CustomerID TEXT," +
                    " CustomerName TEXT," +
                    " Email TEXT," +
                    " Address TEXT," +
                    " City TEXT," +
                    " PhoneNumber TEXT," +
                    " ImageName TEXT" +
                    " )");

            //ORDER TABELA
            db.execSQL("CREATE TABLE " + _tblCartDetails + " (" +
            "ProductID INT, " +
                    "Quantity INT DEFAULT 1)");

            db.execSQL("CREATE TABLE " + _tblSearchSuggestions + " (" +
                    " Suggestion TEXT, ModifiedDate NUMERIC)");


        }





        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Utility.deleteApplicationData(context);
        }
    }

    public void firstInit() throws SQLException {
        if (_dbDatabase == null) {
            DbHelper local = new DbHelper(_dbContext);
            local.getWritableDatabase();
            local = new DbHelper(_dbContext);
            local.getWritableDatabase();
        }
    }

    public BrokerSQLite(Context c) {
        _dbContext = c;
        context = c;

    }

    public BrokerSQLite open() throws SQLException {
        if (_dbDatabase == null) {
            _dbHelper = new DbHelper(_dbContext);
            _dbDatabase = _dbHelper.getWritableDatabase();
        } else if (!_dbDatabase.isOpen()) {
            _dbDatabase = _dbHelper.getWritableDatabase();
        }

        return this;
    }

    public void close() {
        _dbDatabase.close();
    }

    public void customer_I(Customer customer) {

        ContentValues cv = new ContentValues();
        cv.put("CustomerID", customer.getCustomerID());
        cv.put("CustomerName", customer.getFullName());
        cv.put("Email", customer.getEmail());
        cv.put("Address", customer.getAddress());
        cv.put("City", customer.getCity());
        cv.put("PhoneNumber", customer.getPhoneNum());
        cv.put("ImageName", customer.getProfileImageName());
        _dbDatabase.insert(_tblCustomer, null, cv);
    }

    public void customer_D() {
        _dbDatabase.delete(_tblCustomer, null, null);
    }

    public Customer getCustomer(){
        Customer customer = new Customer();
        String sql = "SELECT * FROM Customer";
        Cursor c = _dbDatabase.rawQuery(sql, null);
        if(c.getCount() > 0){
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            customer.setCustomerID(c.getString(c.getColumnIndex("CustomerID")));
            customer.setFullName(c.getString(c.getColumnIndex("CustomerName")));
            customer.setEmail(c.getString(c.getColumnIndex("Email")));
            customer.setAddress(c.getString(c.getColumnIndex("Address")));
            customer.setCity(c.getString(c.getColumnIndex("City")));
            customer.setPhoneNum(c.getString(c.getColumnIndex("PhoneNumber")));
            customer.setProfileImageName(c.getString(c.getColumnIndex("ImageName")));
        }
            c.close();
            return customer;
        } else{
            return null;
        }

    }

    public void addProductIDToCart(int productID){
        ContentValues cv = new ContentValues();
        cv.put("ProductID", productID);

        _dbDatabase.insert(_tblCartDetails, null, cv);
    }

    public void removeProductFromCart(int productID){

        _dbDatabase.delete(_tblCartDetails, "ProductID = " + productID, null);
    }

    public boolean checkIfProductExistsInCart(int productID) {

        String sql = "SELECT * FROM CartDetails WHERE ProductID = " + productID;
        Cursor c = _dbDatabase.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    public List<Integer> getCartProductIDs(){
        List<Integer> listOfIDs = new ArrayList<>();
        String sql =  "SELECT ProductID FROM CartDetails";
        Cursor c = _dbDatabase.rawQuery(sql, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            listOfIDs.add(c.getInt(0));
        }
    c.close();
        return listOfIDs;
    }

    public int getNumberOfArticlesInCart() {
     String sql = "SELECT ProductID FROM CartDetails";
     Cursor c = _dbDatabase.rawQuery(sql, null);
     int count = c.getCount();
     c.close();
     return  count;

    }

    public List<CartProduct> getCartProducts(){
        String sql = "SELECT * FROM CartDetails";
        List<CartProduct> list = new ArrayList<>();
        Cursor c = _dbDatabase.rawQuery(sql, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            list.add(new CartProduct(c.getInt(c.getColumnIndex("ProductID")), c.getInt(c.getColumnIndex("Quantity"))));
        }
        c.close();
        return list;
    }

    public void setCartProductQuantity(int productID, int quantity){
        ContentValues cv = new ContentValues();
        cv.put("Quantity",quantity );
        _dbDatabase.update(_tblCartDetails, cv, "ProductID = " + productID, null);

    }

    public void insertProductAndQuantity(int productID, int quantity){
        ContentValues cv = new ContentValues();
        cv.put("ProductID", productID);
        cv.put("Quantity", quantity);
        _dbDatabase.insert(_tblCartDetails, null, cv);
    }

    public void deleteProductFromCart(int productID){

        _dbDatabase.delete(_tblCartDetails, "ProductID = " + productID, null );
    }

    public void deleteAllFromCart(){

        _dbDatabase.delete(_tblCartDetails, null, null );
    }

    public void insertSuggestion(String suggestion){

        List<SearchSuggestion> suggestions = new ArrayList<>();
        String sql = "SELECT * FROM SearchSuggestions ORDER BY ModifiedDate DESC";
        Cursor c = _dbDatabase.rawQuery(sql, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            suggestions.add(new SearchSuggestion(c.getString(0), c.getLong(1)));
        }
        if(suggestions.size()==0){
            ContentValues cv = new ContentValues();
            cv.put("Suggestion", suggestion);
            cv.put("ModifiedDate", System.currentTimeMillis());
            _dbDatabase.insert("SearchSuggestions", null, cv);
            return;
        }
        if(suggestions.size()>= 6) {
            for(SearchSuggestion sugg : suggestions){
                if (TextUtils.equals(sugg.getSuggestion().toLowerCase(), suggestion.toLowerCase())) {

                    _dbDatabase.delete("SearchSuggestions", " Suggestion = '" + suggestion +"'", null);
                    ContentValues cv = new ContentValues();
                    cv.put("Suggestion", suggestion);
                    cv.put("ModifiedDate", System.currentTimeMillis());
                    _dbDatabase.insert("SearchSuggestions", null, cv);
                    return;

                } else{
                    _dbDatabase.delete("SearchSuggestions", null, null);
                    for (int i = 0; i <suggestions.size()-1 ; i++) {
                        ContentValues cv = new ContentValues();
                        cv.put("Suggestion", suggestions.get(i).getSuggestion());
                        cv.put("ModifiedDate", suggestions.get(i).getTimeMiliSec());
                        _dbDatabase.insert("SearchSuggestions", null, cv);
                    }
                    ContentValues cv = new ContentValues();
                    cv.put("Suggestion", suggestion);
                    cv.put("ModifiedDate", System.currentTimeMillis());
                    _dbDatabase.insert("SearchSuggestions", null, cv);

                }

            }

        }else{

            for(SearchSuggestion sugg : suggestions){
                if (TextUtils.equals(sugg.getSuggestion().toLowerCase(), suggestion.toLowerCase())) {
                    return;
                }
                }
            ContentValues cv = new ContentValues();
            cv.put("Suggestion", suggestion);
            cv.put("ModifiedDate", System.currentTimeMillis());
            _dbDatabase.insert("SearchSuggestions", null, cv);

        }

    }

    public ArrayList<String> getSuggestionsFromDb(){
       ArrayList<String> suggestions = new ArrayList<>();
        String sql = "SELECT * FROM SearchSuggestions ORDER BY ModifiedDate DESC LIMIT 5";
        Cursor c = _dbDatabase.rawQuery(sql, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            suggestions.add(c.getString(0));
        }
        return suggestions;
    }

}
