import { useEffect } from 'react'

export const KodeverkWrapper = ({ navn, value, kodeverk, fetchKodeverk, children }) => {
	useEffect(() => {
		fetchKodeverk(navn)
	})

	let verdi

	if (kodeverk && value) {
		verdi = kodeverk.find(v => v.value === value)
	}

	return children(kodeverk, verdi)
}
