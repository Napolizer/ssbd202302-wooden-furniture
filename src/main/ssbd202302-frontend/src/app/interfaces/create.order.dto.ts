import {ShippingDataDto} from "./shipping.data.dto";
import {OrderProductDto} from "./order.product.dto";

export interface CreateOrderDto {
  products: OrderProductDto[];
  shippingData: ShippingDataDto;
}
