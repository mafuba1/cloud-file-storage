package ru.nasrulaev.cloudfilestorage.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.nasrulaev.cloudfilestorage.dto.PersonDTO;
import ru.nasrulaev.cloudfilestorage.services.PeopleService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PeopleService peopleService;

    @Test
    public void AuthController_LoginPage_Ok() throws Exception {
        this.mockMvc.perform(
                get("/login")
        )
                .andExpect(status().isOk());
    }

    @Test
    public void AuthController_RegistrationPage_Ok() throws Exception {
        this.mockMvc.perform(
                get("/register")
        )
                .andExpect(status().isOk());
    }

    @Test
    public void Registration_PersonDTO_SavePerson() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "passwordA12~"
        );

        this.mockMvc.perform(
                    post("/register")
                            .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }

    @Test
    public void Registration_BadEmail_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "emailemail.com",
                "password"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());
    }

    @Test
    public void Registration_BlankEmail_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "   ",
                "password"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());
    }

    @Test
    public void Registration_BlankPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "                   "
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());
    }

    @Test
    public void Registration_ShortPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "aA~23"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().concat("aA~23")
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());

    }

    @Test
    public void Registration_LongPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "aA~23Idqwe123n.~?..wqe#ljksaduqweqnwelqkwjei*(213"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().substring(0, 30)
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }

    @Test
    public void Registration_NoUpperCaseCharInPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "a~23dwe123n.~*(213"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().concat("A")
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }

    @Test
    public void Registration_NoLowerCaseCharInPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "K~23DJN123L.~*(213"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().concat("a")
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }

    @Test
    public void Registration_NoSpecialCharInPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "K23DJddN123L213"
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().concat("~")
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }

    @Test
    public void Registration_NoDigitsInPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "K~DJNdaL.~*("
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().concat("1")
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }

    @Test
    public void Registration_WhitespacesInPassword_PersonNotSaved() throws Exception {
        PersonDTO personDTO = new PersonDTO(
                "email@email.com",
                "K~DJN1 3Ld.~*("
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService, never()).save(any());

        personDTO.setPassword(
                personDTO.getPassword().replaceAll(" ", "")
        );

        this.mockMvc.perform(
                post("/register")
                        .flashAttr("person", personDTO)
        );

        verify(peopleService).save(notNull());
    }
}
