// import { useAsync } from 'react-use'
// import _get from 'lodash/get'
// import { DollyApi } from '~/service/Api'

// export const JoarkMetadata = (system, ident) => {
// 	const transaksjonLog = useAsync(async () => {
// 		const response = await DollyApi.getTransaksjonid(system, ident)
// 		return response.data
// 	}, [])

// 	const getMetadata = async bestilling => {
// 		await DollyApi.getDokarkivMetadata(
// 			JSON.parse(bestilling.transaksjonId).journalpostId,
// 			bestilling.miljoe
// 		).then(response => {
// 			metadata.push(response.data)
// 		})
// 	}

// 	const bestillinger = transaksjonLog.value
// 	const metadata = []

// 	bestillinger &&
// 		bestillinger.forEach(bestilling => {
// 			//! Sjekk for å unngå duplikater?
// 			getMetadata(bestilling)
// 		})

// 	return metadata
// }
