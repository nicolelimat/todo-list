package br.edu.unifal.service;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.excepition.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChoreService {

    private List<Chore> chores;

    public ChoreService(){
        chores = new ArrayList<>();
    }

    /**
     * Method to add a new chore
     * @param description The description of the chore
     * @param deadline The deadline to fulfill the chore
     * @return Chore The new (and uncompleted) chore
     * @throws InvalidDescriptionException When the description is null or empty
     * @throws InvalidDeadlineException When the deadline is null or empty
     * @throws DuplicatedChoreException When the given chore already exists
     */
    public Chore addChore(String description, LocalDate deadline){
        if (Objects.isNull(description) || description.isEmpty()){
            throw new InvalidDescriptionException("The description cannot be null or empty");
        }
        if(Objects.isNull(deadline) || deadline.isBefore(LocalDate.now())){
            throw new InvalidDeadlineException("The deadline cannot be null or before the current date");
        }
        for(Chore chore : chores){
            if (chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline)) {
                throw new DuplicatedChoreException("The given chore already exists.");
            }
        }

//        Using anyMatch solution
//
//        boolean choreExists = chores.stream().anyMatch(chore -> chore.getDescription().equals(description)
//        && chore.getDeadline().isEqual(deadline));
//
//        if(choreExists){
//            throw new DuplicatedChoreException("The given chore already exists.");
//        }

//        Using Getter and Setters

//        Chore chore = new Chore();
//        chore.setDescription(description);
//        chore.setDeadline(deadline);
//        chore.setIsCompleted(Boolean.FALSE);

//                 Using Lombok's builder
//
//         Chore chore = Chore.builder()
//                .description(description)
//                .deadline(deadline)
//                .isCompleted(false)
//                .build();

        // Using Constructor with all arguments
        Chore chore = new Chore(description, Boolean.FALSE, deadline);

        chores.add(chore);
        return chore;
    }

    /**
     * Method to verify if a chore is on the list.
     * @param chore The chore given
     * @return false if the chore is on the list, true if not
     */
    boolean isChoreOnList(Chore chore){
        for(Chore x : chores){
            if (chore.getDescription().equals(x.getDescription()) && chore.getDeadline().isEqual(x.getDeadline())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the added chores.
     *
     * @return List<Chore> The chores added until now.
     */
    public List<Chore> getChores() {
        return this.chores;
    }

    /**
     * Method to delete a given chore.
     * @param description The description of the chore
     * @param deadline The deadline of the chore
     */
    public void deleteChore(String description, LocalDate deadline) {
        if(isChoreListEmpty.test(this.chores)){
            throw new EmptyChoreListException("Unable to remove a chore from an empty list");
        }
//        Implemented in class, although `isChoreOnList` already exists
        boolean isChoreExist = this.chores.stream().anyMatch(
                (chore -> chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline)));
        if(!isChoreExist){
            throw new ChoreNotFoundException("The given chore does not exist");
        }

        this.chores = this.chores.stream()
                .filter(chore -> !chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline))
                .collect(Collectors.toList());
    }

    private final Predicate<List<Chore>> isChoreListEmpty = choreList -> choreList.isEmpty();

}


