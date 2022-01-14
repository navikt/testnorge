import React from 'react'
import Formatters from '~/utils/DataFormatter'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { isArray, isEmpty } from 'lodash'

type AlleMidlertidigeAdresser = {
	midlertidigAdresse: Array<MidlertidigAdresse>
}

type Enkeltadresse = {
	midlertidigAdresse: MidlertidigAdresse
}

type MidlertidigAdresse = {
	adressetype: string
	gyldigTom: string
	gatekode?: string
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

type PostnummerKodeverk = {
	values: Array<Array<Postnummer>>
}

type Postnummer = {
	data: string
	label: string
	value: string
}

export const Adressevisning = ({ midlertidigAdresse }: Enkeltadresse) => {
	const {
		adressetype,
		gyldigTom,
		gatekode,
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
		tilleggsadresse,
	} = midlertidigAdresse

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
		<ErrorBoundary>
			<>
				{adressetype && (
					<TitleValue title={Formatters.adressetypeToString(adressetype)} size="medium">
						{adressetype === 'GATE' && gate}
						{adressetype === 'STED' && sted}
						{adressetype === 'PBOX' && postboks}
						{adressetype === 'UTAD' && utland}
						{postnr && (
							<KodeverkConnector navn="Postnummer" value={postnr}>
								{(v: PostnummerKodeverk, verdi: Postnummer) => (
									<span>{verdi ? verdi.label : postnr}</span>
								)}
							</KodeverkConnector>
						)}
					</TitleValue>
				)}
				{!adressetype && (
					<div className="person-visning_content">
						<TitleValue title="Gatekode" value={gatekode} />
						<TitleValue title="Gatenavn" value={gatenavn} />
						<TitleValue title="Husnr" value={husnr} />
						<TitleValue title="Postboksanlegg" value={postboksAnlegg} />
						<TitleValue title="Postboksnummer" value={postboksnr} />
						<TitleValue title="Postnr" value={postnr} />
					</div>
				)}
				<TitleValue title="Gyldig t.o.m." value={Formatters.formatDate(gyldigTom)} />
				<TitleValue title="Tilleggsadresse" value={tilleggsadresse} />
			</>
		</ErrorBoundary>
	)
}

export const MidlertidigAdresse = ({ midlertidigAdresse }: AlleMidlertidigeAdresser) => {
	if (!midlertidigAdresse || midlertidigAdresse.length < 1 || isEmpty(midlertidigAdresse))
		return null

	return (
		<ErrorBoundary>
			<>
				<SubOverskrift label="Midlertidig adresse" iconKind="midlertidigAdresse" />
				<div className="person-visning_content">
					{isArray(midlertidigAdresse) ? (
						<Historikk
							component={Adressevisning}
							propName="midlertidigAdresse"
							data={midlertidigAdresse}
						/>
					) : (
						<Adressevisning midlertidigAdresse={midlertidigAdresse} />
					)}
				</div>
			</>
		</ErrorBoundary>
	)
}
