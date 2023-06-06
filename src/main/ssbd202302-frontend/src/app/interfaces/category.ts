export interface Category {
  id: number;
  name: string;
  subcategories: Category[];
  parentName: string;
  imageUrl: string;
}
