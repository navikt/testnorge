import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { formatTenorDate } from '@/utils/DataFormatter'

export const EnhetsregisteretVisning = ({ data: brregData }: { data: any }) => {
	if (!brregData) {
		return null
	}

	const { forretningsadresse, postadresse } = brregData
	return (
		<>
			<SubOverskriftExpandable label="Organisasjonsinfo" iconKind="organisasjon" isExpanded={true}>
				<TabsVisning kildedata={brregData.tenorMetadata?.kildedata}>
					<TitleValue title="Organisasjonsnummer" value={brregData.organisasjonsnummer} />
					<TitleValue title="Navn" value={brregData.navn} />
					<TitleValue
						title="Dato registrert i EREG"
						value={formatTenorDate(brregData.registreringsdatoEnhetsregisteret)}
					/>
					<TitleValue
						title="Organisasjonsform"
						value={
							brregData.organisasjonsform?.beskrivelse &&
							`${brregData.organisasjonsform.beskrivelse} (${brregData.organisasjonsform.kode})`
						}
					/>
					{brregData?.naeringskoder && brregData.naeringskoder.length > 0 && (
						<>
							<TitleValue
								title="Næringskode"
								value={brregData.naeringskoder[brregData.naeringskoder.length - 1].kode}
							/>
							<TitleValue
								title="Næringsbeskrivelse"
								value={brregData.naeringskoder[brregData.naeringskoder.length - 1].beskrivelse}
							/>
						</>
					)}
					<TitleValue
						title="Matrikkelenhet ID"
						value={brregData.matrikkelnummer?.[0]?.matrikkelEnhetId}
					/>
				</TabsVisning>
			</SubOverskriftExpandable>
			{forretningsadresse?.land && (
				<SubOverskriftExpandable label="Forretningsadresse" iconKind="adresse" isExpanded={false}>
					<TabsVisning kildedata={brregData.tenorMetadata?.kildedata}>
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
				<SubOverskriftExpandable label="Postadresse" iconKind="postadresse" isExpanded={false}>
					<TabsVisning kildedata={brregData.tenorMetadata?.kildedata}>
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
