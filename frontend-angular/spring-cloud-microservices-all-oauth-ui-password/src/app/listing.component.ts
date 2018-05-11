import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { AppService, Employee } from './app.service';

@Component({
    // tslint:disable-next-line:component-selector
    selector: 'listing',
    providers: [AppService],
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

    constructor(private _service: AppService) { }

    ngOnInit() {
        this.getInitData();
    }

    getInitData(): void {
        this._service.getResource(this.resourceUrl)
            .subscribe(
                data => {
                    this.employees = data;
                    this.mockFisrt = this.employees[0];
                },
                error => this.mockFisrt.name = 'Error');
    }
}
