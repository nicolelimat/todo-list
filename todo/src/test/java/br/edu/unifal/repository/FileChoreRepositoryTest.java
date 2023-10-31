package br.edu.unifal.repository;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.repository.impl.FileChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileChoreRepositoryTest {

    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#load > When the file is found > When the content is empty > Return empty list")
    void loadWhenTheFileIsFoundWhenTheContentIsEmptyReturnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class) // o que tem o mock em cima
        ).thenThrow(MismatchedInputException.class);
        List<Chore> response = repository.load();

        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is not found (or path is invalid) > Return empty list")
    void loadWhenTheFileIsNotFoundOrPathIsInvalidReturnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(FileNotFoundException.class);

        List<Chore> response = repository.load();
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is loaded > Return a chore's list")
    void loadWhenTheFileIsLoadedReturnAChoresList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenReturn(new Chore[] {
                new Chore("First Chore", Boolean.FALSE, LocalDate.now()),
                new Chore("Second Chore", Boolean.TRUE, LocalDate.now().minusDays(5))
        });

        List<Chore> chores = repository.load();
        assertAll(
                () -> assertEquals(2, chores.size()),
                () -> assertEquals("First Chore", chores.get(0).getDescription()),
                () -> assertEquals(LocalDate.now().minusDays(5), chores.get(1).getDeadline())
        );

    }

    @Test
    @DisplayName("#save > When unable to write the chores on the file > Return false")
    void saveWhenUnableToWriteTheChoresOnTheFileReturnFalse() throws IOException {
        // mock do ObjectMapper lança uma 'IOException' ao escrever no arquivo
        Mockito.doThrow(IOException.class)
                .when(mapper).writeValue(Mockito.any(File.class), Mockito.any());

        List<Chore> chores = new ArrayList<>();
        boolean response = repository.save(chores);

        assertFalse(response);
    }

    @Test
    @DisplayName("#save > When able to write the chores on the file > Return true")
    void saveWhenAbleToWriteTheChoresOnTheFileReturnTrue() throws IOException {
        // mock do ObjectMapper retorna 'true' quando writeValue é chamado
        Mockito.doNothing().when(mapper).writeValue(Mockito.any(File.class), Mockito.any());

        List<Chore> chores = new ArrayList<>();
        boolean response = repository.save(chores);

        assertTrue(response);
    }





}
