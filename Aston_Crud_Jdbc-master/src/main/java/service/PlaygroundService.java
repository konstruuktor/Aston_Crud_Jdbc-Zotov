package service;

import dao.ManufacturerDAO;
import entities.Manufacturer;
import entities.Models;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for Game object, that communicates with DAO
 */
public class PlaygroundService {

    private final ManufacturerDAO manufacturerDAO;

    public PlaygroundService(Connection connection) {
        this.manufacturerDAO = new ManufacturerDAO(connection);
    }

    /**
     * Get all the playgrounds objects from database
     *
     * @return List of Playgrounds
     */
    public List<Manufacturer> getAll() {
        return manufacturerDAO.getAll();
    }


    /**
     * Method adds new playground to database, getting the Playground
     * from client. First checks whether it is already in database and
     * only then adds it or not
     *
     * @param name name of the Playground sent by client
     * @return true if object was successfully added to database
     */
    public boolean add(String name) {
        Manufacturer manufacturer = Manufacturer.builder()
                .withName(Models.valueOf(name))
                .build();
        try {
            return manufacturerDAO.create(manufacturer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search for the object in database and returns mapped
     * object. In case if there is no such an object returns null
     *
     * @param id given from client id number
     * @return object of Playground
     */
    public Manufacturer get(int id) {
        return manufacturerDAO.read(id);
    }

    /**
     * Updating only existing Playground object
     *
     * @param id   the playground id got from client
     * @param name object that has to be updated
     * @return true if updating was successfully
     */
    public boolean update(int id, String name) {
        Manufacturer updated = Manufacturer.builder()
                .withId(id)
                .withName(Models.valueOf(name))
                .build();
        try {
            return manufacturerDAO.update(updated);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Checks on id. If it's 0, then no object will be deleted.
     * If there is such an object, deletes it from database
     *
     * @param id id of the Playground, that has to be deleted from database
     * @return true if it was deleted
     */
    public boolean delete(int id) {
        try {
            return manufacturerDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
