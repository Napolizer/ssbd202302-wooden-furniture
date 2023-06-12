import {OrderProductDto} from "./order.product.dto";
import {Address} from "./address";

export interface OrderDetailsDto {
  id: number;
  orderedProducts: OrderProductDto[];
  orderState: string;
  recipientFirstName: string;
  recipientLastName: string;
  recipientAddress: Address;
  accountId: number;
  accountLogin: string;
  hash: string;
  observed: boolean;
  totalPrice: number;
  [key: string]: any;
}
