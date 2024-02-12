import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'

export const FolkeregisteretVisning = ({ data }) => {
	if (!data) {
		return null
	}

	return (
		<SubOverskriftExpandable label="Folkeregisteret" iconKind="personinformasjon" isExpanded={true}>
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
		</SubOverskriftExpandable>
	)
}
