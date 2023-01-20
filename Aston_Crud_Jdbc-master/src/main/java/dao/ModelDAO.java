package dao;

import entities.Model;
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
public class ModelDAO implements DAO<Model> {

    private static final Logger log = getLogger(ModelDAO.class);

    private final Connection connection;

    public ModelDAO(Connection connection) {
        this.connection = connection;


    }

    /**
     * Method adds new model to database, getting the Model
     * from client. First checks whether it is already in database and
     * only then adds it or not
     *
     * @param name Model object sent by client
     * @return true if object was successfully added to database
     * @throws SQLException if there appeared the error in process of adding
     */
    @Override
    public boolean create(@NotNull Model name) throws SQLException {
        if (!isExisted(name)) {
            try (PreparedStatement ps = connection.prepareStatement(ModelDAO.SQLGame.ADD.QUERY)) {
                ps.setString(1, name.getName());
                ps.executeUpdate();
                log.info("Adding '{}' object to database", name.getName());
                return true;
            } catch (SQLException e) {
                log.info("Error is appeared while adding to database");
                connection.rollback();
                throw new RuntimeException("Model failed to be inserted");
            }
        }
        log.info("Model is already in database");
        return false;
    }


    /**
     * Search for the object in database and returns mapped
     * object. In case if there is no such an object returns null
     *
     * @param id given from client id number
     * @return object of Model
     */
    @Override
    public Model read(int id) {
        try (PreparedStatement ps = connection.prepareStatement(ModelDAO.SQLGame.GET.QUERY)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Model model = Mapper.mapModel(resultSet);
                log.info("Model {} is found", model.getName());
                resultSet.close();
                return model;
            }
        } catch (SQLException e) {
            log.info("An error occurred while searching for the Model");
            throw new RuntimeException(e);
        }
        log.info("No model has been found");
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
    public boolean update(@NonNull Model name) throws SQLException {
        if (name.getId() != 0) {
            try (PreparedStatement ps = connection.prepareStatement(SQLGame.UPDATE.QUERY)) {
                ps.setString(1, name.getName());
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
        }
        log.info("No such model in database or you didn't specify the id");
        return false;
    }

    /**
     * Checks on id. If it's 0, then no object will be deleted.
     * If there is such an object, deletes it from database
     *
     * @param id id of the model, that has to be deleted from database
     * @return true if it was deleted
     */
    @Override
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(ModelDAO.SQLGame.DELETE.QUERY)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                log.info("Model was deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            log.info("There is an error occurred while deleting the model");
            connection.rollback();
            throw new RuntimeException(e);
        }

        log.info("No model to delete was found or you didn't specify id");
        return false;
    }

    /**
     * Get all the model objects from database
     *
     * @return List of Models
     */
    @Override
    public List<Model> getAll() {
        List<Model> allModels = new ArrayList<>();
        log.info("Collecting all models");
        try(Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery(SQLGame.GET_ALL.QUERY);
            while(resultSet.next()) {
                Model model = Mapper.mapModel(resultSet);
                allModels.add(model);
            }
            resultSet.close();
            return allModels;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Checking whether Model object is in database
     *
     * @param name Model object
     * @return true if yes
     */

    public boolean isExisted(Model name) {
        try (PreparedStatement ps = connection.prepareStatement(ModelDAO.SQLGame.SEARCH_NAME.QUERY)) {
            ps.setString(1, name.getName());
            ResultSet resultSet = ps.executeQuery();
            log.info("Checking if model '{}' exists in database", name.getName());
            return resultSet.next();
        } catch (SQLException e) {
            log.info("There is an error occurred while checking on existence");
            throw new RuntimeException(e);
        }
    }

    private enum SQLGame {
        GET("SELECT * FROM model WHERE model_id=(?);"),
        ADD("INSERT INTO model(model_name) VALUES ((?));"),
        UPDATE("UPDATE model SET model_name=(?) WHERE model_id=(?);"),
        DELETE("DELETE FROM model WHERE model_id=(?);"),
        SEARCH_NAME("SELECT model_name FROM model WHERE model_name ILIKE ((?));"),
        GET_ALL("SELECT * FROM model");
        final String QUERY;

        SQLGame(String query) {
            this.QUERY = query;
        }
    }
}
