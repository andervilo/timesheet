package br.com.andervilo.timesheet.application;

import br.com.andervilo.timesheet.application.command.EmployerCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployerUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployerDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
import br.com.andervilo.timesheet.domain.Employer;
import br.com.andervilo.timesheet.infrastructure.repository.EmployerRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerService employerService;

    private Employer employer;
    private EmployerCreateCommand createCommand;
    private EmployerUpdateCommand updateCommand;
    private EmployerFilterQuery filterQuery;
    private String employerId = "emp123";

    @BeforeEach
    void setUp() {
        // Setup test data
        employer = Employer.of("Acme Inc", "12345678901234", "123 Main St", "555-1234", "contact@acme.com");
        // Use reflection to set ID since it's a private field with no setter
        try {
            java.lang.reflect.Field idField = employer.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(employer, employerId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createCommand = new EmployerCreateCommand("Acme Inc", "12345678901234", "123 Main St", "555-1234", "contact@acme.com");
        updateCommand = new EmployerUpdateCommand("Acme Updated", "98765432109876", "456 New St", "555-5678", "new@acme.com");
        
        filterQuery = new EmployerFilterQuery();
        filterQuery.setName("Acme");
        filterQuery.setCnpj("123");
    }

    @Test
    @DisplayName("Should create an employer successfully")
    void shouldCreateEmployerSuccessfully() {
        // Given
        when(employerRepository.save(any(Employer.class))).thenAnswer(invocation -> {
            Employer savedEmployer = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = savedEmployer.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedEmployer, employerId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return savedEmployer;
        });

        // When
        EmployerDTO result = employerService.create(createCommand);

        // Then
        assertNotNull(result);
        assertEquals(employerId, result.id());
        assertEquals("Acme Inc", result.name());
        assertEquals("12345678901234", result.cnpj());
        assertEquals("123 Main St", result.address());
        assertEquals("555-1234", result.phone());
        assertEquals("contact@acme.com", result.email());
        
        verify(employerRepository, times(1)).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should update an employer successfully")
    void shouldUpdateEmployerSuccessfully() {
        // Given
        Employer updatedEmployer = Employer.of(
            updateCommand.name(), 
            updateCommand.cnpj(), 
            updateCommand.address(), 
            updateCommand.phone(), 
            updateCommand.email()
        );
        try {
            java.lang.reflect.Field idField = updatedEmployer.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedEmployer, employerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employer));
        when(employerRepository.save(any(Employer.class))).thenReturn(updatedEmployer);

        // When
        EmployerDTO result = employerService.update(employerId, updateCommand);

        // Then
        assertNotNull(result);
        assertEquals(employerId, result.id());
        assertEquals("Acme Updated", result.name());
        assertEquals("98765432109876", result.cnpj());
        assertEquals("456 New St", result.address());
        assertEquals("555-5678", result.phone());
        assertEquals("new@acme.com", result.email());
        
        verify(employerRepository, times(1)).findById(employerId);
        verify(employerRepository, times(1)).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent employer")
    void shouldThrowExceptionWhenUpdatingNonExistentEmployer() {
        // Given
        when(employerRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> employerService.update("nonexistent", updateCommand));
        verify(employerRepository, times(1)).findById("nonexistent");
        verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should delete an employer successfully")
    void shouldDeleteEmployerSuccessfully() {
        // Given
        doNothing().when(employerRepository).deleteById(employerId);

        // When
        employerService.delete(employerId);

        // Then
        verify(employerRepository, times(1)).deleteById(employerId);
    }

    @Test
    @DisplayName("Should find employer by ID successfully")
    void shouldFindEmployerByIdSuccessfully() {
        // Given
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employer));

        // When
        EmployerDTO result = employerService.findById(employerId);

        // Then
        assertNotNull(result);
        assertEquals(employerId, result.id());
        assertEquals("Acme Inc", result.name());
        assertEquals("12345678901234", result.cnpj());
        assertEquals("123 Main St", result.address());
        assertEquals("555-1234", result.phone());
        assertEquals("contact@acme.com", result.email());
        
        verify(employerRepository, times(1)).findById(employerId);
    }

    @Test
    @DisplayName("Should throw exception when finding non-existent employer")
    void shouldThrowExceptionWhenFindingNonExistentEmployer() {
        // Given
        when(employerRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> employerService.findById("nonexistent"));
        verify(employerRepository, times(1)).findById("nonexistent");
    }

    @Test
    @DisplayName("Should find all employers successfully")
    void shouldFindAllEmployersSuccessfully() {
        // Given
        List<Employer> employers = List.of(employer);
        when(employerRepository.findAll()).thenReturn(employers);

        // When
        List<EmployerDTO> result = employerService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employerId, result.get(0).id());
        assertEquals("Acme Inc", result.get(0).name());
        assertEquals("12345678901234", result.get(0).cnpj());
        assertEquals("123 Main St", result.get(0).address());
        assertEquals("555-1234", result.get(0).phone());
        assertEquals("contact@acme.com", result.get(0).email());
        
        verify(employerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find employers with filters successfully")
    void shouldFindEmployersWithFiltersSuccessfully() {
        // Given
        List<Employer> employers = List.of(employer);
        Page<Employer> employerPage = new PageImpl<>(employers);
        
        when(employerRepository.findWithFilters(any(EmployerFilterQuery.class), any(Pageable.class))).thenReturn(employerPage);

        // When
        PageDTO<EmployerDTO> result = employerService.findWithFilters(filterQuery);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(employerId, result.content().get(0).id());
        assertEquals("Acme Inc", result.content().get(0).name());
        assertEquals("12345678901234", result.content().get(0).cnpj());
        assertEquals("123 Main St", result.content().get(0).address());
        assertEquals("555-1234", result.content().get(0).phone());
        assertEquals("contact@acme.com", result.content().get(0).email());
        assertEquals(1, result.totalElements());
        
        verify(employerRepository, times(1)).findWithFilters(any(EmployerFilterQuery.class), any(Pageable.class));
    }
}
