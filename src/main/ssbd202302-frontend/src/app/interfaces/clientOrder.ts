import {Product} from "./product";
import {Address} from "./address";

export interface ClientOrder {
  id: number,
  products: Product[],
  firstName: string,
  lastName: string,
  addressDto: Address,
  hash: string,
  observed: boolean
}
