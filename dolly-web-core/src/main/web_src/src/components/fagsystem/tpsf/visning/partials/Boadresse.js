import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

export const Boadresse = ({ boadresse, visKunAdresse }) => {
	if (!boadresse || boadresse.length < 1) return false

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
		flyttedato,
		validAdresse
	} = boadresse[0]

	const matrikkelVisning = (
		<div>
			{mellomnavn && <span>{`${mellomnavn}, `}</span>}
			<span>{`${gardsnr}`}</span>
			<span>{`/${bruksnr}`}</span>
			{festenr && <span>{`/${festenr}`}</span>}
			{undernr && <span>{`-${undernr}`}</span>}
		</div>
	)

	const adresseVisning = (
		<TitleValue title={Formatters.adressetypeToString(adressetype)} size="medium">
			{adressetype === 'GATE' && (
				<div>
					{!validAdresse ? <div>{gateadresse}</div> : <div>{`${gateadresse} ${husnummer}`}</div>}
				</div>
			)}
			{adressetype === 'MATR' && matrikkelVisning}
			<KodeverkConnector navn="Postnummer" value={postnr}>
				{(v, verdi) => <span>{verdi ? verdi.label : postnr}</span>}
			</KodeverkConnector>
		</TitleValue>
	)

	return (
		<div>
			{visKunAdresse ? (
				adresseVisning
			) : (
				<div>
					<SubOverskrift label="Boadresse" iconKind="adresse" />
					<div className="person-visning_content">
						{adresseVisning}
						<TitleValue title="Flyttedato" value={Formatters.formatDate(flyttedato)} />
					</div>{' '}
				</div>
			)}
		</div>
	)
}
