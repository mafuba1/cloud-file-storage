package ru.nasrulaev.cloudfilestorage.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nasrulaev.cloudfilestorage.models.Person;
import ru.nasrulaev.cloudfilestorage.repositories.PeopleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PeopleServiceTests {

    @Mock
    private PeopleRepository peopleRepository;

    @InjectMocks
    private PeopleService peopleService;

    @Test
    public void PeopleRepository_SavePerson_OK() {
        Person person = new Person(
                "email@email.ru",
                "password"
        );
        peopleService.save(person);

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        verify(peopleRepository).save(captor.capture());

        Person capturedPerson = captor.getValue();
        assertEquals(person, capturedPerson);
    }
}