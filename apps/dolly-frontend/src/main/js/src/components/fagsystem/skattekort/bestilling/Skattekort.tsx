import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { KodeverkTitleValue } from '@/components/fagsystem/skattekort/visning/Visning'
import { formatDate, toTitleCase } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import {
	ArbeidsgiverSkatt,
	Forskuddstrekk,
	SkattekortTypes,
} from '@/components/fagsystem/skattekort/SkattekortTypes'

export const Skattekort = ({ skattekort }: SkattekortTypes) => {
	if (!skattekort || isEmpty(skattekort)) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Nav skattekort</BestillingTitle>
				<DollyFieldArray header="Skattekort" data={skattekort?.arbeidsgiverSkatt}>
					{(arbeidsgiver: ArbeidsgiverSkatt, idx: number) => {
						const arbeidstaker = arbeidsgiver?.arbeidstaker?.[0]
						const trekkListe = arbeidstaker?.skattekort?.forskuddstrekk

						return (
							<React.Fragment key={idx}>
								<KodeverkTitleValue
									kodeverkstype="RESULTATSTATUS"
									value={arbeidstaker?.resultatPaaForespoersel}
									label="Resultat på forespørsel"
								/>
								<TitleValue title="Inntektsår" value={arbeidstaker?.inntektsaar} />
								<TitleValue
									title="Utstedt dato"
									value={formatDate(arbeidstaker?.skattekort?.utstedtDato)}
								/>
								<DollyFieldArray header="Forskuddstrekk" data={trekkListe} nested>
									{(trekk: Forskuddstrekk, idx: number) => {
										const forskuddstrekkType = Object.keys(trekk)?.filter((key) => trekk[key])?.[0]
										const forskuddstrekk = trekk[forskuddstrekkType]

										return (
											<React.Fragment key={idx}>
												<h4 style={{ width: '100%', margin: '0 0 15px 0' }}>
													{toTitleCase(forskuddstrekkType)}
												</h4>
												<KodeverkTitleValue
													kodeverkstype="TREKKODE"
													value={forskuddstrekk?.trekkode}
													label="Trekkode"
												/>
												<TitleValue title="Frikortbeløp" value={forskuddstrekk?.frikortbeloep} />
												<KodeverkTitleValue
													kodeverkstype="TABELLTYPE"
													value={forskuddstrekk?.tabelltype}
													label="Tabelltype"
												/>
												<TitleValue title="Tabellnummer" value={forskuddstrekk?.tabellnummer} />
												<TitleValue title="Prosentsats" value={forskuddstrekk?.prosentsats} />
												<TitleValue
													title="Antall måneder for trekk"
													value={forskuddstrekk?.antallMaanederForTrekk}
												/>
											</React.Fragment>
										)
									}}
								</DollyFieldArray>
								<KodeverkTitleValue
									kodeverkstype="TILLEGGSOPPLYSNING"
									value={arbeidstaker?.tilleggsopplysning}
									label="Tilleggsopplysning"
								/>
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
