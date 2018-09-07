import Request from '../Request'
import config from '~/config'

const uri = config.services.tpsf

export default class TpsfService {
	static getTestbrukere(userArray) {
		if (!userArray) return
		const userString = userArray.join(',')
		const endpoint = uri + '/dolly/testdata/personerdata'

		return Request.get(`${endpoint}?identer=${userString}`)
	}
}
