import { SortBy } from "./sort.by";

export interface AccountSearchSettings {
    searchPage: number;
    displayedAccounts: number;
    searchKeyword: string;
    sortBy: string;
    sortAscending: boolean;
}