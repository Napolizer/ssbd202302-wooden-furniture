import {Product} from "./product";

export interface OrderProductWithRate {
  amount: number,
  rate: number,
  oldRate: number,
  price: number,
  product: Product
}
