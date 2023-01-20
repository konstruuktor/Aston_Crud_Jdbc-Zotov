package dao;

import entities.Market;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Getter
@Setter
public class MarketDAO implements DAO<Market> {

    private static final Logger log = getLogger(MarketDAO.class);
    private final Connection connection;

    public MarketDAO(Connection connection) {
        this.connection = connection;
    }


    /**
     * Method adds new market to database, getting the Market
     * from client. First checks whether it is already in database and
     * only then adds it or not
     *
     * @param name Market object sent by client
     * @return true if object was successfully added to database
     * @throws SQLException if there appeared the error in process of adding
     */
    @Override
    public boolean create(@NotNull Market name) throws SQLException {
        if (isExisted(name).isEmpty()) {
            log.info("Market object is new. I will create it");
            try (PreparedStatement ps = connection.prepareStatement(MarketDAO.SQLPlay.ADD.QUERY)) {
                ps.setInt(1, name.getManufacturer().getId());
                ps.setInt(2, name.getModel().getId());
                ps.setDouble(3, name.getPrice());
                ps.setInt(4, name.getAmount());
                ps.executeUpdate();
                log.info("Adding new Market object to database");
                return true;
            } catch (SQLException e) {
                log.info("Error is appeared while adding to database");
                connection.rollback();
                throw new RuntimeException(e);
            }
        }
        log.info("Market is already in database");
        return false;
    }


    /**
     * Search for the object in database and returns mapped
     * object. In case if there is no such an object returns null
     *
     * @param id given from client id number
     * @return object of Market
     */
    @Override
    public Market read(int id) {
        try (PreparedStatement ps = connection.prepareStatement(MarketDAO.SQLPlay.GET.QUERY)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Market market = Mapper.mapMarket(resultSet);
                log.info("Market object is found");
                resultSet.close();
                return market;
            }
        } catch (SQLException e) {
            log.info("An error occurred while searching for the Market object");
            throw new RuntimeException(e);
        }
        log.info("No market has been found");
        return null;
    }

    /**
     * Updating only existing Model object, searching by id
     *
     * @param name object that has to be updated
     * @return true if updating was successfully
     * @throws SQLException in case of error in updating
     */
    @Override
    public boolean update(@NonNull Market name) throws SQLException {
        if (name.getId() != 0) {
            try (PreparedStatement ps = connection.prepareStatement(MarketDAO.SQLPlay.UPDATE.QUERY)) {
                ps.setInt(1, name.getManufacturer().getId());
                ps.setInt(2, name.getModel().getId());
                ps.setDouble(3, name.getPrice());
                ps.setInt(4, name.getAmount());
                ps.setInt(5, name.getModel().getId());
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    log.info("Updated successfully");
                    return true;
                }
            } catch (SQLException e) {
                log.info("There is an error occurred during updating. Rolling back operation");
                connection.rollback();
                throw new RuntimeException(e);
            }
        }
        log.info("No such market in database or you didn't specify the id");
        return false;
    }

    /**
     * Checks on id. If it's 0, then no object will be deleted.
     * If there is such an object, deletes it from database
     *
     * @param id Market object, that has to be deleted from database
     * @return true if it was deleted
     */
    @Override
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(MarketDAO.SQLPlay.DELETE.QUERY)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                log.info("Market was deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            log.info("There is an error occurred while deleting the market");
            connection.rollback();
            throw new RuntimeException(e);
        }

        log.info("No market to delete was found or you didn't specify id");
        return false;
    }

    /**
     * Get all the market objects from database
     *
     * @return List of Markets
     */
    @Override
    public List<Market> getAll() {
        List<Market> allMarkets = new ArrayList<>();
        log.info("Collecting all markets");
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery(MarketDAO.SQLPlay.GET_ALL.QUERY);
            while (resultSet.next()) {
                Market market = Mapper.mapMarket(resultSet);
                allMarkets.add(market);
            }
            resultSet.close();
            return allMarkets;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Checking whether Market objects is in database
     *
     * @param name Market object
     * @return true if yes
     */

    public List<Market> isExisted(Market name) {
        List<Market> markets = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(MarketDAO.SQLPlay.SEARCH_NAME.QUERY)) {
            ps.setString(1, name.getModel().getName());
            ResultSet resultSet = ps.executeQuery();
            log.info("Checking if market exists in database");
            while (resultSet.next()) {
                Market market = Mapper.mapMarket(resultSet);
                if (name.getModel().getId() == market.getModel().getId()) {
                    log.info("You try to add absolutely the same model");
                    markets.add(market);
                }
            }
            return markets;
        } catch (SQLException e) {
            log.info("There is an error occurred while checking on existence");
            throw new RuntimeException(e);
        }
    }

    private enum SQLPlay {
        GET("""
                SELECT market_id, manufacturer.manufacturer_id, manufacturer_name, model.model_id, model_name, price, amount
                FROM market JOIN model ON market.model_id=model.model_id
                          JOIN manufacturer ON market.manufacturer_id=manufacturer.manufacturer_id
                WHERE market_id=(?);
                """),
        ADD("INSERT INTO market(manufacturer_id, model_id, price, amount) VALUES ((?), (?), (?), (?));"),
        UPDATE("UPDATE market SET manufacturer_id=(?), model_id=(?), price=(?), amount=(?) WHERE model_id=(?);"),
        DELETE("DELETE FROM market WHERE market_id=(?);"),
        SEARCH_NAME("""
                SELECT market_id, manufacturer.manufacturer_id, manufacturer_name, model.model_id, model_name, price, amount
                FROM market JOIN model ON market.model_id=model.model_id
                          JOIN manufacturer ON market.manufacturer_id=manufacturer.manufacturer_id
                WHERE model_name ILIKE ((?));
                """
        ),
        GET_ALL("""
                SELECT market_id, manufacturer.manufacturer_id, manufacturer_name, model.model_id, model_name, price, amount
                FROM market JOIN model ON market.model_id=model.model_id
                          JOIN manufacturer ON market.manufacturer_id=manufacturer.manufacturer_id
                """);
        final String QUERY;

        SQLPlay(String query) {
            this.QUERY = query;
        }
    }


}
