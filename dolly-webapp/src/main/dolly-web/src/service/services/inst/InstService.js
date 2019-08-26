import Request from '../Request'
import axios from 'axios'
import ConfigService from '~/service/Config'

export default class InstService {
	static getInstUrl() {
		return ConfigService.getDatesourceUrl('inst') || 'https://testnorge-inst.nais.preprod.local'
	}

	static getTestbruker(ident, env) {
		return Request.getWithoutCredentials(
			`${this.getInstUrl()}/api/v1/ident?identer=${ident}&miljoe=${env[0]}`,
			{
				headers: {
					NavCallId: 'dolly',
					NavConsumerId: 'dolly-frontend'
				}
			}
		)
	}

	static getTilgjengeligeMiljoer() {
		return Request.getWithoutCredentials(`${this.getInstUrl()}/api/v1/miljoer`)
	}
}
