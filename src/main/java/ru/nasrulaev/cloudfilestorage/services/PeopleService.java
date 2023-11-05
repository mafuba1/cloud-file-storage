package ru.nasrulaev.cloudfilestorage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nasrulaev.cloudfilestorage.models.Person;
import ru.nasrulaev.cloudfilestorage.repositories.PeopleRepository;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }


    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }
}
