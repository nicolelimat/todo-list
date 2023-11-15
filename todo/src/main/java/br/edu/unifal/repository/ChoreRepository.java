package br.edu.unifal.repository;

import br.edu.unifal.domain.Chore;

import java.util.List;

public interface ChoreRepository {

    List<Chore> load();

    boolean saveAll(List<Chore> chores);

    boolean save(Chore chore);

    boolean update(Chore chore);

}
