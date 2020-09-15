import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'

export const GyldigeBestillinger = (data: any, system: string, ident: string) => {
	const transaksjonLog = useAsync(async () => {
		const response = await DollyApi.getTransaksjonid(system, ident)
		return response.data
	}, [])

	const bestillinger: any = transaksjonLog.value
	const gyldige: Array<any> = []

	bestillinger &&
		bestillinger.forEach((bestilling: any) => {
			!gyldige.find(x => x.id === bestilling.bestillingId) &&
				gyldige.push(data.find(y => y.id === bestilling.bestillingId))
		})

	return gyldige
}
