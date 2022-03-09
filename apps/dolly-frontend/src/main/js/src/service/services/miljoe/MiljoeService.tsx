import Request from '~/service/services/Request'

const miljoeUrl = '/testnav-miljoer-service/api/v1'

type MiljoeResponse = {
	data: string[]
}

export default {
	getAktiveMiljoer(): Promise<string[]> {
		const endpoint = miljoeUrl + '/miljoer'
		return Request.get(endpoint).then((response: MiljoeResponse) => {
			return response.data
		})
	},
}
