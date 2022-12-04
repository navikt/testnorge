import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Personnavn = ({ data }) => {
	if (!data) return false
	const { fornavn, mellomnavn, etternavn } = data
	return (
		<>
			<TitleValue title="Kontaktperson fornavn" value={fornavn} />
			<TitleValue title="Kontaktperson mellomnavn" value={mellomnavn} />
			<TitleValue title="Kontaktperson etternavn" value={etternavn} />
		</>
	)
}
