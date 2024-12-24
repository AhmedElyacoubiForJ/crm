package edu.yacoubi.crm.repository;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;

public interface IEmployeeCustomRepository {
    void partialUpdateEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO);
}
