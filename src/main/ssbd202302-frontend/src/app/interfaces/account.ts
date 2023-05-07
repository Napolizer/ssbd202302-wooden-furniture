import {Address} from "./address";

export interface Account {
  accountState: string,
  address: Address,
  archive: boolean,
  email: string,
  failedLoginCounter: number,
  fistName: string,
  groups: string[],
  id: number,
  lastName: string,
  locale: string,
  login: string
}
