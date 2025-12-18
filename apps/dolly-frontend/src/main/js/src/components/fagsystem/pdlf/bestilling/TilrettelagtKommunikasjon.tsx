import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { TilrettelagtKommunikasjonData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'

type TilrettelagtKommunikasjonTypes = {
	tilrettelagtKommunikasjonListe: Array<TilrettelagtKommunikasjonData>
}

export const TilrettelagtKommunikasjon = ({
	tilrettelagtKommunikasjonListe,
}: TilrettelagtKommunikasjonTypes) => {
	if (!tilrettelagtKommunikasjonListe || tilrettelagtKommunikasjonListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Tilrettelagt kommunikasjon</BestillingTitle>
				<DollyFieldArray header="Tolk" data={tilrettelagtKommunikasjonListe}>
					{(tilrettelagtKommunikasjon: TilrettelagtKommunikasjonData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Talespråk"
									value={tilrettelagtKommunikasjon.spraakForTaletolk}
									kodeverk={PersoninformasjonKodeverk.Spraak}
								/>
								<TitleValue
									title="Tegnspråk"
									value={tilrettelagtKommunikasjon.spraakForTegnspraakTolk}
									kodeverk={PersoninformasjonKodeverk.Spraak}
								/>
								<TitleValue title="Master" value={tilrettelagtKommunikasjon.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
