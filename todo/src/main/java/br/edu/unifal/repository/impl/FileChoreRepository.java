package br.edu.unifal.repository.impl;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.repository.ChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileChoreRepository implements ChoreRepository {

    private ObjectMapper mapper;
    public FileChoreRepository(){
        mapper = new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public List<Chore> load() {
        try{
            // Array.asList -> Gera uma lista IMUTÁVEL
            return new ArrayList<>(
                    Arrays.asList(
                            mapper.readValue(new File("chores.json"), Chore[].class))
            );
        } catch (MismatchedInputException exception){
            System.out.println("Unable to convert the content of the file into Chores!");

        } catch (IOException exception){
            System.out.println("ERROR: Unable to open file.");
        }
        return new ArrayList<>();
    }

    @Override
    public boolean saveAll(List<Chore> chores){
        try {
            mapper.writeValue(new File("chores.json"), chores);
            return true;
        } catch (IOException exception) {
            System.out.println("ERROR: Unable to write the chores on the file.");
        }
        return false;
    }

    // TODO
    @Override
    public boolean save(Chore chore) {
        throw new RuntimeException("Operation not supported yet.");
    }

    @Override
    public boolean update(Chore chore){
        throw new RuntimeException("Operation not supported yet.");
    }
}
