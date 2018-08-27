import Request from '../Request'

//const uri = 'https://tps-forvalteren-u2.nais.preprod.local/api/v1/dolly/testdata/personerdata'
const uri = 'http://tps-forvalteren/api/v1/dolly/testdata/personerdata';

export default class TpsfService {
	static getTestbrukere(userArray) {
		const userString = userArray.join(',')

		return Request.get(`${uri}?identer=${userString}`)
	}
}
