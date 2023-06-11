import {Product} from "./product";
import {Address} from "./address";
import {OrderedProduct} from "./orderedProduct";

export interface ClientOrder {
  id: number,
  orderProductList: OrderedProduct[],
  orderState: string,
  recipientFirstName: string,
  recipientLastName: string,
  recipientAddress: Address,
  hash: string,
  observed: boolean
}
