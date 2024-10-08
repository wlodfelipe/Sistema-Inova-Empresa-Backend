package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserService userService;

    List<UserEntity> lista = new ArrayList<>();

    @BeforeEach
    public void setup() {
        //findAll
        this.lista.add(new UserEntity(UUID.randomUUID(), "teste", "teste@gmail.com", "senha", Role.COLABORADOR, null, null, null,null));
        this.lista.add(new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha2", Role.AVALIADOR, null, null, null,null));
        this.lista.add(new UserEntity(UUID.randomUUID(), "teste3", "teste3@gmail.com", "senha3", Role.ADMIN, null, null, null,null));
        when(userRepository.findAll()).thenReturn(lista);
    }

    @Test
    void alterarUsuario() {
        UserEntity usuario = new UserEntity(UUID.randomUUID(), "teste", "teste@gmail.com", "senha", Role.COLABORADOR, null, null, null,null);
        Role novaRole = Role.ADMIN;

        when(userRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        String resultado = userService.alterarUsuario(novaRole, usuario.getId());

        assertEquals("Usuario alterado para ADMIN", resultado);
        assertEquals(novaRole, usuario.getRole());
        verify(userRepository).save(usuario);
    }

    @Test
    void testAlterarUsuarioNaoEncontrado() {
        UUID userId = UUID.randomUUID();
        Role novaRole = Role.ADMIN;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.alterarUsuario(novaRole, userId);
        });

        assertEquals("Erro ao alterar usuario: Usuario nao encontrado", exception.getMessage());
    }

    @Test
    void testAlterarUsuarioComRoleInvalida() {
        UUID userId = UUID.randomUUID();
        Role roleInvalida = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.alterarUsuario(roleInvalida, userId);
        });

        assertEquals("Erro ao alterar usuario: Role nao pode ser nula", exception.getMessage());
    }


    @Test
    void findAll() {
        var retorno = userService.findAll();

        assertEquals(3, retorno.size());
        assertEquals(Role.ADMIN, retorno.get(2).getRole());
    }

    @Test
    void testFindAllComErro() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Erro no banco de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findAll();
        });

        assertEquals("erro ao listar usuarios Erro no banco de dados", exception.getMessage());
    }
}