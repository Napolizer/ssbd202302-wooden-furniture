import {ProductField} from "../enums/product.field";

export interface ProductHistoryDto {
  fieldName: ProductField;
  oldValue: number;
  newValue: number;
  editDate: Date;
  editedBy: string;
  [key: string]: any;
}
