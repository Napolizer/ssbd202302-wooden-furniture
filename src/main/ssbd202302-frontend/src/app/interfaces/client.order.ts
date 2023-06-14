import {Product} from "./product";
import {Address} from "./address";
import {OrderedProduct} from "./ordered.product";

export interface ClientOrder {
  id: number,
  orderProductList: OrderedProduct[],
  orderState: string,
  recipientFirstName: string,
  recipientLastName: string,
  recipientAddress: Address,
  hash: string,
  observed: boolean,
  totalPrice: number
}
