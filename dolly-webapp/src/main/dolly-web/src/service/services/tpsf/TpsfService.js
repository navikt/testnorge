import Request from '../Request'

const uri = '/tpsf/api/v1/dolly/testdata/personerdata'

export default class TpsfService {
	static getTestbrukere(userArray) {
		const userString = userArray.join(',')

		return Request.get(`${uri}?identer=${userString}`)
	}
}
