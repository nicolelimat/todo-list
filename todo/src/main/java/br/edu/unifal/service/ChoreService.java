package br.edu.unifal.service;

import br.edu.unifal.domain.Chore;
import br.edu.unifal.enumerator.ChoreFilter;
import br.edu.unifal.excepition.*;
import br.edu.unifal.repository.ChoreRepository;
import br.edu.unifal.repository.impl.FileChoreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChoreService {
    private List<Chore> chores;
    private ChoreRepository repository;

    public ChoreService(ChoreRepository repository){
        chores = new ArrayList<>();
        this.repository = repository;
    }

    public ChoreService(){
        chores = new ArrayList<>();
        repository = new FileChoreRepository();
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

        Chore chore = new Chore(description, Boolean.FALSE, deadline);

        repository.save(chore);
        chores.add(chore);
        return chore;
    }

    /**
     * Method to get the added chores.
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
        if(!(isChoreExist.test(description,deadline))){
            throw new ChoreNotFoundException("The given chore does not exist");
        }
        this.chores = this.chores.stream()
                .filter(chore -> !chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline))
                .collect(Collectors.toList());
    }

    /**
     * Method to toggle a chore from completed to uncompleted and vice-versa
     * @param description The chore's description
     * @param deadline The deadline to complete the chore
     * @throws ChoreNotFoundException When the chore is not found on the list
     */
    public void toggleChore(String description, LocalDate deadline){
        if(!(isChoreExist.test(description,deadline))){
            throw new ChoreNotFoundException("Chore not found. Impossible to toggle!");
        }
        this.chores.stream().map(chore-> {
            if(!chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline)){
                return chore;
            }
            if(chore.getDeadline().isBefore(LocalDate.now()) && chore.getIsCompleted()){
                throw new ToggleChoreWithInvalidDeadlineException("Unable to toggle a completed chore with a past deadline");
            }
            chore.setIsCompleted(!chore.getIsCompleted());
            return chore;
        }).collect(Collectors.toList());
    }

    /**
     * Method to filter the list of chores
     * @param filter The condition to filter the chore
     * @return The list of chores filtered
     */
    public List<Chore> filterChores(ChoreFilter filter) {
        switch (filter){
            case COMPLETED:
                return this.chores.stream().filter(Chore::getIsCompleted).collect(Collectors.toList());
            case UNCOMPLETED:
                return this.chores.stream().filter(chore -> !chore.getIsCompleted()).collect(Collectors.toList());
            case ALL:
            default:
                return this.chores;
        }
    }

    /**
     * Method to print the list of chores
     * @throws EmptyChoreListException When the list is empty
     */
    public void printChores (){
        if (isChoreListEmpty.test(this.chores)){
            throw new EmptyChoreListException("Unable to display chores of an empty list");
        }
        this.chores.stream().forEach(chore ->
                System.out.println(
                        "ID: " + chore.getId() +
                        " - Descrição: \"" + chore.getDescription() + "\"" + " Deadline: " +
                        chore.getDeadline().getDayOfMonth() + "/" +
                        chore.getDeadline().getMonthValue() +"/"+
                        chore.getDeadline().getYear() + " Status: " +
                        (chore.getIsCompleted() ? "Completa" : "Incompleta"))
        );
    }

    /**
     * Method to edit a chore's description and deadline
     * @param oldDescription The description of the chore to be edited
     * @param oldDeadline The deadline of the chore to be edited
     * @param newDescription The new description of the chore to be edited
     * @param newDeadline The new deadline of the chore to be edited
     * @throws EmptyChoreListException When the list is empty
     * @throws ChoreNotFoundException When the chore doesn't exist
     * @throws DuplicatedChoreException When the new chore is equal to another existing chore
     * @throws InvalidDescriptionException When the new description is invalid
     * @throws InvalidDeadlineException When the new deadline is invalid
     */
    public void editChore(String oldDescription, LocalDate oldDeadline, String newDescription, LocalDate newDeadline) {
        if(isChoreListEmpty.test(this.chores)){
            throw new EmptyChoreListException("Unable to edit a chore from an empty list");
        }
        if(!(isChoreExist.test(oldDescription, oldDeadline))){
            throw new ChoreNotFoundException("Unable to edit a chore that does not exist");
        }
        if((isChoreExist.test(newDescription, newDeadline) && oldDeadline != newDeadline && oldDescription != newDescription)){
            throw new DuplicatedChoreException("Unable to edit a chore to a chore that already exists");
        }
        if(Objects.isNull(newDescription) || newDescription.isEmpty()){
            throw new InvalidDescriptionException("Unable to edit a chore to a description that is null or empty");
        }
        if(Objects.isNull(newDeadline) || newDeadline.isBefore(LocalDate.now())){
            throw new InvalidDeadlineException("Unable to edit a chore to a deadline that is null or before the current date");
        }
        this.chores.stream().map(chore -> {
            if(!chore.getDescription().equals(oldDescription) && !chore.getDeadline().isEqual(oldDeadline)){
                return chore;
            }
            chore.setDeadline(newDeadline);
            chore.setDescription(newDescription);
            return chore;
        }).collect(Collectors.toList());
    }

    /**
     */
    public void loadChores() {
        this.chores = repository.load();
    }

    /**
     * Method to save the chores into the file
     * @return TRUE, if the saved was completed and <br/>
     *         FALSE, if the save fails
     */
    public Boolean saveChores(){
        return repository.saveAll(this.chores);
    }

    /**
     * Method to update the chore
     * @param chore
     * @return
     */
    public Boolean updateChore(Chore chore){
        if(Objects.isNull(chore)){
            return Boolean.FALSE;
        }
        return repository.update(chore);
    }

    private final Predicate<List<Chore>> isChoreListEmpty = choreList -> choreList.isEmpty();

    private final BiPredicate<String, LocalDate> isChoreExist = (description, deadline) ->
            this.chores.stream().anyMatch(choreFound ->
                    choreFound.getDescription().equals(description) &&
                    choreFound.getDeadline().equals(deadline));
}


