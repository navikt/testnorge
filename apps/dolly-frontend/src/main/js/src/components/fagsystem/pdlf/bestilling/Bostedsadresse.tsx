import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle, EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { BostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { uppercaseAndUnderscoreToCapitalized } from '@/utils/DataFormatter'
import { Vegadresse } from '@/components/fagsystem/pdlf/bestilling/Vegadresse'
import { Adresseinfo } from '@/components/fagsystem/pdlf/bestilling/Adresseinfo'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/bestilling/Matrikkeladresse'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { UtenlandskAdresse } from '@/components/fagsystem/pdlf/bestilling/UtenlandskAdresse'
import { UkjentBosted } from '@/components/fagsystem/pdlf/bestilling/UkjentBosted'

type BostedsadresseTypes = {
	bostedsadresseListe: Array<BostedData>
}

const getHeader = (bostedsadresse: BostedData) => {
	return uppercaseAndUnderscoreToCapitalized(bostedsadresse?.adressetype) || 'Adresse'
}
export const Bostedsadresse = ({ bostedsadresseListe }: BostedsadresseTypes) => {
	if (!bostedsadresseListe || bostedsadresseListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Bostedsadresse</BestillingTitle>
				<DollyFieldArray header="Adresse" getHeader={getHeader} data={bostedsadresseListe}>
					{(bostedsadresse: BostedData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(bostedsadresse, ['kilde', 'master']) ? (
									<EmptyObject />
								) : (
									<>
										<Vegadresse vegadresse={bostedsadresse.vegadresse} />
										<Matrikkeladresse matrikkeladresse={bostedsadresse.matrikkeladresse} />
										<UtenlandskAdresse utenlandskAdresse={bostedsadresse.utenlandskAdresse} />
										<UkjentBosted ukjentBosted={bostedsadresse.ukjentBosted} />
										<Adresseinfo adresseinfo={bostedsadresse} />
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
