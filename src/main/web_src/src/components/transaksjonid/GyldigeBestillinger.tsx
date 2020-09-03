import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'

export const GyldigeBestillinger = (data, system, ident) => {
	const transaksjonLog = useAsync(async () => {
		const response = await DollyApi.getTransaksjonid(system, ident)
		return response.data
	}, [])

	const bestillinger = transaksjonLog.value
	// console.log('bestillinger :>> ', bestillinger)
	const gyldige = []
	bestillinger &&
		bestillinger.forEach(bestilling => {
			console.log('gyldige :>> ', gyldige)
			!gyldige.find(y => y.id === bestilling.bestillingId) &&
				gyldige.push(data.find(x => x.id === bestilling.bestillingId))
		})
	return gyldige
}
