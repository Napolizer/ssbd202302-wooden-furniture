import { Category } from './category';

export interface ProductGroup {
  archive: boolean;
  name: string;
  id: number;
  category: Category;
  averageRating: number;
}
