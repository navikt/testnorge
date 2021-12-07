import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const Adressevisning = ({ boadresse }) => {
	const {
		adressetype,
		adresse,
		gateadresse,
		husnummer,
		mellomnavn,
		gardsnr,
		bruksnr,
		festenr,
		undernr,
		postnr,
		bolignr,
		flyttedato,
		tilleggsadresse,
	} = boadresse

	const gate = <div>{`${adresse ? adresse : gateadresse} ${husnummer}`}</div>

	const matrikkel = (
		<div>
			{mellomnavn && <span>{`${mellomnavn}, `}</span>}
			<span>{`${gardsnr}`}</span>
			<span>{`/${bruksnr}`}</span>
			{festenr && <span>{`/${festenr}`}</span>}
			{undernr && <span>{`-${undernr}`}</span>}
		</div>
	)

	if (adresse === 'UTEN FAST BOSTED' || gateadresse === 'UTEN FAST BOSTED') {
		return (
			<TitleValue title="Bosted" size="medium">
				Uten fast bosted
			</TitleValue>
		)
	}

	return (
		<>
			<TitleValue
				title={adressetype ? Formatters.adressetypeToString(adressetype) : 'Boadresse'}
				size="medium"
			>
				{(!adressetype || adressetype === 'GATE') && gate}
				{adressetype === 'MATR' && matrikkel}
				{postnr && (
					<KodeverkConnector navn="Postnummer" value={postnr}>
						{(v, verdi) => <span>{verdi ? verdi.label : postnr}</span>}
					</KodeverkConnector>
				)}
			</TitleValue>
			<TitleValue title="Bruksenhetsnummer" value={bolignr} />
			<TitleValue title="Flyttedato" value={Formatters.formatDate(flyttedato)} />
			<TitleValue title="Tilleggsadresse" value={tilleggsadresse} />
		</>
	)
}

export const Boadresse = ({ boadresse }) => {
	if (!boadresse || (boadresse instanceof Array && boadresse.length < 1)) return false
	return (
		<>
			<SubOverskrift label="Boadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<Historikk
					component={Adressevisning}
					propName="boadresse"
					data={boadresse instanceof Array ? boadresse : [boadresse]}
				/>
			</div>
		</>
	)
}
