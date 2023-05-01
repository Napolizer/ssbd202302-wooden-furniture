export interface Account {
  accountState: string,
  archive: boolean,
  email: string,
  failedLoginCounter: number,
  groups: string[],
  id: number,
  locale: string,
  login: string
}
