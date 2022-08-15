import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { AdresseKodeverk } from '~/config/kodeverk'
import React from 'react'
import { PersonUtenIdData } from '~/components/fagsystem/pdlf/PdlTypes'

type RelatertPersonUtenIdData = {
	data: PersonUtenIdData
	tittel: string
	rolle?: string
}

export const RelatertPersonUtenId = ({ data, tittel, rolle = null }: RelatertPersonUtenIdData) => {
	if (!data) {
		return null
	}

	return (
		<div className="person-visning_content">
			<h4 style={{ width: '100%', marginTop: '0' }}>{tittel}</h4>
			<TitleValue title="Fornavn" value={data.navn?.fornavn} />
			<TitleValue title="Mellomnavn" value={data.navn?.mellomnavn} />
			<TitleValue title="Etternavn" value={data.navn?.etternavn} />
			<TitleValue title="Kjønn" value={data.kjoenn} />
			<TitleValue title="Fødselsdato" value={Formatters.formatDate(data.foedselsdato)} />
			<TitleValue
				title="Statsborgerskap"
				value={data.statsborgerskap}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
			<TitleValue title="Rolle for barn" value={rolle} size="medium" />
		</div>
	)
}
