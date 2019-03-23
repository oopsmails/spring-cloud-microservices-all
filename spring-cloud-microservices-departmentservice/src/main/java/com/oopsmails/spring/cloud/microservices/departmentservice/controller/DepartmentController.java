package com.oopsmails.spring.cloud.microservices.departmentservice.controller;

import com.oopsmails.spring.cloud.microservices.departmentservice.client.EmployeeClient;
import com.oopsmails.spring.cloud.microservices.departmentservice.model.Department;
import com.oopsmails.spring.cloud.microservices.departmentservice.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DepartmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    DepartmentRepository repository;

    @Autowired
    EmployeeClient employeeClient;

    @PostMapping("/")
    @PreAuthorize("#oauth2.hasScope('write') and #oauth2.hasScope('read')")
    public Department add(@RequestBody Department department) {
        LOGGER.info("Department add: {}", department);
        return repository.add(department);
    }

    @GetMapping("/{id}")
    public Department findById(@PathVariable("id") Long id) {
        LOGGER.info("Department find: id={}", id);
        return repository.findById(id);
    }

    @GetMapping("/")
    @PreAuthorize("#oauth2.hasScope('read')")
    public List<Department> findAll() {
        LOGGER.info("+++++++++++++++++++++++++++++ Department find");
        return repository.findAll();
    }

    @GetMapping("/organization/{organizationId}")
    public List<Department> findByOrganization(@PathVariable("organizationId") Long organizationId) {
        LOGGER.info("Department find: organizationId={}", organizationId);
        return repository.findByOrganization(organizationId);
    }

    @GetMapping("/organization/{organizationId}/with-employees")
    @PreAuthorize("#oauth2.hasScope('read')")
    public List<Department> findByOrganizationWithEmployees(@PathVariable("organizationId") Long organizationId) {
        LOGGER.info("Department find: organizationId={}", organizationId);
        List<Department> departments = repository.findByOrganization(organizationId);
        departments.forEach(d -> d.setEmployees(employeeClient.findByDepartment(d.getId())));
        return departments;
    }

}
