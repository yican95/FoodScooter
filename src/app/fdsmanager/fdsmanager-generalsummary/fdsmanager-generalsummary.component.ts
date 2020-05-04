import { Component, OnInit } from '@angular/core';
import {FDSManagerService} from "../../services/users/fdsmanager/fdsmanager.service";
import {GeneralSummary} from "../../store/summaries/generalSummary";

@Component({
  selector: 'app-fdsmanager-generalsummary',
  templateUrl: './fdsmanager-generalsummary.component.html',
  styleUrls: ['./fdsmanager-generalsummary.component.css']
})
export class FDSManagerGeneralsummaryComponent implements OnInit {
  id: number;
  generalSummaryList: GeneralSummary[];

  constructor(
    private fdsManagerService: FDSManagerService
  ) { }

  ngOnInit(): void {
    this.getGeneralSummary();
  }

  getGeneralSummary(): void {
    this.fdsManagerService.fetchGeneralSummary().subscribe((data: any[]) => {
      console.log(data);
      this.generalSummaryList = data;
    })
  }
}
