import Api from '../api'
import { Level, Rating } from './types'
import config from '../config'

interface LogEvent {
	level: Level
	event: string
	message?: string
	uuid: string
	rating?: Rating
}

export default {
	log: (event: LogEvent): void => {
		Api.fetch(
			`/api/dolly-log`,
			{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
			event
		)
	}
}
