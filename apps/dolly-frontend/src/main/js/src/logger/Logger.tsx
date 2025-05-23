// @ts-ignore
import { v4 as _uuid } from 'uuid'
import Api from '@/api/index'

const log = (event: object | undefined) => {
	Api.fetch(
		`/api/dolly-logg`,
		{ method: 'POST', headers: { 'Content-Type': 'application/json' } },
		event,
	)
}

const logger = (
	level: string,
	event: any,
	message: any,
	uuid: any,
	rating?: undefined,
	isAnonym?: undefined,
	brukerType?: undefined,
	brukernavn?: string,
	tilknyttetOrganisasjon?: string,
) => {
	log({
		event: event,
		message: message,
		level: level,
		uuid: uuid || _uuid(),
		rating: rating,
		isAnonym: isAnonym,
		brukerType: brukerType,
		brukernavn: brukernavn,
		tilknyttetOrganisasjon: tilknyttetOrganisasjon,
	})
}

export const Logger = {
	trace: ({ event, message, uuid }) =>
		logger('TRACE', event, message, uuid, undefined, undefined, undefined),
	log: ({
		event,
		message,
		uuid,
		rating,
		isAnonym,
		brukerType,
		brukernavn,
		tilknyttetOrganisasjon,
	}: any) =>
		logger(
			'INFO',
			event,
			message,
			uuid,
			rating,
			isAnonym,
			brukerType,
			brukernavn,
			tilknyttetOrganisasjon,
		),
	warn: ({ event, message, uuid, rating }) =>
		logger('WARNING', event, message, uuid, rating, undefined, undefined),
	error: ({ event, message, uuid, rating = undefined }) =>
		logger('ERROR', event, message, uuid, rating, undefined, undefined),
}
