import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { formatDate } from '@/utils/DataFormatter'
import { InnvandringValues } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type InnvandringTypes = {
	innvandringListe: Array<InnvandringValues>
}

export const Innvandring = ({ innvandringListe }: InnvandringTypes) => {
	if (!innvandringListe || innvandringListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Innvandring</BestillingTitle>
				<DollyFieldArray header="Innvandring" data={innvandringListe}>
					{(innvandring: InnvandringValues, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Fraflyttingsland"
									value={innvandring.fraflyttingsland}
									kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								/>
								<TitleValue
									title="Fraflyttingssted"
									value={innvandring.fraflyttingsstedIUtlandet}
								/>
								<TitleValue
									title="Fraflyttingsdato"
									value={formatDate(innvandring.innflyttingsdato)}
								/>
								<TitleValue title="Master" value={innvandring.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
