import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle, EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { KontaktadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { uppercaseAndUnderscoreToCapitalized } from '@/utils/DataFormatter'
import { Vegadresse } from '@/components/fagsystem/pdlf/bestilling/Vegadresse'
import { Adresseinfo } from '@/components/fagsystem/pdlf/bestilling/Adresseinfo'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { UtenlandskAdresse } from '@/components/fagsystem/pdlf/bestilling/UtenlandskAdresse'
import { Postboksadresse } from '@/components/fagsystem/pdlf/bestilling/Postboksadresse'

type KontaktadresseTypes = {
	kontaktadresseListe: Array<KontaktadresseData>
}

const getHeader = (kontaktadresse: KontaktadresseData) => {
	return uppercaseAndUnderscoreToCapitalized(kontaktadresse?.adressetype) || 'Adresse'
}
export const Kontaktadresse = ({ kontaktadresseListe }: KontaktadresseTypes) => {
	if (!kontaktadresseListe || kontaktadresseListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Kontaktadresse</BestillingTitle>
				<DollyFieldArray header="Adresse" getHeader={getHeader} data={kontaktadresseListe}>
					{(kontaktadresse: KontaktadresseData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(kontaktadresse, ['kilde', 'master']) ? (
									<EmptyObject />
								) : (
									<>
										<Vegadresse vegadresse={kontaktadresse.vegadresse} />
										<UtenlandskAdresse utenlandskAdresse={kontaktadresse.utenlandskAdresse} />
										<Postboksadresse postboksadresse={kontaktadresse.postboksadresse} />
										<Adresseinfo adresseinfo={kontaktadresse} />
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
