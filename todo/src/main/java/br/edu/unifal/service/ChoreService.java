package br.edu.unifal.service;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.excepition.DuplicatedChoreException;
import br.edu.unifal.excepition.InvalidDeadlineException;
import br.edu.unifal.excepition.InvalidDescriptionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChoreService {

    private List<Chore> chores;

    public ChoreService(){
        chores = new ArrayList<>();
    }

    public Chore addChore(String description, LocalDate deadline){
        if (Objects.isNull(description) || description.isEmpty()){
            throw new InvalidDescriptionException("The description cannot be null or empty");
        }
        if(Objects.isNull(deadline) || deadline.isBefore(LocalDate.now())){
            throw new InvalidDeadlineException("The deadline cannot be null or before the current date");
        }

//        boolean choreExists = chores.stream().anyMatch(chore -> chore.getDescription().equals(description)
//        && chore.getDeadline().isEqual(deadline));
//
//        if(choreExists){
//            throw new DuplicatedChoreException("The given chore already exists.");
//        }

        for(Chore chore : chores){
            if (chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline)) {
                throw new DuplicatedChoreException("The given chore already exists.");
            }
        }

//        Chore chore = new Chore();
//        chore.setDescription(description);
//        chore.setDeadline(deadline);
//        chore.setIsCompleted(Boolean.FALSE);

        Chore chore = new Chore(description, Boolean.FALSE, deadline);
        chores.add(chore);

        return chore;
    }

    boolean isChoreOnList(Chore chore){
        for(Chore x : chores){
            if (chore.getDescription().equals(x.getDescription()) && chore.getDeadline().isEqual(x.getDeadline())) {
                return true;
            }
        }
        return false;
    }
}
