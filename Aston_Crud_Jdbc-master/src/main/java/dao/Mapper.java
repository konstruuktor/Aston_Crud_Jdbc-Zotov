package dao;

import entities.Manufacturer;
import entities.Market;
import entities.Model;
import entities.Models;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps data from database to Objects
 */
public final class Mapper {

    static final String MARKET_ID = "market_id";
    static final String MANUFACTURER_ID = "manufacturer_id";
    static final String MODEL_ID = "model_id";


    static final String MODEL_NAME = "model_name";
    static final String MANUFACTURER_NAME = "manufacturer_name";
    static final String PRICE = "price";
    static final String AMOUNT = "amount";

    /**
     * Maps market fields into Market Object
     *
     * @param resultSet data from database
     * @return Market object
     * @throws SQLException if SQL command was incorrect
     */
    static Market mapMarket(ResultSet resultSet) throws SQLException {
        return Market.builder()
                .withId(resultSet.getInt(MARKET_ID))
                .withManufacturer(mapManufacturer(resultSet))
                .withModel(mapModel(resultSet))
                .withPrice(resultSet.getDouble(PRICE))
                .withAmount(resultSet.getInt(AMOUNT))
                .build();
    }


    /**
     * Maps model fields into Model Object
     *
     * @param resultSet data from database
     * @return Model object
     * @throws SQLException if SQL command was incorrect
     */
    static Model mapModel(ResultSet resultSet) throws SQLException {
        return Model.builder()
                .withId(resultSet.getInt(MODEL_ID))
                .withName(resultSet.getString(MODEL_NAME))
                .build();
    }


    /**
     * Maps manufacturer fields into Manufacturer Object
     *
     * @param resultSet data from database
     * @return Manufacturer object
     * @throws SQLException if SQL command was incorrect
     */
    static Manufacturer mapManufacturer(ResultSet resultSet) throws SQLException {
        return Manufacturer.builder()
                .withId(resultSet.getInt(MANUFACTURER_ID))
                .withName(Models.valueOf(resultSet.getString(MANUFACTURER_NAME)))
                .build();
    }

}
