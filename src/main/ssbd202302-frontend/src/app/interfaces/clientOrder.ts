import {Product} from "./product";
import {Address} from "./address";
import {OrderProduct} from "./orderProduct";

export interface ClientOrder {
  id: number,
  orderProductList: OrderProduct[],
  orderState: string,
  recipientFirstName: string,
  recipientLastName: string,
  recipientAddress: Address,
  hash: string,
  observed: boolean
}
