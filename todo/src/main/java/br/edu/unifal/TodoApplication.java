package br.edu.unifal;

import br.edu.unifal.repository.ChoreRepository;
import br.edu.unifal.repository.impl.FileChoreRepository;
import br.edu.unifal.repository.impl.MySQLChoreRepository;
import br.edu.unifal.service.ChoreService;

import java.time.LocalDate;

public class TodoApplication {
    public static void main(String[] args) {
////        ChoreRepository repository = new FileChoreRepository();
        ChoreRepository repository = new MySQLChoreRepository();
        ChoreService service = new ChoreService(repository); // injetar dependencia
        service.loadChores();
//        service.addChore("Testing write on databse feature", LocalDate.now());
//        service.addChore("Feriado", LocalDate.now().plusDays(1));
//        service.saveChores();
////        service.addChore("Testing ", LocalDate.now());
        System.out.println("Tamanho da lista de chores: " + service.getChores().size());
//        service.saveChores();
        service.printChores();
        System.out.println("\nUpdate chore: " + service.updateChore(service.getChores().get(0)));

    }
}
