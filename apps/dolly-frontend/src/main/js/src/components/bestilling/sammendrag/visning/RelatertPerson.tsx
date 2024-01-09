import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'

export const RelatertPerson = ({ personData }: any) => {
	if (!personData) {
		return null
	}

	return (
		<>
			<TitleValue title="Identtype" value={personData.identtype} />
			<TitleValue title="Kjønn" value={personData.kjoenn} />
			<TitleValue title="Født etter" value={formatDate(personData.foedtEtter)} />
			<TitleValue title="Født før" value={formatDate(personData.foedtFoer)} />
			<TitleValue title="Fødselsdato" value={formatDate(personData.foedselsdato)} />
			<TitleValue title="Alder" value={personData.alder} />
			<TitleValue
				title="Statsborgerskap"
				value={personData.statsborgerskapLandkode || personData.statsborgerskap}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
			<TitleValue title="Gradering" value={personData.gradering} />
			<TitleValue title="Har mellomnavn" value={personData.hasMellomnavn && 'JA'} />
			<TitleValue title="Fornavn" value={personData.fornavn} />
			<TitleValue title="Mellomnavn" value={personData.mellomnavn} />
			<TitleValue title="Etternavn" value={personData.etternavn} />
		</>
	)
}
