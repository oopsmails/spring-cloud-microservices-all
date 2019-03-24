import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { AddUserComponent } from "./add-user/add-user.component";
import { ListUserComponent } from "./list-user/list-user.component";
import { ListingComponent } from "./list-employee/listing.component";
import { EditUserComponent } from "./edit-user/edit-user.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'add-user', component: AddUserComponent },
  { path: 'list-user', component: ListUserComponent },
  { path: 'list-employee', component: ListingComponent },
  { path: 'edit-user', component: EditUserComponent },
  { path: '', component: LoginComponent }
];

export const routing = RouterModule.forRoot(routes);
