package ru.nasrulaev.cloudfilestorage.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.nasrulaev.cloudfilestorage.models.Person;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PeopleRepositoryTests {

    @Autowired
    private PeopleRepository peopleRepository;

    @Test
    public void PeopleRepository_FindByEmail_ReturnPerson() {
        Person person = new Person(
                "email@mail.com",
                "passwordA12~"
        );

        peopleRepository.save(person);

        assertTrue(peopleRepository.findByEmail(person.getEmail()).isPresent());
    }

    @Test
    public void PeopleRepository_FindByEmail_ReturnNull() {
        Person person = new Person(
                "email@mail.com",
                "passwordA12~"
        );

        peopleRepository.delete(person);

        assertTrue(peopleRepository.findByEmail("email@mail.com").isEmpty());
    }
}
