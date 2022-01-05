import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import { UtenlandskAdresseData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type DataListe = {
	data: Array<Data>
}

type Data = {
	enhet: UtenlandskAdresseData
	idx: number
}

export const UtenlandskBoadresse = ({ data }: DataListe) => {
	if (!data || data.length === 0) return false

	const UtenlandskBoadresseVisning = ({ enhet, idx }: Data) => {
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
			<div className="person-visning_content" key={idx}>
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
						{(enhet: UtenlandskAdresseData, idx: number) => (
							<UtenlandskBoadresseVisning enhet={enhet} idx={idx} />
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
