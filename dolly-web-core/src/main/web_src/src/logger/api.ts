import Api from '../api';
import {Level} from "./types";
import config from "../config";

interface LogEvent {
    level: Level,
    event: string,
    message?: string,
    uuid: string
}

const uri = `${config.services.dollyBackend}`

export default {
    log :(event: LogEvent) : void => {
        Api.fetch(`${uri}/logg`, "POST", event);
    }
};