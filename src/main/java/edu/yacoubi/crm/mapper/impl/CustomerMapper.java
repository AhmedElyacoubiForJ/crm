package edu.yacoubi.crm.mapper.impl;

import edu.yacoubi.crm.dto.CustomerDTO;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper implements IMapper<Customer, CustomerDTO> {
    @Override
    public CustomerDTO mapTo(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .lastInteractionDate(customer.getLastInteractionDate())
                .employeeId(customer.getEmployee().getId())
                .build();
    }

    @Override
    public Customer mapFrom(CustomerDTO customerDTO) {
        return Customer.builder()
               .id(customerDTO.getId())
               .firstName(customerDTO.getFirstName())
               .lastName(customerDTO.getLastName())
               .email(customerDTO.getEmail())
               .phone(customerDTO.getPhone())
               .address(customerDTO.getAddress())
               .lastInteractionDate(customerDTO.getLastInteractionDate())
               .employee(Employee.builder().id(customerDTO.getEmployeeId()).build())
               .build();
    }
}
