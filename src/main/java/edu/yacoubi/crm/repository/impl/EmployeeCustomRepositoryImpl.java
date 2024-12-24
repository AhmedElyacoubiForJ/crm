package edu.yacoubi.crm.repository.impl;

import edu.yacoubi.crm.dto.employee.EmployeePatchDTO;
import edu.yacoubi.crm.model.Employee;
import edu.yacoubi.crm.repository.IEmployeeCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeCustomRepositoryImpl implements IEmployeeCustomRepository {
    private final EntityManager entityManager;

    @Override
    public void partialUpdateEmployee(Long employeeId, EmployeePatchDTO employeePatchDTO) {
        log.info("EmployeeCustomRepositoryImpl::partialUpdateEmployee execution start: employeeId {}, employeePatchDTO {}", employeeId, employeePatchDTO);

        //entityValidator.validateEmployeeExists(employeeId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Employee> update = cb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = update.from(Employee.class);

        if (employeePatchDTO.getFirstName() != null) {
            update.set(root.get("firstName"), employeePatchDTO.getFirstName());
        }
        if (employeePatchDTO.getLastName() != null) {
            update.set(root.get("lastName"), employeePatchDTO.getLastName());
        }
        if (employeePatchDTO.getEmail() != null) {
            update.set(root.get("email"), employeePatchDTO.getEmail());
        }
        if (employeePatchDTO.getDepartment() != null) {
            update.set(root.get("department"), employeePatchDTO.getDepartment());
        }
        update.where(cb.equal(root.get("id"), employeeId));

        entityManager.createQuery(update).executeUpdate();

        log.info("EmployeeCustomRepositoryImpl::partialUpdateEmployee execution end");
    }
}
