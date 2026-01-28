import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	BestillingTitle,
	EmptyObject,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { FoedselData } from '@/components/fagsystem/pdlf/PdlTypes'

type FoedselTypes = {
	foedselListe: Array<FoedselData>
}

export const Foedsel = ({ foedselListe }: FoedselTypes) => {
	if (!foedselListe || foedselListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Fødsel</BestillingTitle>
				<DollyFieldArray header="Fødsel" data={foedselListe}>
					{(foedsel: FoedselData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(foedsel, ['kilde', 'master']) ? (
									<EmptyObject />
								) : (
									<>
										<TitleValue title="Fødselsdato" value={formatDate(foedsel.foedselsdato)} />
										<TitleValue title="Fødselsår" value={foedsel.foedselsaar} />
										<TitleValue title="Fødested" value={foedsel.foedested} />
										<TitleValue
											title="Fødekommune"
											value={foedsel.foedekommune}
											kodeverk={AdresseKodeverk.Kommunenummer}
										/>
										<TitleValue
											title="Fødeland"
											value={foedsel.foedeland}
											kodeverk={AdresseKodeverk.StatsborgerskapLand}
										/>
										<TitleValue title="Master" value={foedsel.master} />
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
