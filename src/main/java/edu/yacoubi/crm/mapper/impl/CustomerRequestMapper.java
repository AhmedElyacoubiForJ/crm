package edu.yacoubi.crm.mapper.impl;

import edu.yacoubi.crm.dto.CustomerRequestDTO;
import edu.yacoubi.crm.mapper.IMapper;
import edu.yacoubi.crm.model.Customer;
import edu.yacoubi.crm.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class CustomerRequestMapper implements IMapper<Customer, CustomerRequestDTO> {
    @Override
    public CustomerRequestDTO mapTo(Customer customer) {
        return CustomerRequestDTO.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .lastInteractionDate(customer.getLastInteractionDate())
                .build();
    }

    @Override
    public Customer mapFrom(CustomerRequestDTO customerDTO) {
        return Customer.builder()
                .firstName(customerDTO.getFirstName())
                .lastName(customerDTO.getLastName())
                .email(customerDTO.getEmail())
                .phone(customerDTO.getPhone())
                .address(customerDTO.getAddress())
                .lastInteractionDate(customerDTO.getLastInteractionDate())
                //.employee(Employee.builder().id(customerDTO.getEmployeeId()).build())
                .build();
    }
}
