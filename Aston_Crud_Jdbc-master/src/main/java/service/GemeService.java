package service;

import dao.ModelDAO;
import entities.Model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for Game object, that communicates with DAO
 */
public class GameService {

    private final ModelDAO modelDAO;

    public GameService(Connection connection) {
        this.modelDAO = new ModelDAO(connection);
    }

    public List<Model> getAll() {
        return modelDAO.getAll();
    }

    public boolean add(String name, boolean exclusive) {
        Model model = Model.builder()
                .withName(name)
                .build();
        try {
            return modelDAO.create(model);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Model get(int id) {
        return modelDAO.read(id);
    }

    public boolean update(int id, String name, boolean exclusive) {
        Model updated = Model.builder()
                .withId(id)
                .withName(name)
                .build();
        try {
            return modelDAO.update(updated);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean delete(int id) {
        try {
            return modelDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
