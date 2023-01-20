package dao;

import entities.Model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ModelDAOTest extends AbstractDAOTest {


    @DisplayName("Add non existed and existed game")
    @Test
    void create() throws SQLException {
        // given
        Model nonExisted = Model.builder().withName("3 series").build();
        Model existedModel = Model.builder().withName("A5").build();
        Model existedSmallLetters = Model.builder().withName("e coupe").build();

        // when
        boolean nonExistedTrue = modelDAO.create(nonExisted);
        Model mario = modelDAO.read(5);
        boolean existedFalse = modelDAO.create(existedModel);
        boolean existedWithMistakeFalse = modelDAO.create(existedSmallLetters);

        // then
        assertTrue(nonExistedTrue);
        assertEquals("3 series", mario.getName());
        assertFalse(existedFalse);
        assertFalse(existedWithMistakeFalse);
    }

    @DisplayName("Get non existed and existed game")
    @Test
    void read() {
        // given

        // when
        Model existedModel = modelDAO.read(3);
        Model nonExisted = modelDAO.read(25);

        // then
        assertEquals("E coupe", existedModel.getName());
        assertNull(nonExisted);
    }

    @Test
    void update() throws SQLException {
        // given
        Model updatedModel = Model.builder().withId(1).withName("New Model").build();
        Model modelWithoutId = Model.builder().withName("No ID model").build();

        // when
        boolean updatedTrue = modelDAO.update(updatedModel);
        boolean updatedFalse = modelDAO.update(modelWithoutId);

        // then
        assertTrue(updatedTrue);
        assertFalse(updatedFalse);

    }

    @Test
    void delete() throws SQLException {
        // give
        int existedModel = 3;
        int nonExistedModel = 9;
        int anotherNonExistedModel = -5;


        // when
        boolean deleteTrue = modelDAO.delete(existedModel);
        boolean deleteFalse = modelDAO.delete(nonExistedModel);
        boolean deleteFalseNegativeId = modelDAO.delete(anotherNonExistedModel);

        // then
        assertTrue(deleteTrue);
        assertFalse(deleteFalseNegativeId);
        assertFalse(deleteFalse);
    }
}