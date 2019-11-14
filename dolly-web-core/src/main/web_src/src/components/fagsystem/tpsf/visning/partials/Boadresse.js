import React from 'react'
import Formatters from '~/utils/DataFormatter'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'

const Boadresse = ({ boadresse }) => {
	if (!boadresse) return false

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

	const matrikkelVisning = (
		<div>
			{mellomnavn && <span>{`${mellomnavn}, `}</span>}
			<span>{`${gardsnr}`}</span>
			<span>{`/${bruksnr}`}</span>
			{festenr && <span>{`/${festenr}`}</span>}
			{undernr && <span>{`-${undernr}`}</span>}
		</div>
	)

	return (
		<div className="person-details-block">
			<h3>Bostedadresse</h3>
			<div className="person-info-block">
				<div className="person-info-content">
					<h4>{Formatters.adressetypeToString(adressetype)}</h4>
					{adressetype === 'GATE' && <div>{`${gateadresse} ${husnummer}\n`}</div>}
					{adressetype === 'MATR' && matrikkelVisning}
					<KodeverkValueConnector apiKodeverkId="Postnummer" value={postnr} />
				</div>
				<div className="person-info-content">
					<h4>Flyttedato</h4>
					<span>{Formatters.formatDate(flyttedato)}</span>
				</div>
			</div>
		</div>
	)
}

export default Boadresse
