import { Component, OnInit } from '@angular/core';
import { ApiService, Employee } from "../core/api.service";
import { Router } from '@angular/router';

@Component({
    // tslint:disable-next-line:component-selector
    selector: 'listing',
    providers: [ApiService],
    template: `<div class="container">
    <h1 class="col-sm-12">Employee Details</h1>

    <div class="col-sm-12">
        <table class="table">
            <thead>
                <tr>
                    <th>User Name</th>
                    <th>Msg Text</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let e of employees">
                    <td>{{ e.id }}</td>
                    <td>{{ e.name }}</td>
                    <td>
                        <a [routerLink]="['/', e.id]">Edit</a>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

</div>`
})

export class ListingComponent implements OnInit {
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
