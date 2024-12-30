package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.util.TestDataUtil;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.model.Note;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerRepositoryUnitTest {

    @Autowired
    private CustomerRepository underTest;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private NoteRepository noteRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        Employee employeeA = TestDataUtil.createEmployeeA();
        entityManager.persist(employeeA);
        entityManager.flush();
    }

    @Test
    public void itShouldThrowWhenCreateCustomerWithoutEmployee() {
        // Given
        Customer customer = TestDataUtil.createCustomerA(null);

        // When & Then
        DataIntegrityViolationException exception =
                assertThrows(DataIntegrityViolationException.class, () -> {
                            underTest.save(customer);
                        }
                );
        String expectedMessage = "NULL not allowed for column \"EMPLOYEE_ID\"";
        assertTrue(exception.getMessage().contains(expectedMessage), expectedMessage);
    }

    @Test
    public void itShouldReturnCustomerByEmail() {
        // Given
        Employee employee = entityManager.find(Employee.class, 1L);
        Customer customerA = TestDataUtil.createCustomerA(employee);
        String givenEmail = customerA.getEmail(); // Verwende die E-Mail des Kunden
        Customer savedCustomer = underTest.save(customerA);
        System.out.println(savedCustomer);

        // When
        Optional<Customer> customerOptional = underTest.findByEmail(givenEmail);
        System.out.println(customerOptional);

        // Then
        assertTrue(customerOptional.isPresent());
        Customer customer = customerOptional.get();
        assertEquals(givenEmail, customer.getEmail());
    }

    @Test
    public void itShouldNotReturnCustomerByNotExistingEmail() {
        // Given
        String notExistingEmail = "not.existing@example.com";

        // When
        Optional<Customer> customerOptional = underTest.findByEmail(notExistingEmail);

        // Then
        assertFalse(customerOptional.isPresent());
    }

    @Test
    @Transactional
    public void itShouldCreateNotesToCustomer() {
        // Given
        Employee employee = entityManager.find(Employee.class, 1L);
        Customer customer = TestDataUtil.createCustomerA(employee);
        underTest.save(customer);

        // When
        Note noteA = TestDataUtil.createNoteA(customer);
        Note noteB = TestDataUtil.createNoteB(customer);
        customer.addNote(noteA);
        customer.addNote(noteB);

        // Save customer again to persist notes
        underTest.save(customer);
        entityManager.flush();

        // Then
        Customer foundCustomer = underTest.findById(customer.getId()).orElse(null);
        assertNotNull(foundCustomer);
        assertEquals(2, foundCustomer.getNotes().size());
    }

    @Test
    @Transactional
    public void itShouldDeleteCustomerNotesIfCustomerDeleted() {
        // Given
        Employee employee = entityManager.find(Employee.class, 1L);
        Customer customer = TestDataUtil.createCustomerA(employee);
        underTest.save(customer);

        Note noteA = TestDataUtil.createNoteA(customer);
        Note noteB = TestDataUtil.createNoteB(customer);
        customer.addNote(noteA);
        customer.addNote(noteB);

        // Save notes separately to ensure IDs are generated
        Note persistedAndFlushNoteA = entityManager.persistAndFlush(noteA);
        Note persistedAndFlushNoteB = entityManager.persistAndFlush(noteB);

        // Ensure notes are saved and IDs are generated
        assertNotNull(persistedAndFlushNoteA.getId());
        assertNotNull(persistedAndFlushNoteB.getId());

        // Print notes for debugging (optional)
        underTest.findById(customer.getId())
                .ifPresent(cust -> cust.getNotes().forEach(System.out::println));

        // When
        underTest.deleteById(customer.getId());
        entityManager.flush();

        // Then
        assertNull(underTest.findById(customer.getId()).orElse(null));
        assertNull(entityManager.find(Note.class, persistedAndFlushNoteA.getId()));
        assertNull(entityManager.find(Note.class, persistedAndFlushNoteB.getId()));
    }

    @Test
    public void itShouldReturnAllCustomersPaged() {
        // Given
        Employee employee = entityManager.find(Employee.class, 1L);
        for (int i = 0; i < 10; i++) {
            Customer customer = TestDataUtil.createCustomerA(employee);
            customer.setEmail(i + customer.getEmail());
            underTest.save(customer);
        }


        // When
        Pageable pageable = PageRequest.of(0, 1);
        Page<Customer> customers = underTest.findAll(pageable);

        // Then
        assertEquals(10, customers.getTotalElements());
        assertEquals(10, customers.getTotalPages());
        assertEquals(1, customers.getContent().size());
    }

    @Test
    public void itShouldReturnCustomersByEmployeeId() {
        // Given
        Employee employee = entityManager.find(Employee.class, 1L);
        Customer customerA = TestDataUtil.createCustomerA(employee);
        Customer customerB = TestDataUtil.createCustomerB(employee);
        underTest.save(customerA);
        underTest.save(customerB);

        // When
        List<Customer> customersByEmployeeId = underTest.findByEmployeeId(employee.getId());

        // Then
        assertEquals(2, customersByEmployeeId.size());
        assertTrue(customersByEmployeeId.contains(customerA));
        assertTrue(customersByEmployeeId.contains(customerB));
    }
}