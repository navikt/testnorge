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

const uri = `${config.services.dollyBackend}`

export default {
	log: (event: LogEvent): void => {
		Api.fetch(
			`${uri}/logg`,
			{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
			event
		)
	}
}
