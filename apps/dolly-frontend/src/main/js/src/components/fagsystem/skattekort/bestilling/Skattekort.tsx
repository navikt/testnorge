import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { showKodeverkLabel } from '@/components/fagsystem/skattekort/visning/Visning'
import { arrayToString, formatDate, toTitleCase } from '@/utils/DataFormatter'
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
				<BestillingTitle>Skattekort (SOKOS)</BestillingTitle>
				<DollyFieldArray header="Skattekort" data={skattekort?.arbeidsgiverSkatt}>
					{(arbeidsgiver: ArbeidsgiverSkatt, idx: number) => {
						const arbeidstaker = arbeidsgiver?.arbeidstaker?.[0]
						const trekkListe = arbeidstaker?.skattekort?.forskuddstrekk

						const tilleggsopplysningFormatted = arbeidstaker?.tilleggsopplysning?.map(
							(tilleggsopplysning) => {
								return showKodeverkLabel('TILLEGGSOPPLYSNING', tilleggsopplysning)
							},
						)
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Resultat på forespørsel"
									value={showKodeverkLabel('RESULTATSTATUS', arbeidstaker?.resultatPaaForespoersel)}
								/>
								<TitleValue title="Inntektsår" value={arbeidstaker?.inntektsaar} />
								<TitleValue
									title="Utstedt dato"
									value={formatDate(arbeidstaker?.skattekort?.utstedtDato)}
								/>
								<TitleValue
									title="Skattekortidentifikator"
									value={arbeidstaker?.skattekort?.skattekortidentifikator}
								/>
								<TitleValue
									title="Tilleggsopplysning"
									value={arrayToString(tilleggsopplysningFormatted)}
								/>
								<TitleValue
									title="Arbeidsgiver (org.nr.)"
									value={arbeidsgiver?.arbeidsgiveridentifikator?.organisasjonsnummer}
								/>
								<TitleValue
									title="Arbeidsgiver (ident)"
									value={arbeidsgiver?.arbeidsgiveridentifikator?.personidentifikator}
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
												<TitleValue
													title="Trekkode"
													value={showKodeverkLabel('TREKKODE', forskuddstrekk?.trekkode)}
												/>
												<TitleValue title="Frikortbeløp" value={forskuddstrekk?.frikortbeloep} />
												<TitleValue
													title="Tabelltype"
													value={showKodeverkLabel('TABELLTYPE', forskuddstrekk?.tabelltype)}
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
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
