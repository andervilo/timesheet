package br.com.andervilo.timesheet.application;

import br.com.andervilo.timesheet.application.command.EmployeCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployeUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployeDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import br.com.andervilo.timesheet.domain.Employe;
import br.com.andervilo.timesheet.infrastructure.repository.EmployeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private EmployeService employeService;

    private EmployeCreateCommand createCommand;
    private EmployeUpdateCommand updateCommand;
    private EmployeFilterQuery filterQuery;

    @BeforeEach
    void setUp() {
        // Setup test data
        createCommand = new EmployeCreateCommand("John Doe", "john@example.com", LocalDate.of(1990, 1, 15));
        updateCommand = new EmployeUpdateCommand("emp123", "John Updated", "john.updated@example.com", LocalDate.of(1990, 1, 20));
        
        filterQuery = new EmployeFilterQuery();
        filterQuery.setName("John");
        filterQuery.setEmail("john");
    }

    @Test
    @DisplayName("Should create an employee successfully")
    void shouldCreateEmployeeSuccessfully() {
        // Given
        when(employeRepository.save(any(Employe.class))).thenAnswer(invocation -> {
            Employe savedEmploye = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = savedEmploye.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedEmploye, "emp123");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return savedEmploye;
        });

        // When
        EmployeDTO result = employeService.create(createCommand);

        // Then
        assertNotNull(result);
        assertEquals("emp123", result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());
        assertEquals(LocalDate.of(1990, 1, 15), result.birthDate());
        
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    @DisplayName("Should update an employee successfully")
    void shouldUpdateEmployeeSuccessfully() {
        // Given
        Employe employe = Employe.of(updateCommand.name(), updateCommand.email(), updateCommand.birthDate());
        try {
            java.lang.reflect.Field idField = employe.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(employe, "emp123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        when(employeRepository.findById("emp123")).thenReturn(Optional.of(employe));
        when(employeRepository.save(any(Employe.class))).thenReturn(employe);

        // When
        EmployeDTO result = employeService.update("emp123", updateCommand);

        // Then
        assertNotNull(result);
        assertEquals("emp123", result.id());
        assertEquals("John Updated", result.name());
        assertEquals("john.updated@example.com", result.email());
        assertEquals(LocalDate.of(1990, 1, 20), result.birthDate());
        
        verify(employeRepository, times(1)).findById("emp123");
        verify(employeRepository, times(1)).save(any(Employe.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent employee")
    void shouldThrowExceptionWhenUpdatingNonExistentEmployee() {
        // Given
        when(employeRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> employeService.update("nonexistent", updateCommand));
        verify(employeRepository, times(1)).findById("nonexistent");
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    @DisplayName("Should delete an employee successfully")
    void shouldDeleteEmployeeSuccessfully() {
        // Given
        doNothing().when(employeRepository).deleteById("emp123");

        // When
        employeService.delete("emp123");

        // Then
        verify(employeRepository, times(1)).deleteById("emp123");
    }

    @Test
    @DisplayName("Should find employee by ID successfully")
    void shouldFindEmployeeByIdSuccessfully() {
        // Given
        Employe employe = Employe.of("John Doe", "john@example.com", LocalDate.of(1990, 1, 15));
        try {
            java.lang.reflect.Field idField = employe.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(employe, "emp123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(employeRepository.findById("emp123")).thenReturn(Optional.of(employe));

        // When
        EmployeDTO result = employeService.findById("emp123");

        // Then
        assertNotNull(result);
        assertEquals("emp123", result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());
        assertEquals(LocalDate.of(1990, 1, 15), result.birthDate());
        
        verify(employeRepository, times(1)).findById("emp123");
    }

    @Test
    @DisplayName("Should throw exception when finding non-existent employee")
    void shouldThrowExceptionWhenFindingNonExistentEmployee() {
        // Given
        when(employeRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> employeService.findById("nonexistent"));
        verify(employeRepository, times(1)).findById("nonexistent");
    }

    @Test
    @DisplayName("Should find all employees successfully")
    void shouldFindAllEmployeesSuccessfully() {
        // Given
        Employe employe = Employe.of("John Doe", "john@example.com", LocalDate.of(1990, 1, 15));
        try {
            java.lang.reflect.Field idField = employe.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(employe, "emp123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Employe> employees = List.of(employe);
        when(employeRepository.findAll()).thenReturn(employees);

        // When
        List<EmployeDTO> result = employeService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("emp123", result.get(0).id());
        assertEquals("John Doe", result.get(0).name());
        assertEquals("john@example.com", result.get(0).email());
        assertEquals(LocalDate.of(1990, 1, 15), result.get(0).birthDate());
        
        verify(employeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find employees with filters successfully")
    void shouldFindEmployeesWithFiltersSuccessfully() {
        // Given
        Employe employe = Employe.of("John Doe", "john@example.com", LocalDate.of(1990, 1, 15));
        try {
            java.lang.reflect.Field idField = employe.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(employe, "emp123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Employe> employees = List.of(employe);
        Page<Employe> employeePage = new PageImpl<>(employees);
        
        when(employeRepository.findWithFilters(any(EmployeFilterQuery.class), any(Pageable.class))).thenReturn(employeePage);

        // When
        PageDTO<EmployeDTO> result = employeService.findWithFilters(filterQuery);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("emp123", result.content().get(0).id());
        assertEquals("John Doe", result.content().get(0).name());
        assertEquals("john@example.com", result.content().get(0).email());
        assertEquals(LocalDate.of(1990, 1, 15), result.content().get(0).birthDate());
        assertEquals(1, result.totalElements());
        
        verify(employeRepository, times(1)).findWithFilters(any(EmployeFilterQuery.class), any(Pageable.class));
    }
}
