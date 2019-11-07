import { useEffectOnce } from 'react-use'

export const KodeverkWrapper = ({ navn, kodeverk, fetchKodeverk, children }) => {
	useEffectOnce(() => {
		fetchKodeverk(navn)
	}, [])
	return children(kodeverk)
}
