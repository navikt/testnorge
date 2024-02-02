import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'

export const FolkeregisteretVisning = ({ data }) => {
	// console.log('data: ', data) //TODO - SLETT MEG
	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Folkeregisteret" iconKind="personinformasjon" />
			<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
				<TitleValue title="Identifikator" value={data.identifikator} />
				<TitleValue title="Navn" value={data.visningnavn} />
				<TitleValue title="Fødselsdato" value={formatDate(data.foedselsdato)} />
				<TitleValue title="Kjønn" value={showLabel('kjoenn', data.kjoenn)} />
				<TitleValue title="Personstatus" value={showLabel('personstatus', data.personstatus)} />
				<TitleValue title="Sivilstand" value={showLabel('sivilstandType', data.sivilstand)} />
				<TitleValue
					title="Adressebeskyttelse"
					value={showLabel('gradering', data.adresseBeskyttelse)}
				/>
				<TitleValue title="Bostedsadresse" value={data.bostedsadresse} />
				<TitleValue title="Siste hendelse" value={data.sisteHendelse} />
				{/*// Relasjoner:*/}
			</TabsVisning>
		</>
	)
}
