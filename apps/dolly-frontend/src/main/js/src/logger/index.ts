import Api from './api'
// @ts-ignore
import { v4 as _uuid } from 'uuid'
import { Level, Rating } from './types'

const log = (
	level: Level,
	event: string,
	message?: string,
	uuid?: string,
	rating?: Rating,
	isAnonym?: boolean
) => {
	Api.log({
		event: event,
		message: message,
		level: level,
		uuid: uuid || _uuid(),
		rating: rating,
		isAnonym: isAnonym
	})
}

interface Log {
	event: string
	message?: string
	uuid?: string
	rating?: Rating
	isAnonym?: boolean
}

export default {
	trace: ({ event, message, uuid }: Log) => log(Level.TRACE, event, message, uuid),
	log: ({ event, message, uuid, rating, isAnonym }: Log) =>
		log(Level.INFO, event, message, uuid, rating, isAnonym),
	warn: ({ event, message, uuid, rating }: Log) => log(Level.WARNING, event, message, uuid, rating),
	error: ({ event, message, uuid, rating }: Log) => log(Level.ERROR, event, message, uuid, rating)
}
