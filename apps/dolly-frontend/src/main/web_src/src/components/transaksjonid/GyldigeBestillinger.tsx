import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'

type Bestilling = {
	data: Array<BestillingData>
}

type BestillingData = {
	status: string
}

export const erGyldig = (bestillingId: number, system: string, ident: string) => {
	const finnBestilling = useAsync(async () => {
		const response: Bestilling = await DollyApi.getTransaksjonid(system, ident, bestillingId)
		return response.data
	}, [])

	return (
		finnBestilling.value &&
		finnBestilling.value.length > 0 &&
		finnBestilling.value[0].status === 'OK'
	)
}
