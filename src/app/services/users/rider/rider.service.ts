import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from "rxjs";
import { Util } from 'src/app/users/util';
import { Rider } from "../../../rider/rider";
import { RiderFullTimeSchedule } from 'src/app/store/rider-full-time-schedule';
import { RiderPartTimeShift } from 'src/app/store/rider-part-time-shift';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RiderService {

  private rider: Rider;

  constructor(private httpClient: HttpClient) { }

  addRider(rider: Rider) {
    return this.httpClient.post(`${Util.baseURL}/riders`, rider);
  }

  fetchAllRiders(): Observable<any> {
    return this.httpClient.get(`${Util.baseURL}/riders`);
  }

  fetchRiderInfo(drid: number): Observable<any> {
    return this.httpClient.get(`${Util.baseURL}/rider/${drid}/riderInfo`);
  }

  fetchSalaryInfo(drid: number) {
    return this.httpClient.get(`${Util.baseURL}/rider/${drid}/salaryInfo/`);
  }

  fetchFullTimeSchedule(drid: number) {
    return this.httpClient.get(`${Util.baseURL}/rider/${drid}/fullTimeSchedule/`);
  }

  updateFullTimeSchedule(drid: number, fullTimeSchedule: RiderFullTimeSchedule) {
    return this.httpClient.put(`${Util.baseURL}/rider/${drid}/fullTimeSchedule/`, fullTimeSchedule);
  }

  fetchPartTimeShift(drid: number) {
    return this.httpClient.get(`${Util.baseURL}/rider/${drid}/partTimeShift/`);
  }
  deletePartTimeShift(drid: number, ptsid: number) {
    return this.httpClient.delete(`${Util.baseURL}/rider/${drid}/partTimeShift/${ptsid}`).pipe(catchError(this.handleError));
  }

  addPartTimeShift(drid: number, partTimeShiftList: RiderPartTimeShift[]) {
    return this.httpClient.post(`${Util.baseURL}/rider/${drid}/partTimeShift/`, partTimeShiftList).pipe(catchError(this.handleError));
  }

  setRider(rider: Rider) {
    this.rider = rider;
  }

  getRider() {
    return this.rider;
  }

  handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error!';
    if (error.error instanceof ErrorEvent) {
      // Client-side errors
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side errors
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    window.alert(errorMessage);
    return throwError(errorMessage);
  }
}
