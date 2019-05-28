import Request from '../Request'
import axios from 'axios'
import ConfigService from '~/service/Config'

export default class ArenaService {
	static getArenaUrl() {
		const url =
			ConfigService.getDatesourceUrl('arena') || 'https://arena-forvalteren.nais.preprod.local'
		return url + '/api/v1'
	}

	static getTestbruker(ident) {
		return Request.getWithoutCredentials(
			`https://arena-forvalteren.nais.preprod.local/bruker?filter-personident=${ident}&page=0`,
			{
				headers: {
					'Nav-Call-Id': 'dolly-frontend',
					'Nav-Consumer-Id': 'dolly'
				}
			}
		)
	}
}
