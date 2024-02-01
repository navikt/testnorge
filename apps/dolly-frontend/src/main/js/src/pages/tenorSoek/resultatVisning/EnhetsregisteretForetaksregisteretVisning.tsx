import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString } from '@/utils/DataFormatter'
import React from 'react'

export const EnhetsregisteretForetaksregisteretVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<>
			<SubOverskrift
				label={`Enhetsregisteret og Foretaksregisteret (${data.length})`}
				iconKind="brreg"
			/>
			<div>
				<DollyFieldArray data={data} header="" nested>
					{(organisasjon, idx: number) => {
						return (
							<TabsVisning kildedata={organisasjon.tenorMetadata?.kildedata}>
								<TitleValue title="Organisasjonsnummer" value={organisasjon.organisasjonsnummer} />
								<TitleValue
									title="Organisasjonsform"
									value={organisasjon.organisasjonsform?.kode}
								/>
								<TitleValue
									title="Beskrivelse"
									value={organisasjon.organisasjonsform?.beskrivelse}
								/>
								<TitleValue title="Navn" value={organisasjon.navn} />
								<TitleValue
									title="Adresse"
									value={arrayToString(organisasjon.forretningsadresse?.adresse)}
								/>
								<TitleValue
									title="Postnummer"
									value={organisasjon.forretningsadresse?.postnummer}
								/>
								<TitleValue title="Poststed" value={organisasjon.forretningsadresse?.poststed} />
								<TitleValue
									title="Kommunenummer"
									value={organisasjon.forretningsadresse?.kommunenummer}
								/>
								<TitleValue title="Kommune" value={organisasjon.forretningsadresse?.kommune} />
								<TitleValue
									title="Reg. i Enhetsregisteret"
									value={organisasjon.registreringsdatoEnhetsregisteret}
								/>
								<TitleValue title="Næringskode" value={arrayToString(organisasjon.naeringKode)} />
								<TitleValue
									title="Næringsbeskrivelse"
									value={arrayToString(organisasjon.naeringBeskrivelse)}
								/>
								{/* Relasjoner:*/}
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</>
	)
}
