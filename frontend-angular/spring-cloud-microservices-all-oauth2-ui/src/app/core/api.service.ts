import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {User} from "../model/user.model";
import {Observable} from "rxjs/index";
import { HttpHeaders } from '@angular/common/http';

export class Employee {
  constructor(
    public id: string,
    public name: string) { }
}

@Injectable()
export class ApiService {

  constructor(private http: HttpClient) { }
  baseUrl: string = 'http://localhost:8080/users/';

  login(loginPayload) {
    const headers = {
      'Authorization': 'Basic ' + btoa('demops:my_secret'),
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      // 'Content-type': 'application/json'
    }
    return this.http.post('/uaa' + '/oauth/token', loginPayload, {headers});
  }

  getResource(resourceUrl): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Bearer ' + JSON.parse(window.sessionStorage.getItem('token')).access_token
    });
    console.log('getResource(), headers=', headers);
    return this.http.get(resourceUrl, { headers: headers });
  }

  getUsers() {
    return this.http.get(this.baseUrl + 'user?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  }

  getUserById(id: number) {
    return this.http.get(this.baseUrl + 'user/' + id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  }

  createUser(user: User){
    return this.http.post(this.baseUrl + 'user?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token, user);
  }

  updateUser(user: User) {
    return this.http.put(this.baseUrl + 'user/' + user.id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token, user);
  }

  deleteUser(id: number){
    return this.http.delete(this.baseUrl + 'user/' + id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  }
}
