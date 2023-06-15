export interface OrderStats {
  productGroupName: string;
  averageRating: number;
  amount: number;
  soldAmount: number;
  totalIncome: number;
  [key: string]: any;
}
