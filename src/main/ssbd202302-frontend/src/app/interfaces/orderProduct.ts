import {Product} from "./product";

export interface OrderProduct {
  amount: number,
  price: number,
  product: Product
}
