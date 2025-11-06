import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ForeldreansvarData } from '@/components/fagsystem/pdlf/PdlTypes'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'
import _get from 'lodash/get'

type ForeldreansvarTypes = {
	foreldreansvarListe: Array<ForeldreansvarData>
}

export const Foreldreansvar = ({ foreldreansvarListe }: ForeldreansvarTypes) => {
	if (!foreldreansvarListe || foreldreansvarListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Foreldreansvar</BestillingTitle>
				<DollyFieldArray header="Foreldreansvar" data={foreldreansvarListe}>
					{(foreldreansvar: ForeldreansvarData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Hvem har ansvaret"
									value={showLabel('foreldreansvar', foreldreansvar.ansvar)}
								/>
								<TitleValue
									title="Gyldig f.o.m."
									value={formatDate(foreldreansvar.gyldigFraOgMed)}
								/>
								<TitleValue
									title="Gyldig t.o.m."
									value={formatDate(foreldreansvar.gyldigTilOgMed)}
								/>
								<TitleValue
									title="Type ansvarlig"
									value={
										(foreldreansvar.ansvarlig && 'Eksisterende person') ||
										(foreldreansvar.nyAnsvarlig && 'Ny person') ||
										(foreldreansvar.ansvarligUtenIdentifikator && 'Person uten identifikator')
									}
								/>
								<TitleValue title="Ansvarlig" value={foreldreansvar.ansvarlig} />
								<TitleValue title="Ansvarssubjekt" value={foreldreansvar.ansvarssubjekt} />
								<TitleValue title="Master" value={foreldreansvar.master} />
								<EkspanderbarVisning
									vis={_get(foreldreansvar, 'nyAnsvarlig')}
									header={'NY ANSVARLIG'}
								>
									<RelatertPerson personData={foreldreansvar.nyAnsvarlig} tittel="Ny ansvarlig" />
								</EkspanderbarVisning>
								<EkspanderbarVisning
									vis={_get(foreldreansvar, 'ansvarligUtenIdentifikator')}
									header={'ANSVARLIG UTEN IDENTIFIKATOR'}
								>
									<RelatertPerson
										personData={foreldreansvar.ansvarligUtenIdentifikator}
										tittel="Ansvarlig uten identifikator"
									/>
								</EkspanderbarVisning>
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
