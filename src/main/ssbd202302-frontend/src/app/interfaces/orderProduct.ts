import {Product} from "./product";

export interface OrderProduct {
  amount: number,
  price: number,
  rate: number,
  product: Product
}
