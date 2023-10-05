package br.edu.unifal.service;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.enumerator.ChoreFilter;
import br.edu.unifal.excepition.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.LSOutput;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChoreServiceTest {

    private ChoreService service;

    @BeforeEach
    void setup() {
        service = new ChoreService();
    }

    @Test
    @DisplayName("#addChore > When the description is invalid > Throw an exception")
    void addChoreWhenTheDescriptionIsInvalidThrowAnException() {
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
    void addChoreWhenTheDeadlineIsInvalidThrowAnException() {
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the chore already exists > Throw an exception")
    void addChoreWhenTheChoreAlreadyExistsThrowAnException() {
        service.addChore("Description", LocalDate.now());
        assertThrows(DuplicatedChoreException.class,
                () -> service.addChore("Description", LocalDate.now()));
    }

//    @Test
//    @DisplayName("#addChore > When the chore is successfully added > Return true")
//    void addChoreWhenTheChoreIsSuccessfullyAddedReturnTrue() {
//        Chore chore = service.addChore("Description", LocalDate.now());
//        assertTrue(service.isChoreOnList(chore));
//    }

    @Test
    @DisplayName("#addChore > When the chore's list is empty > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListIsEmptyWhenAddingANewChoreAddTheChore() {
        Chore response = service.addChore("Description", LocalDate.now());
        assertAll(
                () -> assertEquals("Description", response.getDescription()),
                () -> assertEquals(LocalDate.now(), response.getDeadline()),
                () -> assertEquals(Boolean.FALSE, response.getIsCompleted())
        );
    }

    @Test
    @DisplayName("#addChore > When the chore's list has at least one element > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListHasAtLeastOneElementWhenAddingANewChoreAddTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        service.addChore("Chore #02", LocalDate.now().plusDays(2));
        assertAll(
                () -> assertEquals(2, service.getChores().size()),
                () -> assertEquals("Chore #01", service.getChores().get(0).getDescription()),
                () -> assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted()),
                () -> assertEquals("Chore #02", service.getChores().get(1).getDescription()),
                () -> assertEquals(LocalDate.now().plusDays(2), service.getChores().get(1).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(1).getIsCompleted())
        );
    }

//    @Test
//    @DisplayName("#addChore > When chores are different > Return true")
//    void addChoreWhenChoresAreDifferentReturnTrue() {
//        Chore chore1 = service.addChore("Description 1", LocalDate.now());
//        Chore chore2 = service.addChore("Description 2", LocalDate.now());
//
//        assertAll(
//                () -> assertTrue(service.isChoreOnList(chore1)),
//                () -> assertTrue(service.isChoreOnList(chore2))
//        );
//
//    }

    @Test
    @DisplayName("#deleteChore > When the list is empty > Throw an exception")
    void deleteChoreWhenTheListIsEmptyThrowAnException() {
        assertThrows(EmptyChoreListException.class, () -> {
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
    void toggleChoreWhenTheDeadlineIsValidToggleTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the chore doesn't exist > Throw an exception")
    void toggleChoreWhenTheChoreDoesNotExistThrowAnException() {
        assertThrows(ChoreNotFoundException.class, () -> service.toggleChore("Chore #01", LocalDate.now()));
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is uncompleted > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusIsUncompletedToggleTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());
        service.getChores().get(0).setDeadline(LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > When toggle the chore twice > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidWhenToggleTheChoreTwiceToggleTheChore() {
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertFalse(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is completed > Throw and exception")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusIsCompletedToggleTheChore() {
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(1)));
        assertThrows(ToggleChoreWithInvalidDeadlineException.class,
                () -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsAllWhenTheListIsEmptyReturnAllChores() {
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is not empty > Return all chores")
    void filterChoresWhenTheFilterIsAllWhenTheListIsNotEmptyReturnAllChores() {
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertAll(
                () -> assertEquals(2, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted()),
                () -> assertEquals("Chore #02", response.get(1).getDescription()),
                () -> assertEquals(Boolean.TRUE, response.get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsEmptyReturnAnEmptyList() {
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #02", response.get(0).getDescription()),
                () -> assertEquals(Boolean.TRUE, response.get(0).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsEmptyReturnAnEmptyList() {
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted())
        );

    }

    @Test
    @DisplayName("#displayChores > When the list is empty > Throw an exception")
    void displayChoresWhenTheListEmptyThrowAnException(){
        assertThrows(EmptyChoreListException.class, () -> service.displayChores());
    }

    @Test
    @DisplayName("#displayChores > When the list is not empty > When the status is TRUE > Display the chores")
    void displayChoresWhenTheListIsNotEmptyWhenTheStatusIsTrueDisplayChores(){
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(5)));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now().plusDays(9) ));
        service.getChores().add(new Chore("Chore #03", Boolean.TRUE, LocalDate.now() ));

        assertDoesNotThrow(() -> service.displayChores());
    }

    @Test
    @DisplayName("#displayChores > When the list is not empty > When the status is FALSE > Display the chores")
    void displayChoresWhenTheListIsNotEmptyWhenTheStatusIsFalseDisplayChores(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now().minusDays(5)));
        service.getChores().add(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(9) ));
        service.getChores().add(new Chore("Chore #03", Boolean.FALSE, LocalDate.now() ));

        assertDoesNotThrow(() -> service.displayChores());
    }

//    @Test
//    @DisplayName("displayChores > When the list is not empty > Display expected output")
//    void displayChoresWhenTheListIsNotEmptyDisplayExpectedOutput(){
//        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now()));
//        service.getChores().add(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(6) ));
//
//        assertDoesNotThrow(() -> service.displayChores());
//
//        String expectedOutput = "Descrição: \"Chore #01\" Deadline: 4/10/2023 Status: Completa\n" +
//                "Descrição: \"Chore #02\" Deadline: 10/10/2023 Status: Incompleta\n";
//
//    }

    @Test
    @DisplayName("#editChore > When the chore doesn't exist > Throw an exception")
    void editChoreWhenTheChoreDoesNotExistThrowAnException(){
        assertThrows(ChoreNotFoundException.class, () -> service.editChore("Chore to be edited", LocalDate.now(), "New description", LocalDate.now()));
    }

    @Test
    @DisplayName("#editChore > When trying to edit the status > Nothing happens")
    void editChoreWhenTryingToEditTheStatusNothingHappens(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertFalse(service.getChores().get(0).getIsCompleted());

        Chore response = service.editChore("Chore #01", LocalDate.now(), "Chore #01", LocalDate.now());
        assertFalse(response.getIsCompleted());

        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        assertTrue(service.getChores().get(0).getIsCompleted());

        Chore response2 = service.editChore("Chore #01", LocalDate.now(), "Chore #01", LocalDate.now());
        assertTrue(response2.getIsCompleted());
    }

    @Test
    @DisplayName("#editChore > When editing the description > Return the chore with the new description")
    void editChoreWhenEditingTheDescriptionReturnTheChoreWithTheNewDescription(){
        service.getChores().add(new Chore("Old description", Boolean.FALSE, LocalDate.now()));
        Chore response = service.editChore("Old description", LocalDate.now(), "New description", LocalDate.now());

        assertAll(
                () -> assertEquals("New description", response.getDescription()),
                () -> assertEquals(LocalDate.now(), response.getDeadline())
        );
    }

    @Test
    @DisplayName("#editChore > When editing the deadline > Return the chore with the new deadline")
    void editChoreWhenEditingTheDeadlineReturnTheChoreWithTheNewDeadline(){
        service.getChores().add(new Chore("Old description", Boolean.FALSE, LocalDate.now()));
        Chore response = service.editChore("Old description", LocalDate.now(), "Old description", LocalDate.now().plusDays(5));

        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(5), response.getDeadline()),
                () -> assertEquals("Old description", response.getDescription())
        );
    }

    @Test
    @DisplayName("#editChore > When editing the deadline and the description > Return the chore with the new deadline and description")
    void editChoreWhenEditingTheDeadlineAndTheDescriptionReturnTheChoreWithTheNewDeadlineAndDescription(){
        service.getChores().add(new Chore("Old description", Boolean.FALSE, LocalDate.now()));
        Chore response = service.editChore("Old description", LocalDate.now(), "New description", LocalDate.now().plusDays(5));

        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(5), response.getDeadline()),
                () -> assertEquals("New description", response.getDescription())
        );
    }

    @Test
    @DisplayName("#editChore > When chore edited > Update the chore in the list")
    void editChoreWhenChoreEditedUpdateChoreInTheList(){
        service.getChores().add(new Chore("Old description #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Old description #02", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Old description #03", Boolean.FALSE, LocalDate.now()));

        Chore response1 = service.editChore("Old description #01", LocalDate.now(), "New description", LocalDate.now().plusDays(5));
        Chore response2 = service.editChore("Old description #02", LocalDate.now(), "Old description #02", LocalDate.now().plusDays(5));
        Chore response3 = service.editChore("Old description #02", LocalDate.now(), "New description", LocalDate.now());

        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(5), response1.getDeadline()),
                () -> assertEquals("New description", response1.getDescription()),
                () -> assertEquals(3, service.getChores().size()),
                () -> assertEquals(response1.getDeadline(), service.getChores().get(0).getDeadline()),
                () -> assertEquals(response1.getDescription(), service.getChores().get(0).getDescription()),

                () -> assertEquals(LocalDate.now().plusDays(5), response2.getDeadline()),
                () -> assertEquals("Old description #02", response2.getDescription()),
                () -> assertEquals(3, service.getChores().size()),
                () -> assertEquals(response2.getDeadline(), service.getChores().get(1).getDeadline()),
                () -> assertEquals(response2.getDescription(), service.getChores().get(1).getDescription()),

                () -> assertEquals(LocalDate.now(), response3.getDeadline()),
                () -> assertEquals("New description", response3.getDescription()),
                () -> assertEquals(3, service.getChores().size()),
                () -> assertEquals(response3.getDeadline(), service.getChores().get(2).getDeadline()),
                () -> assertEquals(response3.getDescription(), service.getChores().get(2).getDescription())
        );

    }

    @Test
    @DisplayName("#editChore > When description and deadline are not altered > Chore remains unchanged")
    void editChoreWhenDescriptionAndDeadlineNotAlteredChoreRemainsUnchanged(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted());

        Chore response = service.editChore("Chore #01", LocalDate.now(), "Chore #01", LocalDate.now());
        assertEquals(Boolean.FALSE, response.getIsCompleted());
    }

    @Test
    @DisplayName("#editChore > When new description is invalid > Throw an Exception")
    void editChoreWhenNewDescriptionIsInvalidThrowAnException(){
        service.getChores().add(new Chore("Chore # 01", Boolean.FALSE, LocalDate.now()));
        assertAll(
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), null, null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(),"", null)),
                () -> assertThrows(InvalidDescriptionException.class, // garante que chama a exceção
                        () -> service.editChore("Chore # 01", LocalDate.now(), null, LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), "", LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class, // garante que chama a exceção
                        () -> service.editChore("Chore # 01", LocalDate.now(), null, LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.editChore("Chore # 01", LocalDate.now(), "", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#editChore > When new deadline is invalid > Throw an Exception")
    void editChoreWhenNewDeadlineIsInvalidThrowAnException(){
        service.getChores().add(new Chore("Description", Boolean.FALSE, LocalDate.now()));
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.editChore("Description", LocalDate.now(),"Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.editChore("Description", LocalDate.now(),"Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#editChore > When the new chore is equal to an existing chore > Throw an Exception")
    void editChoreWhenTheNewChoreIsEqualToAnExistingChore(){
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(2)));
        assertThrows(DuplicatedChoreException.class,
                () -> service.editChore("Chore #02", LocalDate.now().plusDays(2),"Chore #01",  LocalDate.now()));
    }

}
