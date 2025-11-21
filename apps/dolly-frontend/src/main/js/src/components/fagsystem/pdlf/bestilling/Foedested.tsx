import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle, EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FoedestedData } from '@/components/fagsystem/pdlf/PdlTypes'

type FoedestedTypes = {
	foedestedListe: Array<FoedestedData>
}

export const Foedested = ({ foedestedListe }: FoedestedTypes) => {
	if (!foedestedListe || foedestedListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Fødested</BestillingTitle>
				<DollyFieldArray header="Fødested" data={foedestedListe}>
					{(foedested: FoedestedData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(foedested, ['kilde', 'master']) ? (
									<EmptyObject />
								) : (
									<>
										<TitleValue title="Fødested" value={foedested.foedested} />
										<TitleValue
											title="Fødekommune"
											value={foedested.foedekommune}
											kodeverk={AdresseKodeverk.Kommunenummer}
										/>
										<TitleValue
											title="Fødeland"
											value={foedested.foedeland}
											kodeverk={AdresseKodeverk.StatsborgerskapLand}
										/>
										<TitleValue title="Master" value={foedested.master} />
									</>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
