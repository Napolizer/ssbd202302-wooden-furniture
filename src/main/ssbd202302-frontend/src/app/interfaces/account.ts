import {Address} from "./address";

export interface Account {
  accountState: string,
  address: Address,
  archive: boolean,
  blockadeEnd: Date,
  email: string,
  failedLoginCounter: number,
  firstName: string,
  roles: string[],
  id: number,
  lastName: string,
  lastLogin: Date,
  lastFailedLogin: Date,
  lastLoginIpAddress: string,
  lastFailedLoginIpAddress: string,
  locale: string,
  login: string,
  hash: string,
  timeZone: string
  [key: string]: any;
}
