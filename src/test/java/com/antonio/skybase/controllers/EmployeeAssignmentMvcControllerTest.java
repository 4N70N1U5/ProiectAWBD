package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeAssignmentDTO;
import com.antonio.skybase.entities.*;
import com.antonio.skybase.services.EmployeeAssignmentService;
import com.antonio.skybase.services.EmployeeService;
import com.antonio.skybase.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeAssignmentMvcControllerTest {

    @Mock
    private EmployeeAssignmentService employeeAssignmentService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private EmployeeAssignmentMvcController employeeAssignmentMvcController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeAssignmentMvcController).build();
    }

    @Test
    void list_ShouldReturnListView() throws Exception {
        // Arrange
        EmployeeAssignment assignment1 = new EmployeeAssignment();
        EmployeeAssignmentId id1 = new EmployeeAssignmentId();
        id1.setEmployeeId(1);
        id1.setFlightId(1);
        id1.setDate(LocalDate.of(2024, 1, 1));
        assignment1.setId(id1);

        List<EmployeeAssignment> assignments = Arrays.asList(assignment1);
        when(employeeAssignmentService.getAll()).thenReturn(assignments);

        // Act & Assert
        mockMvc.perform(get("/web/employee-assignments"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/list"))
                .andExpect(model().attributeExists("employeeAssignments"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attribute("employeeAssignments", assignments));

        verify(employeeAssignmentService).getAll();
    }

    @Test
    void showCreateForm_ShouldReturnFormView() throws Exception {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Employee> employees = Arrays.asList(employee);
        List<Flight> flights = Arrays.asList(flight);
        when(employeeService.getAll()).thenReturn(employees);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(get("/web/employee-assignments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().attributeExists("currentPage"));

        verify(employeeService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void create_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments")
                .param("employeeId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employee-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeAssignmentService).create(any(EmployeeAssignmentDTO.class));
    }

    @Test
    void create_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Employee> employees = Arrays.asList(employee);
        List<Flight> flights = Arrays.asList(flight);
        when(employeeService.getAll()).thenReturn(employees);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments")
                .param("employeeId", "1")
                .param("flightId", "") // Invalid flight ID
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().hasErrors());

        verify(employeeService).getAll();
        verify(flightService).getAll();
        verifyNoInteractions(employeeAssignmentService);
    }

    @Test
    void create_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Employee> employees = Arrays.asList(employee);
        List<Flight> flights = Arrays.asList(flight);
        when(employeeService.getAll()).thenReturn(employees);
        when(flightService.getAll()).thenReturn(flights);
        doThrow(new RuntimeException("Database error")).when(employeeAssignmentService)
                .create(any(EmployeeAssignmentDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments")
                .param("employeeId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(employeeAssignmentService).create(any(EmployeeAssignmentDTO.class));
        verify(employeeService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void details_WithValidId_ShouldReturnDetailsView() throws Exception {
        // Arrange
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2024, 1, 1));

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);
        when(employeeAssignmentService.getById(any(EmployeeAssignmentId.class))).thenReturn(assignment);

        // Act & Assert
        mockMvc.perform(get("/web/employee-assignments/1/1/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/details"))
                .andExpect(model().attributeExists("employeeAssignment"))
                .andExpect(model().attributeExists("currentPage"));

        verify(employeeAssignmentService).getById(any(EmployeeAssignmentId.class));
    }

    @Test
    void showEditForm_WithValidId_ShouldReturnFormView() throws Exception {
        // Arrange
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2024, 1, 1));

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);

        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Employee> employees = Arrays.asList(employee);
        List<Flight> flights = Arrays.asList(flight);

        when(employeeAssignmentService.getById(any(EmployeeAssignmentId.class))).thenReturn(assignment);
        when(employeeService.getAll()).thenReturn(employees);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(get("/web/employee-assignments/1/1/2024-01-01/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("originalId"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"));

        verify(employeeAssignmentService).getById(any(EmployeeAssignmentId.class));
        verify(employeeService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void update_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments/1/1/2024-01-01")
                .param("employeeId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employee-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeAssignmentService).update(any(EmployeeAssignmentId.class), any(EmployeeAssignmentDTO.class));
    }

    @Test
    void update_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Employee> employees = Arrays.asList(employee);
        List<Flight> flights = Arrays.asList(flight);
        when(employeeService.getAll()).thenReturn(employees);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments/1/1/2024-01-01")
                .param("employeeId", "") // Invalid employee ID
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("originalId"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().hasErrors());

        verify(employeeService).getAll();
        verify(flightService).getAll();
        verifyNoInteractions(employeeAssignmentService);
    }

    @Test
    void delete_WithValidId_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments/1/1/2024-01-01/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employee-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeAssignmentService).delete(any(EmployeeAssignmentId.class));
    }

    @Test
    void delete_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(employeeAssignmentService)
                .delete(any(EmployeeAssignmentId.class));

        // Act & Assert
        mockMvc.perform(post("/web/employee-assignments/1/1/2024-01-01/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employee-assignments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(employeeAssignmentService).delete(any(EmployeeAssignmentId.class));
    }
}
