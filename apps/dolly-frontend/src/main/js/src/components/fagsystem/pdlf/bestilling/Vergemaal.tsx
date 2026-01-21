import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { VergemaalKodeverk } from '@/config/kodeverk'
import { TjenesteomraadeValues, VergemaalValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { arrayToString, codeToNorskLabel, formatDate } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import * as _ from 'lodash-es'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'

type VergemaalTypes = {
	vergemaalListe: Array<VergemaalValues>
}

export const Vergemaal = ({ vergemaalListe }: VergemaalTypes) => {
	if (!vergemaalListe || vergemaalListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Vergemål</BestillingTitle>
				<DollyFieldArray header="Vergemål" data={vergemaalListe}>
					{(vergemaal: VergemaalValues, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Fylkesmannsembete"
									value={vergemaal.vergemaalEmbete}
									kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
								/>
								<TitleValue
									title="Sakstype"
									value={vergemaal.sakType}
									kodeverk={VergemaalKodeverk.Sakstype}
								/>
								<TitleValue
									title="Mandattype"
									value={vergemaal.mandatType}
									kodeverk={VergemaalKodeverk.Mandattype}
								/>
								<TitleValue title="Gyldig f.o.m." value={formatDate(vergemaal.gyldigFraOgMed)} />
								<TitleValue title="Gyldig t.o.m." value={formatDate(vergemaal.gyldigTilOgMed)} />
								<TitleValue title="Verge" value={vergemaal.vergeIdent} />
								<TitleValue title="Master" value={vergemaal.master} />
								<DollyFieldArray header="Tjenesteområde" data={vergemaal.tjenesteomraade} nested>
									{(tjenesteomraade: TjenesteomraadeValues, idy: number) => {
										return (
											<React.Fragment key={idy}>
												<TitleValue
													title="Tjenestevirksomhet"
													value={tjenesteomraade.tjenestevirksomhet}
												/>
												<TitleValue
													title="Tjenesteområde"
													value={arrayToString(
														tjenesteomraade.tjenesteoppgave?.map((oppgave: string) =>
															codeToNorskLabel(oppgave),
														),
														', ',
													)}
												/>
											</React.Fragment>
										)
									}}
								</DollyFieldArray>
								<EkspanderbarVisning vis={_.get(vergemaal, 'nyVergeIdent')} header="VERGE">
									<RelatertPerson personData={vergemaal.nyVergeIdent} tittel="Verge" />
								</EkspanderbarVisning>
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
