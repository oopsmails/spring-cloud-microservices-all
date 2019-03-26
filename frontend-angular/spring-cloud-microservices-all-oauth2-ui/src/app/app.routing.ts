import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { AddUserComponent } from "./add-user/add-user.component";
import { ListUserComponent } from "./list-user/list-user.component";
import { ListEmployeeComponent } from "./list-employee/list-employee.component";
import { EditUserComponent } from "./edit-user/edit-user.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'add-user', component: AddUserComponent },
  { path: 'list-user', component: ListUserComponent },
  { path: 'list-employee', component: ListEmployeeComponent },
  { path: 'edit-user', component: EditUserComponent },
  { path: '', component: LoginComponent }
];

export const routing = RouterModule.forRoot(routes);
