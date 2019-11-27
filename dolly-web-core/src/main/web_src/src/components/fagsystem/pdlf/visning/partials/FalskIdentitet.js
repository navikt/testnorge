import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
export const FalskIdentitet = ({ data, loading }) => {
	if (loading) return <Loading label="laster PDL-data" />

	return (
		<div>
			<SubOverskrift label="Falsk identitet" />
			<div className="person-visning_content">
				<TitleValue
					title="Rett identitet"
					value={data.rettIdentitetErUkjent ? data.rettIdentitetVedIdentifikasjonsnummer : 'Ukjent'}
				/>

				<TitleValue title="Fornavn" value={data.rettIdentitetVedOpplysninger.personnavn.fornavn} />

				<TitleValue
					title="Mellomnavn"
					value={data.rettIdentitetVedOpplysninger.personnavn.mellomnavn}
				/>

				<TitleValue
					title="Etternavn"
					value={data.rettIdentitetVedOpplysninger.personnavn.etternavn}
				/>

				<TitleValue title="Fødselsdato" value={data.rettIdentitetVedOpplysninger.foedselsdato} />

				<TitleValue
					title="Statsborgerskap"
					value={data.rettIdentitetVedOpplysninger.statsborgerskap}
				/>

				<TitleValue title="Kjønn" value={data.rettIdentitetVedOpplysninger.kjoenn} />
			</div>
		</div>
	)
}
