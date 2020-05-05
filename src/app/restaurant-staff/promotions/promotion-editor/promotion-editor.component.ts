import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PromotionEditMode} from "./promotion-edit-mode.enum";
import {Promotion} from "../../../store/promotion";
import {FormBuilder, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";
import {PromotionsService} from "../../../services/common/promotions.service";

@Component({
  selector: 'app-promotion-editor',
  templateUrl: './promotion-editor.component.html',
  styleUrls: ['./promotion-editor.component.css']
})
export class PromotionEditorComponent implements OnInit {
  private restaurantId: number;

  @Input() mode: PromotionEditMode;
  @Input() promotion: Promotion;

  @Output() promotionResult: EventEmitter<Promotion>;

  promotionForm = this.formBuilder.group({
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    type: ['', Validators.required],
    discount: ['', Validators.required]
  });

  constructor(
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private promotionsService: PromotionsService
  ) {
    this.promotionResult = new EventEmitter<Promotion>();
  }

  ngOnInit(): void {
    this.restaurantId = Number(this.activatedRoute.parent.snapshot.paramMap.get('restaurantId'));
    if (this.mode == PromotionEditMode.UPDATE) {
      this.promotionForm.setValue({
        startDate: this.promotion.startDate,
        endDate: this.promotion.endDate,
        type: this.promotion.type,
        discount: this.promotion.discount
      });
    }
  }

  public submit() {
    const promotion: Promotion = {
      id: this.promotion == undefined ? null : this.promotion.id,
      startDate: this.promotionForm.get('startDate').value,
      endDate: this.promotionForm.get('endDate').value,
      type: this.promotionForm.get('type').value,
      discount: this.promotionForm.get('discount').value
    }
    let action: Observable<Object>;
    switch(this.mode) {
      case PromotionEditMode.ADD:
        action = this.promotionsService.addRestaurantPromotion(this.restaurantId, promotion);
        break;
      case PromotionEditMode.UPDATE:
        action = this.promotionsService.updateRestaurantPromotion(this.restaurantId, promotion);
      default:
        // will not reach here
    }
    return action.subscribe(_ => this.promotionResult.emit(promotion));
  }

}
