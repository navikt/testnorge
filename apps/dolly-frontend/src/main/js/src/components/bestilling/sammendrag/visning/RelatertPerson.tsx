import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsvisning'

export const RelatertPerson = ({ personData }: any) => {
	if (!personData) {
		return null
	}

	if (isEmpty(personData, ['syntetisk'])) {
		return <EmptyObject />
	}

	return (
		<>
			<TitleValue title="Identtype" value={personData.identtype} />
			<TitleValue title="Kjønn" value={showLabel('kjoenn', personData.kjoenn)} />
			<TitleValue title="Født etter" value={formatDate(personData.foedtEtter)} />
			<TitleValue title="Født før" value={formatDate(personData.foedtFoer)} />
			<TitleValue title="Fødselsdato" value={formatDate(personData.foedselsdato)} />
			<TitleValue title="Alder" value={personData.alder} />
			<TitleValue
				title="Statsborgerskap"
				value={personData.statsborgerskapLandkode || personData.statsborgerskap}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
			<TitleValue title="Gradering" value={showLabel('gradering', personData.gradering)} />
			<TitleValue title="Har mellomnavn" value={personData.nyttNavn?.hasMellomnavn && 'Ja'} />
			<TitleValue title="Fornavn" value={personData.navn?.fornavn} />
			<TitleValue title="Mellomnavn" value={personData.navn?.mellomnavn} />
			<TitleValue title="Etternavn" value={personData.navn?.etternavn} />
		</>
	)
}
