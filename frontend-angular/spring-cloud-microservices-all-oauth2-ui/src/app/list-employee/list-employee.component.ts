import { Component, OnInit } from '@angular/core';
import { ApiService, Employee } from "../core/api.service";
import { Router } from '@angular/router';

@Component({
    // tslint:disable-next-line:component-selector
    selector: 'list-employee',
    templateUrl: './list-employee.component.html',
    styleUrls: ['./list-employee.component.css']
})

export class ListEmployeeComponent implements OnInit {
    public mockFisrt = new Employee('-1', 'mock first');
    public employees = new Array<Employee>();
    private resourceUrl = '/employee/';

    constructor(private router: Router, private apiService: ApiService) { }

    ngOnInit() {
        if(!window.sessionStorage.getItem('token')) {
            this.router.navigate(['login']);
            return;
          }
        this.getInitData();
    }

    getInitData(): void {
        this.apiService.getResource(this.resourceUrl)
            .subscribe(
                data => {
                    this.employees = data;
                    this.mockFisrt = this.employees[0];
                },
                error => this.mockFisrt.name = 'Error');
    }
}
