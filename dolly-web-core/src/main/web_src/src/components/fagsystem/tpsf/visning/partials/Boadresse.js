import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

export const Boadresse = ({ boadresse }) => {
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
		<div>
			<SubOverskrift label="Boadresse" />
			<div className="person-visning_content">
				<TitleValue title={Formatters.adressetypeToString(adressetype)} size="medium">
					{adressetype === 'GATE' && <div>{`${gateadresse} ${husnummer}`}</div>}
					{adressetype === 'MATR' && matrikkelVisning}
					<KodeverkConnector navn="Postnummer" value={postnr}>
						{(v, verdi) => <span>{verdi ? verdi.label : postnr}</span>}
					</KodeverkConnector>
				</TitleValue>
				<TitleValue title="Flyttedato" value={Formatters.formatDate(flyttedato)} />
			</div>
		</div>
	)
}
