import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { TenorOrganisasjon } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonTenorVisning'

export const EnhetsregisteretVisning = ({ data }: { data: TenorOrganisasjon }) => {
	if (!data) {
		return null
	}

	const { forretningsadresse, postadresse } = data

	return (
		<>
			<SubOverskriftExpandable label="Organisasjonsinfo" iconKind="organisasjon" isExpanded={true}>
				<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
					<TitleValue title="Organisasjonsnummer" value={data.organisasjonsnummer} />
					<TitleValue title="Navn" value={data.navn} />
					<TitleValue
						title="Organisasjonsform"
						value={
							data.organisasjonsform?.beskrivelse &&
							`${data.organisasjonsform.beskrivelse} (${data.organisasjonsform.kode})`
						}
					/>
				</TabsVisning>
			</SubOverskriftExpandable>
			{forretningsadresse?.land && (
				<SubOverskriftExpandable label="Forretningsadresse" iconKind="adresse" isExpanded={true}>
					<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
						<TitleValue title="Land" value={forretningsadresse?.land} />
						<TitleValue title="Landkode" value={forretningsadresse?.landkode} />
						<TitleValue title="Postnummer" value={forretningsadresse?.postnummer} />
						<TitleValue title="Poststed" value={forretningsadresse?.poststed} />
						<TitleValue title="Adresse" value={forretningsadresse?.adresse.join(', ')} />
						<TitleValue title="Kommune" value={forretningsadresse?.kommune} />
						<TitleValue title="Kommunenummer" value={forretningsadresse?.kommunenummer} />
					</TabsVisning>
				</SubOverskriftExpandable>
			)}
			{postadresse?.land && (
				<SubOverskriftExpandable label="Postadresse" iconKind="postadresse" isExpanded={true}>
					<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
						<TitleValue title="Land" value={postadresse?.land} />
						<TitleValue title="Landkode" value={postadresse?.landkode} />
						<TitleValue title="Postnummer" value={postadresse?.postnummer} />
						<TitleValue title="Poststed" value={postadresse?.poststed} />
						<TitleValue title="Adresse" value={postadresse?.adresse.join(', ')} />
						<TitleValue title="Kommune" value={postadresse?.kommune} />
						<TitleValue title="Kommunenummer" value={postadresse?.kommunenummer} />
					</TabsVisning>
				</SubOverskriftExpandable>
			)}
		</>
	)
}
