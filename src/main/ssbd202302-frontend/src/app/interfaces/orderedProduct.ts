import {Product} from "./product";

export interface OrderedProduct {
  amount: number,
  price: number,
  product: Product
}
