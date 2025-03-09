package br.com.andervilo.timesheet.ui.rest;

import br.com.andervilo.timesheet.application.EmployerService;
import br.com.andervilo.timesheet.application.command.EmployerCreateCommand;
import br.com.andervilo.timesheet.application.command.EmployerUpdateCommand;
import br.com.andervilo.timesheet.application.dto.EmployerDTO;
import br.com.andervilo.timesheet.application.dto.PageDTO;
import br.com.andervilo.timesheet.application.query.EmployerFilterQuery;
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

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployerControllerTest {

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
    private EmployerService employerService;

    @InjectMocks
    private EmployerController employerController;

    private ObjectMapper objectMapper;
    private EmployerDTO employerDTO;
    private EmployerCreateCommand createCommand;
    private EmployerUpdateCommand updateCommand;
    private EmployerFilterQuery filterQuery;
    private String employerId = "emp123";

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
            .standaloneSetup(employerController)
            .setControllerAdvice(new TestControllerAdvice())
            .setMessageConverters(converter)
            .build();
        
        // Setup test data
        employerDTO = new EmployerDTO(employerId, "Acme Inc", "12345678901234", "123 Main St", "555-1234", "contact@acme.com");
        createCommand = new EmployerCreateCommand("Acme Inc", "12345678901234", "123 Main St", "555-1234", "contact@acme.com");
        updateCommand = new EmployerUpdateCommand("Acme Updated", "12345678901234", "456 Oak St", "555-5678", "updated@acme.com");
        
        filterQuery = new EmployerFilterQuery();
        filterQuery.setName("Acme");
        filterQuery.setCnpj("123");
    }

    @Test
    @DisplayName("Should create an employer successfully")
    void shouldCreateEmployerSuccessfully() throws Exception {
        // Given
        when(employerService.create(any(EmployerCreateCommand.class))).thenReturn(employerDTO);

        // When & Then
        mockMvc.perform(post("/api/employers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employerId)))
                .andExpect(jsonPath("$.name", is("Acme Inc")))
                .andExpect(jsonPath("$.cnpj", is("12345678901234")))
                .andExpect(jsonPath("$.address", is("123 Main St")))
                .andExpect(jsonPath("$.phone", is("555-1234")))
                .andExpect(jsonPath("$.email", is("contact@acme.com")));
        
        verify(employerService, times(1)).create(any(EmployerCreateCommand.class));
    }

    @Test
    @DisplayName("Should update an employer successfully")
    void shouldUpdateEmployerSuccessfully() throws Exception {
        // Given
        EmployerDTO updatedDTO = new EmployerDTO(employerId, "Acme Updated", "12345678901234", "456 Oak St", "555-5678", "updated@acme.com");
        when(employerService.update(eq(employerId), any(EmployerUpdateCommand.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/employers/" + employerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employerId)))
                .andExpect(jsonPath("$.name", is("Acme Updated")))
                .andExpect(jsonPath("$.cnpj", is("12345678901234")))
                .andExpect(jsonPath("$.address", is("456 Oak St")))
                .andExpect(jsonPath("$.phone", is("555-5678")))
                .andExpect(jsonPath("$.email", is("updated@acme.com")));
        
        verify(employerService, times(1)).update(eq(employerId), any(EmployerUpdateCommand.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent employer")
    void shouldReturn404WhenUpdatingNonExistentEmployer() throws Exception {
        // Given
        when(employerService.update(eq("nonexistent"), any(EmployerUpdateCommand.class)))
                .thenThrow(new NoSuchElementException("Employer not found"));

        // When & Then
        mockMvc.perform(put("/api/employers/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isNotFound());
        
        verify(employerService, times(1)).update(eq("nonexistent"), any(EmployerUpdateCommand.class));
    }

    @Test
    @DisplayName("Should delete an employer successfully")
    void shouldDeleteEmployerSuccessfully() throws Exception {
        // Given
        doNothing().when(employerService).delete(employerId);

        // When & Then
        mockMvc.perform(delete("/api/employers/" + employerId))
                .andExpect(status().isNoContent());
        
        verify(employerService, times(1)).delete(employerId);
    }

    @Test
    @DisplayName("Should find employer by ID successfully")
    void shouldFindEmployerByIdSuccessfully() throws Exception {
        // Given
        when(employerService.findById(employerId)).thenReturn(employerDTO);

        // When & Then
        mockMvc.perform(get("/api/employers/" + employerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employerId)))
                .andExpect(jsonPath("$.name", is("Acme Inc")))
                .andExpect(jsonPath("$.cnpj", is("12345678901234")))
                .andExpect(jsonPath("$.address", is("123 Main St")))
                .andExpect(jsonPath("$.phone", is("555-1234")))
                .andExpect(jsonPath("$.email", is("contact@acme.com")));
        
        verify(employerService, times(1)).findById(employerId);
    }

    @Test
    @DisplayName("Should return 404 when finding non-existent employer")
    void shouldReturn404WhenFindingNonExistentEmployer() throws Exception {
        // Given
        when(employerService.findById("nonexistent")).thenThrow(new NoSuchElementException("Employer not found"));

        // When & Then
        mockMvc.perform(get("/api/employers/nonexistent"))
                .andExpect(status().isNotFound());
        
        verify(employerService, times(1)).findById("nonexistent");
    }

    @Test
    @DisplayName("Should find all employers successfully")
    void shouldFindAllEmployersSuccessfully() throws Exception {
        // Given
        List<EmployerDTO> employers = List.of(employerDTO);
        when(employerService.findAll()).thenReturn(employers);

        // When & Then
        mockMvc.perform(get("/api/employers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(employerId)))
                .andExpect(jsonPath("$[0].name", is("Acme Inc")))
                .andExpect(jsonPath("$[0].cnpj", is("12345678901234")))
                .andExpect(jsonPath("$[0].address", is("123 Main St")))
                .andExpect(jsonPath("$[0].phone", is("555-1234")))
                .andExpect(jsonPath("$[0].email", is("contact@acme.com")));
        
        verify(employerService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should filter employers successfully")
    void shouldFilterEmployersSuccessfully() throws Exception {
        // Given
        List<EmployerDTO> employers = List.of(employerDTO);
        PageDTO<EmployerDTO> pageDTO = new PageDTO<>(
            employers, 1, 1, 0, 10, true, true
        );
        
        when(employerService.findWithFilters(any(EmployerFilterQuery.class))).thenReturn(pageDTO);

        // When & Then
        mockMvc.perform(post("/api/employers/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterQuery)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(employerId)))
                .andExpect(jsonPath("$.content[0].name", is("Acme Inc")))
                .andExpect(jsonPath("$.content[0].cnpj", is("12345678901234")))
                .andExpect(jsonPath("$.content[0].address", is("123 Main St")))
                .andExpect(jsonPath("$.content[0].phone", is("555-1234")))
                .andExpect(jsonPath("$.content[0].email", is("contact@acme.com")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.pageSize", is(10)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));
        
        verify(employerService, times(1)).findWithFilters(any(EmployerFilterQuery.class));
    }
}
