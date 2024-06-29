package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "email")
    @NotBlank
    @Pattern(regexp = "^.+@.+\\..+")
    String email;
}
