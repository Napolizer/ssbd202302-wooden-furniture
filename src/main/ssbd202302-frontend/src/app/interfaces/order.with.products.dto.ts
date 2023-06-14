import {Address} from "./address";
import {OrderedProduct} from "./ordered.product";

export interface OrderWithProductsDto {
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
