package dao;

import entities.Manufacturer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class, that works with Playground object. All standard
 * operations of CRUD. Cannot exist without Connection to DB
 */
@Getter
@Setter
public class ManufacturerDAO implements DAO<Manufacturer> {

    private static final Logger log = LoggerFactory.getLogger(ManufacturerDAO.class);
    private final Connection connection;

    public ManufacturerDAO(Connection connection) {
        this.connection = connection;
    }


    /**
     * Method adds new manufacturer to database, getting the manufacturer
     * from client. First checks whether it is already in database and
     * only then adds it or not
     *
     * @param name Manufacturer object sent by client
     * @return true if object was successfully added to database
     * @throws SQLException if there appeared the error in process of adding
     */
    @Override
    public boolean create(@NotNull Manufacturer name) throws SQLException {
        if (!isExisted(name)) {
            try (PreparedStatement ps = connection.prepareStatement(SQLPlayground.ADD.QUERY)) {
                ps.setString(1, name.getName().name());
                ps.executeUpdate();
                log.info("Adding new Manufacturer object to database");
                return true;
            } catch (SQLException e) {
                log.info("Error is appeared while adding to database");
                connection.rollback();
                throw new RuntimeException("Manufacturer failed to be inserted");
            }
        }
        log.info("Manufacturer is already in database");
        return false;
    }


    /**
     * Search for the object in database and returns mapped
     * object. In case if there is no such an object returns null
     *
     * @param id given from client id number
     * @return object of Manufacturer
     */
    @Override
    public Manufacturer read(int id) {
        try (PreparedStatement ps = connection.prepareStatement(SQLPlayground.GET.QUERY);
        ) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Manufacturer manufacturer = Mapper.mapManufacturer(resultSet);
                log.info("Manufacturer {} is found", manufacturer.getName().name());
                resultSet.close();
                return manufacturer;
            }
        } catch (SQLException e) {
            log.info("An error occurred while searching for the Manufacturer");
            throw new RuntimeException(e);
        }
        log.info("No manufacturer has been found");
        return null;
    }

    /**
     * Updating only existing Manufacturer object
     *
     * @param name object that has to be updated
     * @return true if updating was successfully
     * @throws SQLException in case of error in updating
     */
    @Override
    public boolean update(@NonNull Manufacturer name) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQLPlayground.UPDATE.QUERY)) {
            ps.setString(1, name.getName().name());
            ps.setInt(2, name.getId());
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
        log.info("No such manufacturer in database");
        return false;
    }

    /**
     * Checks on id. If it's 0, then no object will be deleted.
     * If there is such an object, deletes it from database
     *
     * @param id id of the Manufacturer, that has to be deleted from database
     * @return true if it was deleted
     */
    @Override
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQLPlayground.DELETE.QUERY)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                log.info("Manufacturer was deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            log.info("There is an error occurred while deleting the Manufacturer");
            connection.rollback();
            throw new RuntimeException(e);
        }

        log.info("No manufacturer to delete was found");
        return false;
    }

    /**
     * Get all the manufacturers objects from database
     *
     * @return List of Manufacturers
     */
    @Override
    public List<Manufacturer> getAll() {
        List<Manufacturer> allManufacturers = new ArrayList<>();
        log.info("Collecting all manufacturers");
        try(Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery(ManufacturerDAO.SQLPlayground.GET_ALL.QUERY);
            while(resultSet.next()) {
                Manufacturer manufacturer = Mapper.mapManufacturer(resultSet);
                allManufacturers.add(manufacturer);
            }
            resultSet.close();
            return allManufacturers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Checking whether Manufacturer object is in database
     *
     * @param name Manufacturer object
     * @return true if yes
     */

    public boolean isExisted(Manufacturer name) {
        try (PreparedStatement ps = connection.prepareStatement(SQLPlayground.SEARCH_NAME.QUERY)) {
            ps.setString(1, name.getName().name());
            ResultSet resultSet = ps.executeQuery();
            log.info("Checking if manufacturer exists in database");
            return resultSet.next();
        } catch (SQLException e) {
            log.info("There is an error occurred while checking on existence");
            throw new RuntimeException(e);
        }
    }

    private enum SQLPlayground {
        GET("SELECT * FROM manufacturer WHERE manufacturer_id=(?);"),
        ADD("INSERT INTO manufacturer(manufacturer_name) VALUES ((?));"),
        UPDATE("UPDATE manufacturer SET manufacturer_name=(?) WHERE manufacturer_id=(?);"),
        DELETE("DELETE FROM manufacturer WHERE manufacturer_id=(?);"),
        SEARCH_NAME("SELECT manufacturer_name FROM manufacturer WHERE manufacturer_name=(?);"),
        GET_ALL("SELECT * FROM manufacturer");
        final String QUERY;

        SQLPlayground(String query) {
            this.QUERY = query;
        }
    }
}
