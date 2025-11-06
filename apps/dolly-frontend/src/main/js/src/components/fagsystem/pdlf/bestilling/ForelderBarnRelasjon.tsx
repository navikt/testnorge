import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ForeldreBarnRelasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { showLabel } from '@/utils/DataFormatter'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import { DeltBostedVisning } from '@/components/fagsystem/pdlf/bestilling/DeltBosted'
import { RelatertPerson } from '@/components/bestilling/sammendrag/visning/RelatertPerson'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import _get from 'lodash/get'

type ForelderBarnTypes = {
	forelderBarnListe: Array<ForeldreBarnRelasjon>
}

const getHeader = (forelderBarnRelasjon: ForeldreBarnRelasjon) => {
	return showLabel('pdlRelasjonTyper', forelderBarnRelasjon?.relatertPersonsRolle)
}

export const ForelderBarnRelasjon = ({ forelderBarnListe }: ForelderBarnTypes) => {
	if (!forelderBarnListe || forelderBarnListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Barn/foreldre</BestillingTitle>
				<DollyFieldArray header="Relasjon" getHeader={getHeader} data={forelderBarnListe}>
					{(forelderBarn: ForeldreBarnRelasjon, idx: number) => {
						if (
							['FORELDER', 'MOR', 'MEDMOR', 'FAR'].includes(forelderBarn.relatertPersonsRolle) &&
							isEmpty(forelderBarn, [
								'kilde',
								'master',
								'minRolleForPerson',
								'relatertPersonsRolle',
							])
						) {
							return <TitleValue title="Relasjon" value="Ingen verdier satt" />
						}

						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Rolle for barn"
									value={
										forelderBarn.relatertPersonsRolle === 'BARN' &&
										showLabel('pdlRelasjonTyper', forelderBarn.minRolleForPerson)
									}
								/>
								<TitleValue title="Bor ikke sammen" value={forelderBarn.borIkkeSammen && 'Ja'} />
								<TitleValue
									title="Partner ikke forelder"
									value={forelderBarn.partnerErIkkeForelder && 'Ja'}
								/>
								<TitleValue
									title={`Type ${forelderBarn.relatertPersonsRolle}`}
									value={showLabel('typeAnsvarlig', forelderBarn.typeForelderBarn)}
								/>
								<TitleValue
									title={forelderBarn.relatertPersonsRolle}
									value={forelderBarn.relatertPerson}
								/>
								<TitleValue title="Master" value={forelderBarn.master} />
								<EkspanderbarVisning vis={_get(forelderBarn, 'deltBosted')} header="DELT BOSTED">
									<DeltBostedVisning deltBosted={forelderBarn.deltBosted} />
								</EkspanderbarVisning>
								<EkspanderbarVisning
									vis={_get(forelderBarn, 'nyRelatertPerson')}
									header={forelderBarn.relatertPersonsRolle}
								>
									<RelatertPerson personData={forelderBarn.nyRelatertPerson} />
								</EkspanderbarVisning>
								<EkspanderbarVisning
									vis={_get(forelderBarn, 'relatertPersonUtenFolkeregisteridentifikator')}
									header={forelderBarn.relatertPersonsRolle}
								>
									<RelatertPerson
										personData={forelderBarn.relatertPersonUtenFolkeregisteridentifikator}
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
