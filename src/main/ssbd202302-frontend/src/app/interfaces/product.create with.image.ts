import { ProductCreate } from "./product.create";

export interface ProductCreateWithImage extends ProductCreate {
    imageProductId: number;
}