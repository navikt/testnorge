import { useEffect } from 'react'

export const KodeverkWrapper = ({ navn, kodeverk, fetchKodeverk, children }) => {
	useEffect(() => {
		fetchKodeverk(navn)
	})
	return children(kodeverk)
}
