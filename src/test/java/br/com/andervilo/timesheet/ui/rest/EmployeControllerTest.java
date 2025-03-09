package br.com.andervilo.timesheet.ui.rest;

import br.com.andervilo.timesheet.application.EmployeService;
import br.com.andervilo.timesheet.application.command.EmployeCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployeUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployeDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployeFilterQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeControllerTest {

    // Exception handler for tests
    @ControllerAdvice
    static class TestControllerAdvice {
        @ExceptionHandler(NoSuchElementException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private MockMvc mockMvc;

    @Mock
    private EmployeService employeService;

    @InjectMocks
    private EmployeController employeController;

    private ObjectMapper objectMapper;
    private EmployeDTO employeDTO;
    private EmployeCreateCommand createCommand;
    private EmployeUpdateCommand updateCommand;
    private EmployeFilterQuery filterQuery;
    private String employeId = "emp123";

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper for proper date serialization
        objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new com.fasterxml.jackson.databind.util.StdDateFormat());
        
        // Create a message converter with our configured ObjectMapper
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        
        // Setup MockMvc with the custom converter
        mockMvc = MockMvcBuilders
            .standaloneSetup(employeController)
            .setControllerAdvice(new TestControllerAdvice())
            .setMessageConverters(converter)
            .build();
        
        // Setup test data
        employeDTO = new EmployeDTO(employeId, "John Doe", "john@example.com", LocalDate.of(1990, 1, 15));
        createCommand = new EmployeCreateCommand("John Doe", "john@example.com", LocalDate.of(1990, 1, 15));
        updateCommand = new EmployeUpdateCommand(null, "John Updated", "john.updated@example.com", LocalDate.of(1990, 1, 20));
        
        filterQuery = new EmployeFilterQuery();
        filterQuery.setName("John");
        filterQuery.setEmail("john");
    }

    @Test
    @DisplayName("Should create an employee successfully")
    void shouldCreateEmployeeSuccessfully() throws Exception {
        // Given
        when(employeService.create(any(EmployeCreateCommand.class))).thenReturn(employeDTO);

        // When & Then
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeId)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.birthDate", is("1990-01-15")));
        
        verify(employeService, times(1)).create(any(EmployeCreateCommand.class));
    }

    @Test
    @DisplayName("Should update an employee successfully")
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        // Given
        EmployeDTO updatedDTO = new EmployeDTO(employeId, "John Updated", "john.updated@example.com", LocalDate.of(1990, 1, 20));
        when(employeService.update(eq(employeId), any(EmployeUpdateCommand.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/employees/" + employeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeId)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.birthDate", is("1990-01-20")));
        
        verify(employeService, times(1)).update(eq(employeId), any(EmployeUpdateCommand.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent employee")
    void shouldReturn404WhenUpdatingNonExistentEmployee() throws Exception {
        // Given
        when(employeService.update(eq("nonexistent"), any(EmployeUpdateCommand.class)))
                .thenThrow(new NoSuchElementException("Employee not found"));

        // When & Then
        mockMvc.perform(put("/api/employees/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isNotFound());
        
        verify(employeService, times(1)).update(eq("nonexistent"), any(EmployeUpdateCommand.class));
    }

    @Test
    @DisplayName("Should delete an employee successfully")
    void shouldDeleteEmployeeSuccessfully() throws Exception {
        // Given
        doNothing().when(employeService).delete(employeId);

        // When & Then
        mockMvc.perform(delete("/api/employees/" + employeId))
                .andExpect(status().isNoContent());
        
        verify(employeService, times(1)).delete(employeId);
    }

    @Test
    @DisplayName("Should find employee by ID successfully")
    void shouldFindEmployeeByIdSuccessfully() throws Exception {
        // Given
        when(employeService.findById(employeId)).thenReturn(employeDTO);

        // When & Then
        mockMvc.perform(get("/api/employees/" + employeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeId)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.birthDate", is("1990-01-15")));
        
        verify(employeService, times(1)).findById(employeId);
    }

    @Test
    @DisplayName("Should return 404 when finding non-existent employee")
    void shouldReturn404WhenFindingNonExistentEmployee() throws Exception {
        // Given
        when(employeService.findById("nonexistent")).thenThrow(new NoSuchElementException("Employee not found"));

        // When & Then
        mockMvc.perform(get("/api/employees/nonexistent"))
                .andExpect(status().isNotFound());
        
        verify(employeService, times(1)).findById("nonexistent");
    }

    @Test
    @DisplayName("Should find all employees successfully")
    void shouldFindAllEmployeesSuccessfully() throws Exception {
        // Given
        List<EmployeDTO> employees = List.of(employeDTO);
        when(employeService.findAll()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(employeId)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[0].birthDate", is("1990-01-15")));
        
        verify(employeService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should filter employees successfully")
    void shouldFilterEmployeesSuccessfully() throws Exception {
        // Given
        List<EmployeDTO> employees = List.of(employeDTO);
        PageDTO<EmployeDTO> pageDTO = new PageDTO<>(
            employees, 1, 1, 0, 10, true, true
        );
        
        when(employeService.findWithFilters(any(EmployeFilterQuery.class))).thenReturn(pageDTO);

        // When & Then
        mockMvc.perform(post("/api/employees/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterQuery)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(employeId)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.content[0].email", is("john@example.com")))
                .andExpect(jsonPath("$.content[0].birthDate", is("1990-01-15")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.pageSize", is(10)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));
        
        verify(employeService, times(1)).findWithFilters(any(EmployeFilterQuery.class));
    }
}
