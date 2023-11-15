package br.edu.unifal.domain;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chore {

    private Long id;

    private String description;

    private Boolean isCompleted;

    private LocalDate deadline;

    public Chore(String description, Boolean isCompleted, LocalDate deadline) {
        this.description =  description;
        this.isCompleted = isCompleted;
        this.deadline = deadline;
    }
}
