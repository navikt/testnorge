import Request from '../Request'
import axios from 'axios'
import ConfigService from '~/service/Config'

export default class InstService {
	static getInstUrl() {
		return ConfigService.getDatesourceUrl('inst') || 'https://testnorge-inst.nais.preprod.local/'
	}

	static getTestbruker(ident, env) {
		console.log('env :', env)
		return Request.getWithoutCredentials(
			`${this.getInstUrl()}api/v1/ident?identer=${ident}&miljoe=${env}`,
			{
				headers: {
					'Nav-Call-Id': 'dolly',
					'Nav-Consumer-Id': 'dolly-frontend'
				}
			}
		)
	}
}
