import {OrderedProduct} from "./ordered.product";
import {Address} from "./address";

export interface OrderDto {
  id: number;
  orderedProducts: OrderedProduct[];
  orderState: string;
  recipientFirstName: string;
  recipientLastName: string;
  recipientAddress: Address;
  accountLogin: string;
  hash: string
  observed: boolean;
  totalPrice: number;
}
