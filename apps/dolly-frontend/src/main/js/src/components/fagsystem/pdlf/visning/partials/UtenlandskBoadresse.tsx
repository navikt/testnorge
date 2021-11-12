import React, { Fragment } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Personnavn } from '~/components/fagsystem/pdlf/visning/partials/Personnavn'
import Formatters from '~/utils/DataFormatter'
import { AdresseKodeverk } from '~/config/kodeverk'

export const UtenlandskBoadresse = ({ data }) => {
	console.log('data', data)
	if (!data || data.length === 0) return false

	const UtenlandskBoadresseVisning = ({ enhet, id }) => {
		if (!enhet.utenlandskAdresse) return null
		const {
			adressenavnNummer,
			postboksNummerNavn,
			postkode,
			bySted,
			landkode,
			bygningEtasjeLeilighet,
			regionDistriktOmraade,
		} = enhet.utenlandskAdresse

		return (
			<div className="person-visning_content" key={id}>
				<TitleValue title="Gatenavn og husnummer" value={adressenavnNummer} />
				<TitleValue title="Postboksnummer og -navn" value={postboksNummerNavn} />
				<TitleValue title="Postkode" value={postkode} />
				<TitleValue title="By eller sted" value={bySted} />
				<TitleValue title="Land" value={landkode} kodeverk={AdresseKodeverk.StatsborgerskapLand} />
				<TitleValue title="Bygg-/leilighetsinfo" value={bygningEtasjeLeilighet} />
				<TitleValue title="Region/distrikt/område" value={regionDistriktOmraade} />
			</div>
		)
	}

	return (
		<div>
			<SubOverskrift label="Utenlandsk boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(enhet, idx) => <UtenlandskBoadresseVisning enhet={enhet} id={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
