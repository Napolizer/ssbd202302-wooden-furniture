import {Product} from "./product";
import {Address} from "./address";

export interface ClientOrder {
  id: number,
  products: Product[],
  orderState: string,
  firstName: string,
  lastName: string,
  addressDto: Address,
  hash: string,
  observed: boolean
}
