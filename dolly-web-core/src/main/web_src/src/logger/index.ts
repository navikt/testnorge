import Api from './api'
// @ts-ignore
import { v4 as _uuid } from 'uuid'
import { Level, Rating } from './types'

const log = (level: Level, event: string, message?: string, uuid?: string, rating?: Rating) => {
	Api.log({ event: event, message: message, level: level, uuid: uuid || _uuid(), rating: rating })
}

interface Log {
	event: string
	message?: string
	uuid?: string
	rating?: Rating
}

export default {
	log: ({ event, message, uuid, rating }: Log) => log(Level.INFO, event, message, uuid, rating),
	warn: ({ event, message, uuid, rating }: Log) => log(Level.WARNING, event, message, uuid, rating),
	error: ({ event, message, uuid, rating }: Log) => log(Level.ERROR, event, message, uuid, rating)
}
