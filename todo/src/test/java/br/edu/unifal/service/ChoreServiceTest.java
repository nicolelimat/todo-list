package br.edu.unifal.service;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.excepition.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ChoreServiceTest {

    private ChoreService service;

    @BeforeEach
    void setup(){
        service = new ChoreService();
    }

    @Test
    @DisplayName("#addChore > When the description is invalid > Throw an exception")
    void addChoreWhenTheDescriptionIsInvalidThrowAnException(){
        assertAll(
                () -> assertThrows(InvalidDescriptionException.class, // garante que chama a exceção
                        () -> service.addChore(null, null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", null)),
                () -> assertThrows(InvalidDescriptionException.class, // garante que chama a exceção
                        () -> service.addChore(null, LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class, // garante que chama a exceção
                        () -> service.addChore(null, LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the deadline is invalid > Throw an exception")
    void addChoreWhenTheDeadlineIsInvalidThrowAnException(){
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the chore already exists > Throw an exception")
    void addChoreWhenTheChoreAlreadyExistsThrowAnException(){
        service.addChore("Description", LocalDate.now());
        assertThrows(DuplicatedChoreException.class,
                () -> service.addChore("Description", LocalDate.now()));
    }

    @Test
    @DisplayName("#addChore > When the chore is successfully added > Return true")
    void addChoreWhenTheChoreIsSuccessfullyAddedReturnTrue(){
        Chore chore = service.addChore("Description", LocalDate.now());
        assertTrue(service.isChoreOnList(chore));
    }

    @Test
    @DisplayName("#addChore > When chores are different > Return true")
    void addChoreWhenChoresAreDifferentReturnTrue(){
        Chore chore1 = service.addChore("Description 1", LocalDate.now());
        Chore chore2 = service.addChore("Description 2", LocalDate.now());

        assertAll(
                () -> assertTrue(service.isChoreOnList(chore1)),
                () -> assertTrue(service.isChoreOnList(chore2))
        );

    }

    @Test
    @DisplayName("#deleteChore > When the list is empty > Throw an exception")
    void deleteChoreWhenTheListIsEmptyThrowAnException(){
        assertThrows(EmptyChoreListException.class,() -> {
            service.deleteChore("Description", LocalDate.now());
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore does not exist > Throw an exception")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreDoesNotExistThrowAnException() {
        service.addChore("Description", LocalDate.now());
        assertThrows(ChoreNotFoundException.class, () -> {
            service.deleteChore("Chore to be deleted", LocalDate.now().plusDays(2));
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore exists > Delete the chore")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreExistsDeleteTheChore() {
        service.addChore("Description", LocalDate.now().plusDays(5));
        assertEquals(1, service.getChores().size());

        assertDoesNotThrow(() -> service.deleteChore("Description", LocalDate.now().plusDays(5)));
        assertEquals(0, service.getChores().size());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidToggleTheChore(){
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the chore doesn't exist > Throw an exception")
    void toggleChoreWhenTheChoreDoesNotExistThrowAnException(){
        assertThrows(ChoreNotFoundException.class, () -> service.toggleChore("Chore #01", LocalDate.now()));
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is uncompleted > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusIsUncompletedToggleTheChore(){
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());
        service.getChores().get(0).setDeadline(LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > When toggle the chore twice > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidWhenToggleTheChoreTwiceToggleTheChore(){
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertFalse(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is completed > Throw and exception")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusIsCompletedToggleTheChore(){
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(1)));
        assertThrows(ToggleChoreWithInvalidDeadlineException.class,
                () -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
    }



}
