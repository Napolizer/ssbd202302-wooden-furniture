import { Category } from "./category";

export interface ProductGroupInfo {
    id: number,
    name: string,
    archive: boolean,
    averageRating: number,
    category: Category;
}