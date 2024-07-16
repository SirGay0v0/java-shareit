package ru.practicum.shareit.RESTEndpointsTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void testAddUser() throws Exception {
        User user = new User()
                .setId(1L)
                .setName("John")
                .setEmail("john@example.com");

        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User().setId(1L).setName("John Updated").setEmail("john.updated@example.com");
        when(userService.update(anyLong(), any(User.class))).thenReturn(user);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).delete(1L);
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User().setId(1L).setName("John").setEmail("john@example.com");
        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User().setId(1L).setName("John").setEmail("john@example.com");
        User user2 = new User().setId(2L).setName("Jane").setEmail("jane@example.com");
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
    }
}
