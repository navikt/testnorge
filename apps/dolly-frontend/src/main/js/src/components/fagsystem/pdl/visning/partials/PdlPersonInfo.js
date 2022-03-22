import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import { getSortedSivilstand } from '~/components/fagsystem/pdl/visning/partials/utils'

export const PdlPersonInfo = ({ data, visTittel = true }) => {
	if (!data) {
		return null
	}

	const personNavn = data?.navn?.[0]
	const personKjoenn = data?.kjoenn?.[0]
	const personSivilstand = getSortedSivilstand(data?.sivilstand)?.[0]
	const personFoedsel = data?.foedsel?.[0]
	const personstatus = data?.folkeregisterPersonstatus?.[0] || data?.folkeregisterpersonstatus?.[0]

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Ident" value={data?.ident} />
					<TitleValue title="Fornavn" value={personNavn?.fornavn} />
					<TitleValue title="Mellomnavn" value={personNavn?.mellomnavn} />
					<TitleValue title="Etternavn" value={personNavn?.etternavn} />
					<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
					<TitleValue title="Sivilstand" value={personSivilstand?.type} />
					<TitleValue
						title="Fødselsdato"
						value={Formatters.formatDate(personFoedsel?.foedselsdato)}
					/>
					<TitleValue
						title="Personstatus"
						value={Formatters.allCapsToCapitalized(personstatus?.status)}
					/>
				</div>
			</div>
		</ErrorBoundary>
	)
}
