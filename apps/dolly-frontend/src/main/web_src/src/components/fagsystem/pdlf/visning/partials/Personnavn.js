import React, { Fragment } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Personnavn = ({ data }) => {
	if (!data) return false
	const { fornavn, mellomnavn, etternavn } = data
	return (
		<Fragment>
			<TitleValue title="Fornavn" value={fornavn ? fornavn : ''} />
			<TitleValue title="Mellomnavn" value={mellomnavn ? mellomnavn : ''} />
			<TitleValue title="Etternavn" value={etternavn ? etternavn : ''} />
		</Fragment>
	)
}
