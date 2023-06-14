import { FurnitureDimensions } from "./furniture.dimensions";
import { PackageDimensions } from "./package.dimensions";
import {ProductGroupInfo} from "./product.group.info"

export interface Product {
    id: number,
    amount: number,
    archive: boolean,
    color: string,
    furnitureDimensions: FurnitureDimensions,
    packageDimensions: PackageDimensions,
    price: number,
    imageUrl: string;
    weight: number,
    weightInPackage: number,
    woodType: string;
    productGroup: ProductGroupInfo;
    hash: string;
}
