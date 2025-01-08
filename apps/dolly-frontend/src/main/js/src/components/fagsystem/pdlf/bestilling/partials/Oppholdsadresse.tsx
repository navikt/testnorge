import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle, EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { OppholdsadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { uppercaseAndUnderscoreToCapitalized } from '@/utils/DataFormatter'
import { Vegadresse } from '@/components/fagsystem/pdlf/bestilling/partials/Vegadresse'
import { Adresseinfo } from '@/components/fagsystem/pdlf/bestilling/partials/Adresseinfo'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/bestilling/partials/Matrikkeladresse'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { UtenlandskAdresse } from '@/components/fagsystem/pdlf/bestilling/partials/UtenlandskAdresse'

type OppholdsadresseTypes = {
	oppholdsadresseListe: Array<OppholdsadresseData>
}

const getHeader = (oppholdsadresse: OppholdsadresseData) => {
	return uppercaseAndUnderscoreToCapitalized(oppholdsadresse?.adressetype) || 'Adresse'
}
export const Oppholdsadresse = ({ oppholdsadresseListe }: OppholdsadresseTypes) => {
	if (!oppholdsadresseListe || oppholdsadresseListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Oppholdsadresse</BestillingTitle>
				<DollyFieldArray header="Adresse" getHeader={getHeader} data={oppholdsadresseListe}>
					{(oppholdsadresse: OppholdsadresseData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(oppholdsadresse, ['kilde', 'master']) ? (
									<EmptyObject />
								) : (
									<>
										<Vegadresse vegadresse={oppholdsadresse.vegadresse} />
										<Matrikkeladresse matrikkeladresse={oppholdsadresse.matrikkeladresse} />
										<UtenlandskAdresse utenlandskAdresse={oppholdsadresse.utenlandskAdresse} />
										<Adresseinfo adresseinfo={oppholdsadresse} />
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
