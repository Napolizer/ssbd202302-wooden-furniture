export interface ProductCreate {
    amount: number,
    available: boolean,
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