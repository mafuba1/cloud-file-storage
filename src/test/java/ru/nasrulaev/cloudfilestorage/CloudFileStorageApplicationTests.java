package ru.nasrulaev.cloudfilestorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.nasrulaev.cloudfilestorage.repositories.PeopleRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CloudFileStorageApplicationTests {

    @Autowired
    private PeopleRepository peopleRepository;

	@Test
	void contextLoads() {
        assertNotNull(peopleRepository);
	}

}
