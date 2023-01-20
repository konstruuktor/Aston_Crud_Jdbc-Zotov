package dao;

import entities.Manufacturer;
import entities.Models;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerDAOTest extends AbstractDAOTest {


    @DisplayName("Check on creating new manufacturer")
    @Test
    void createNewPlayground() throws SQLException {
        // given
        Manufacturer alreadyExisted = Manufacturer.builder()
                .withName(Models.AUDI).build();
        Manufacturer newManufacturer = Manufacturer.builder()
                .withName(Models.LINCOLN).build();

        // when
        boolean hasNotToBeAdded = manufacturerDAO.create(alreadyExisted);
        boolean hasToBeAdded = manufacturerDAO.create(newManufacturer);
        Manufacturer manufacturer = manufacturerDAO.read(4);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM manufacturer")) {
            resultSet.next();
            int amountOfRows = resultSet.getInt(1);

            // then
            assertFalse(hasNotToBeAdded);
            assertTrue(hasToBeAdded);
            assertEquals(4, amountOfRows);
            assertEquals(manufacturer.getName(), Models.LINCOLN);
        }
    }

    @DisplayName("Check on incorrect id")
    @Test
    void getAbsentPlayground() {
        // given

        // when
        Manufacturer noSuchId = manufacturerDAO.read(10);
        Manufacturer negativeId = manufacturerDAO.read(-5);

        // then
        assertNull(noSuchId);
        assertNull(negativeId);
    }

    @DisplayName("Updating the existing and non existing playground")
    @Test
    void updatingExistingAndNonExistingPlayground() throws SQLException {
        // given
        Manufacturer updateExisting = Manufacturer.builder()
                .withName(Models.LINCOLN)
                .withId(1).build();
        Manufacturer updateNonExisting = Manufacturer.builder()
                .withName(Models.AUDI)
                .withId(25)
                .build();

        // when
        boolean resultOnExisting = manufacturerDAO.update(updateExisting);
        boolean resultOnNonExisting = manufacturerDAO.update(updateNonExisting);
        Manufacturer hasToBeUpdated = manufacturerDAO.read(1);

        // then
        assertTrue(resultOnExisting);
        assertFalse(resultOnNonExisting);
        assertEquals(Models.LINCOLN, hasToBeUpdated.getName());
    }

    @DisplayName("Deleting non existing and existing playgroung")
    @Test
    void deletePlaygrounds() throws SQLException {
        // given
        int existedPlayground = 2;
        int nonExistedPlayground = 9;

        // when
        boolean deleteTrue = manufacturerDAO.delete(existedPlayground);
        boolean deleteFalse = manufacturerDAO.delete(nonExistedPlayground);

        // then
        assertFalse(deleteFalse);
        assertTrue(deleteTrue);

    }
}