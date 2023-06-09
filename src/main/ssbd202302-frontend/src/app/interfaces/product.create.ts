export interface ProductCreate {
    amount: number,
    color: string,
    furnitureWidth: number,
    furnitureHeight: number,
    furnitureDepth: number,
    packageWidth: number,
    packageHeight: number,
    packageDepth: number,
    price: number,
    weight: number,
    weightInPackage: number,
    woodType: string;
    productGroupId: number;
}
