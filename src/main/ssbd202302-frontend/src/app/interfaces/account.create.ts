import { Accesslevel } from "./accesslevel";
import { AccountRegister } from "./account.register";

export interface AccountCreate extends AccountRegister {
    accessLevel : Accesslevel
}