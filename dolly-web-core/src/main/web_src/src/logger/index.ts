import Api from './api';
// @ts-ignore
import {v4 as _uuid} from 'uuid';
import {Level} from "./types";

const log = (level : Level, event: string, message?: string, uuid?: string) => {
    Api.log({ event: event, message: message, level: level, uuid: uuid || _uuid()});
};

export default {
    log: (event: string, message?: string, uuid?: string) => log(Level.INFO, event, message, uuid),
    warn: (event: string, message?: string, uuid?: string) => log(Level.WARNING, event, message, uuid),
    error: (event: string, message?: string, uuid?: string) => log(Level.ERROR, event, message, uuid)
}