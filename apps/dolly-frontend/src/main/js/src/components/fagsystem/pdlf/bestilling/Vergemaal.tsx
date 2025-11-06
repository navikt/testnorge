import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { VergemaalKodeverk } from '@/config/kodeverk'
import { VergemaalValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { formatDate } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import _get from 'lodash/get'
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
								<EkspanderbarVisning vis={_get(vergemaal, 'nyVergeIdent')} header="VERGE">
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
