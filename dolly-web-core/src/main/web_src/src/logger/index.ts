import Api from './api';
import {Level} from "./types";

const log = (level : Level, event: string, message?: string) => {
    Api.log({ event: event, message: message, level: level});
};

export default {
    log: (event: string, message?: string) => log(Level.INFO, event, message),
    warn: (event: string, message?: string) => log(Level.WARRING, event, message),
    error: (event: string, message?: string) => log(Level.ERROR, event, message)
}