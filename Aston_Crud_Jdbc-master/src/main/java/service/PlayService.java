package service;

import dao.ModelDAO;
import dao.MarketDAO;
import dao.ManufacturerDAO;
import entities.Market;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PlayService {

    private final MarketDAO marketDAO;
    private final ManufacturerDAO manufacturerDAO;
    private final ModelDAO modelDAO;


    public PlayService(Connection connection) {
        this.marketDAO = new MarketDAO(connection);
        this.manufacturerDAO = new ManufacturerDAO(connection);
        this.modelDAO = new ModelDAO(connection);
    }

    /**
     * Get all the play objects from database
     *
     * @return List of Plays
     */
    public List<Market> getAll() {
        return marketDAO.getAll();
    }


    /**
     * Method adds new play to database, getting the Play
     * from client. First checks whether it is already in database and
     * only then adds it or not
     *
     * @param playgroundId id of the playground got from client
     * @param gameId       id of the game sent by client
     * @param price        name of the game for specific playground
     * @param amount       amount of the specific game for particular playground
     * @return true if object was successfully added to database
     */
    public boolean add(int playgroundId, int gameId, double price, int amount) {
        Market market = Market.builder()
                .withManufacturer(manufacturerDAO.read(playgroundId))
                .withModel(modelDAO.read(gameId))
                .withPrice(price)
                .withAmount(amount)
                .build();
        try {
            return marketDAO.create(market);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search for the object in database and returns mapped
     * object. In case if there is no such an object returns null
     *
     * @param id given from client id number
     * @return object of Play
     */
    public Market get(int id) {
        return marketDAO.read(id);
    }

    /**
     * Updating only existing Play object
     *
     * @param id           the play id got from client
     * @param playgroundId id of the playground for play
     * @param gameId       id of the game for play
     * @param price        updated data for price
     * @param amount       updated data for amount
     * @return true if updating was successfully
     */
    public boolean update(int id, int playgroundId, int gameId, double price, int amount) {
        Market updated = Market.builder()
                .withId(id)
                .withManufacturer(manufacturerDAO.read(playgroundId))
                .withModel(modelDAO.read(gameId))
                .withPrice(price)
                .withAmount(amount)
                .build();
        try {
            return marketDAO.update(updated);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Checks on id.
     * If there is such an object, deletes it from database
     *
     * @param id id of the Play, that has to be deleted from database
     * @return true if it was deleted
     */
    public boolean delete(int id) {
        try {
            return marketDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
