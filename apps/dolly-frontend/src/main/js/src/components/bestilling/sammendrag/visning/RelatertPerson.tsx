import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate, showLabel } from '@/utils/DataFormatter'
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
			<TitleValue title="Gradering" value={showLabel('gradering', personData.gradering)} />
			<TitleValue title="Har mellomnavn" value={personData.nyttNavn?.hasMellomnavn && 'JA'} />
			<TitleValue title="Fornavn" value={personData.navn?.fornavn} />
			<TitleValue title="Mellomnavn" value={personData.navn?.mellomnavn} />
			<TitleValue title="Etternavn" value={personData.navn?.etternavn} />
		</>
	)
}
