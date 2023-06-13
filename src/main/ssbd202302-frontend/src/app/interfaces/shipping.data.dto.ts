import {Address} from "./address";

export interface ShippingDataDto {
  recipientFirstName: string;
  recipientLastName: string;
  recipientAddress: Address;
}
