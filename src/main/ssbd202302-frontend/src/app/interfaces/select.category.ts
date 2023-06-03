import { SelectItem } from './select.item';

export interface SelectCategory extends SelectItem {
  subcategories: SelectCategory[];
}
