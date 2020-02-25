import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const Adressevisning = ({ boadresse }) => {
	const {
		adressetype,
		gateadresse,
		husnummer,
		mellomnavn,
		gardsnr,
		bruksnr,
		festenr,
		undernr,
		postnr,
		flyttedato
	} = boadresse

	const gate = <div>{`${gateadresse} ${husnummer}`}</div>

	const matrikkel = (
		<div>
			{mellomnavn && <span>{`${mellomnavn}, `}</span>}
			<span>{`${gardsnr}`}</span>
			<span>{`/${bruksnr}`}</span>
			{festenr && <span>{`/${festenr}`}</span>}
			{undernr && <span>{`-${undernr}`}</span>}
		</div>
	)

	if (gateadresse === 'UTEN FAST BOSTED') {
		return (
			<TitleValue title="Bosted" size="medium">
				Uten fast bosted
			</TitleValue>
		)
	}

	return (
		<React.Fragment>
			<TitleValue title={Formatters.adressetypeToString(adressetype)} size="medium">
				{adressetype === 'GATE' && gate}
				{adressetype === 'MATR' && matrikkel}
				{postnr && (
					<KodeverkConnector navn="Postnummer" value={postnr}>
						{(v, verdi) => <span>{verdi ? verdi.label : postnr}</span>}
					</KodeverkConnector>
				)}
			</TitleValue>
			<TitleValue title="Flyttedato" value={Formatters.formatDate(flyttedato)} />
		</React.Fragment>
	)
}

// ;<Historikk component={Adressevisning} boadresse={boadresse} />

export const Boadresse = ({ boadresse }) => {
	if (!boadresse) return false

	return (
		<React.Fragment>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<Historikk component={Adressevisning} propName="boadresse" data={boadresse} />
				{/* <Adressevisning boadresse={boadresse} /> */}
			</div>
		</React.Fragment>
	)
}
