import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Barn = ({ data }) => {
	return (
		<div className="person-visning_content">
			<TitleValue title={data.identtype} value={data.ident} />
			<TitleValue title="Fornavn" value={data.fornavn} />

			<TitleValue title="Mellomnavn" value={data.mellomnavn} />

			<TitleValue title="Etternavn" value={data.etternavn} />

			<TitleValue title="Kjønn" value={Formatters.kjonnToString(data.kjonn)} />

			<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
			{/* Vise om barnet bor sammen med hovedperson? */}
		</div>
	)
}
