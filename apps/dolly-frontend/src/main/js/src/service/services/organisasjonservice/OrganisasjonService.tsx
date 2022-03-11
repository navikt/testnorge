import Request from '~/service/services/Request'
import { OrgInfoResponse } from '~/service/services/organisasjonservice/types'

const orgServiceUrl = '/testnav-organisasjon-service/api/v1/organisasjoner'

export default {
	getOrganisasjonInfo(orgnummer: string, miljoe: string): Promise<OrgInfoResponse> {
		return Request.get(`${orgServiceUrl}/${orgnummer}`, { miljo: miljoe ? miljoe : 'q1' }).then(
			(response: OrgInfoResponse) => {
				if (response != null) return response
			}
		)
	},
}
