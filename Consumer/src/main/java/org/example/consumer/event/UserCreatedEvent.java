package org.example.consumer.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEvent {
    private Long id;
    private String name;
    private LocalDate birthday;
    private String email;
}
