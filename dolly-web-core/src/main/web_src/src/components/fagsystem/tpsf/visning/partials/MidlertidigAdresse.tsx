import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

type MidlertidigeAdresser = {
	midlertidigAdresse: Array<MidlertidigAdresse>
}

type MidlertidigAdresse = {
	adressetype: string
	gyldigTom: string
	gatenavn?: string
	husnr?: string
	postnr?: string
	eiendomsnavn?: string
	postboksnr?: string
	postboksAnlegg?: string
	postLinje1?: string
	postLinje2?: string
	postLinje3?: string
	postLand?: string
	tilleggsadresse: string
}

export const MidlertidigAdresse = ({ midlertidigAdresse }: MidlertidigeAdresser) => {
	if (!midlertidigAdresse) return null

	const {
		adressetype,
		gyldigTom,
		gatenavn,
		husnr,
		postnr,
		eiendomsnavn,
		postboksnr,
		postboksAnlegg,
		postLinje1,
		postLinje2,
		postLinje3,
		postLand,
		tilleggsadresse
	} = midlertidigAdresse[0]

	const gate = <div>{`${gatenavn} ${husnr}`}</div>

	const sted = <div>{eiendomsnavn}</div>

	const postboks = <div>{`Postboks ${postboksnr} ${postboksAnlegg}`}</div>

	const utland = (
		<div>
			<div>{postLinje1}</div>
			<div>{postLinje2}</div>
			<div>{postLinje3}</div>
			<div>{postLand}</div>
		</div>
	)

	return (
		<>
			<SubOverskrift label="Midlertidig adresse" iconKind="midlertidigAdresse" />
			<div className="person-visning_content">
				<TitleValue title={Formatters.adressetypeToString(adressetype)} size="medium">
					{adressetype === 'GATE' && gate}
					{adressetype === 'STED' && sted}
					{adressetype === 'PBOX' && postboks}
					{adressetype === 'UTAD' && utland}
					{postnr && (
						<KodeverkConnector navn="Postnummer" value={postnr}>
							{(v, verdi) => <span>{verdi ? verdi.label : postnr}</span>}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue title="Gyldig t.o.m." value={Formatters.formatDate(gyldigTom)} />
				<TitleValue title="Tilleggsadresse" value={tilleggsadresse} />
			</div>
		</>
	)
}
