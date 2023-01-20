package dao;

import entities.Manufacturer;
import entities.Market;
import entities.Model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MarketDAOTest extends AbstractDAOTest {

    @DisplayName("Creates new Play in database")
    @Test
    void create() throws SQLException {
        // given
        Model existedModel = modelDAO.read(2);
        Manufacturer pg1 = manufacturerDAO.read(3);
        Market market = Market.builder().withManufacturer(pg1).withModel(existedModel)
                .withPrice(25.55).withAmount(233).build();

        Model newModel = Model.builder().withName("Best Car").build();
        modelDAO.create(newModel);
        Model bestModelWithId = modelDAO.read(5);
        Market anotherMarket = Market.builder().withManufacturer(pg1).withModel(bestModelWithId)
                .withPrice(25.55).withAmount(233).build();

        // when
        boolean createFalse = marketDAO.create(market);
        boolean createTrue = marketDAO.create(anotherMarket);

        // then
        assertTrue(createTrue);
        assertFalse(createFalse);
    }

    @DisplayName("Check on reading play object")
    @Test
    void read() {
        // given

        // when
        Market existedMarket = marketDAO.read(2);
        Market nonExistedMarket = marketDAO.read(10);

        // then
        assertEquals("7 series", existedMarket.getModel().getName());
        assertNull(nonExistedMarket);
    }

    @DisplayName("Updating play object")
    @Test
    void update() throws SQLException {
        // given
        Market existedGame = marketDAO.read(1);
        existedGame.setAmount(299);
        existedGame.setPrice(24.95);

        // when
        marketDAO.update(existedGame);
        Market updated = marketDAO.read(1);

        // then
        assertEquals(24.95, updated.getPrice());
    }

    @DisplayName("Deleting the object")
    @Test
    void delete() throws SQLException {
        // given

        // when
        marketDAO.delete(2);
        boolean notDeleted = marketDAO.delete(99);
        Market notFound = marketDAO.read(2);

        // then
        assertNull(notFound);
        assertFalse(notDeleted);
    }
}