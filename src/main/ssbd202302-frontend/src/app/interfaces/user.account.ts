export interface UserAccount {
  id: number,
  login: string
  email: string,
  archive: boolean,
  lastLogin: Date,
  lastFailedLogin: Date,
  lastLoginIpAddress: string,
  locale: string,
  failedLoginCounter: number,
  blockadeEnd: Date,
  accountState: string,
  groups: string[]
}
