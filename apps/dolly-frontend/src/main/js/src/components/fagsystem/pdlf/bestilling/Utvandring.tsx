import { UtvandringValues } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { formatDate } from '@/utils/DataFormatter'

type UtvandringTypes = {
	utvandringListe: Array<UtvandringValues>
}

export const Utvandring = ({ utvandringListe }: UtvandringTypes) => {
	if (!utvandringListe || utvandringListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Utvandring</BestillingTitle>
				<DollyFieldArray header="Utvandring" data={utvandringListe}>
					{(utvandring: UtvandringValues, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Tilflyttingsland"
									value={utvandring.tilflyttingsland}
									kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								/>
								<TitleValue title="Tilflyttingssted" value={utvandring.tilflyttingsstedIUtlandet} />
								<TitleValue
									title="Utvandringsdato"
									value={formatDate(utvandring.utflyttingsdato)}
								/>
								<TitleValue title="Master" value={utvandring.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
