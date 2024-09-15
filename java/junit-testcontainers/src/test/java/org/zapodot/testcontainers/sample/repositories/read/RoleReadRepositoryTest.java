package org.zapodot.testcontainers.sample.repositories.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.zapodot.testcontainers.sample.model.Role;
import org.zapodot.testcontainers.sample.repositories.RolleTabell;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class RoleReadRepositoryTest {

    @Mock
    private NamedParameterJdbcOperations namedParameterJdbcOperations;

    @InjectMocks
    private RoleReadRepository roleReadRepository;

    @DisplayName("findById uten treff")
    @Test
    void findByIdWithoutResult() {
        when(namedParameterJdbcOperations.queryForObject(anyString(), anyMap(), isA(RowMapper.class))).thenThrow(new IncorrectResultSizeDataAccessException(0));
        assertThat(roleReadRepository.findById(Long.MAX_VALUE)).isNull();
        verify(namedParameterJdbcOperations).queryForObject(anyString(), anyMap(), isA(RowMapper.class));
    }

    @DisplayName("findById med treff")
    @Test
    void findByIdWithResult() {
        when(namedParameterJdbcOperations.queryForObject(anyString(), anyMap(), isA(RowMapper.class)))
                .thenReturn(new Role(1L, "Test"));
        assertThat(roleReadRepository.findById(1)).isNotNull();
        verify(namedParameterJdbcOperations).queryForObject(anyString(), anyMap(), isA(RowMapper.class));
    }

    @DisplayName("findByName uten treff")
    @Test
    void findByNameWithoutResult() {
        when(namedParameterJdbcOperations.queryForObject(anyString(), anyMap(), isA(RowMapper.class))).thenThrow(new IncorrectResultSizeDataAccessException(0));
        assertThat(roleReadRepository.findByName("Verdens beste rolle")).isNull();
        verify(namedParameterJdbcOperations).queryForObject(anyString(), anyMap(), isA(RowMapper.class));
    }

    @DisplayName("findByName med treff")
    @Test
    void findByNameWithResult() {
        when(namedParameterJdbcOperations.queryForObject(anyString(), anyMap(), isA(RowMapper.class)))
                .thenAnswer(invocation -> {
                    Map<String, String> arguments = invocation.getArgument(1, Map.class);
                    return new Role(1L, arguments.get(RolleTabell.NAVN));
                });
        final var rolleNavn = "Test";
        assertThat(roleReadRepository.findByName(rolleNavn)).satisfies(role -> {
            assertThat(role).isNotNull();
            assertThat(role.name()).isEqualTo(rolleNavn);
        });
        verify(namedParameterJdbcOperations).queryForObject(anyString(), anyMap(), isA(RowMapper.class));

    }

    @DisplayName("findAll med treff")
    @Test
    void findAll() {
        when(namedParameterJdbcOperations.query(anyString(), isA(RowMapper.class)))
                .thenReturn(List.of(new Role(1L, "Test"), new Role(2L, "Test2")));
        assertThat(roleReadRepository.findAll()).isNotEmpty();
        verify(namedParameterJdbcOperations).query(anyString(), isA(RowMapper.class));
    }

    @DisplayName("findAll uten treff")
    @Test
    void findAllWithoutResults() {
        when(namedParameterJdbcOperations.query(anyString(), isA(RowMapper.class)))
                .thenReturn(Collections.emptyList());
        assertThat(roleReadRepository.findAll()).isEmpty();
        verify(namedParameterJdbcOperations).query(anyString(), isA(RowMapper.class));
    }

    @Test
    void findByUserId() {
    }
}