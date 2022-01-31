import React from 'react'

export type PdlDataWrapper = {
	data: PdlData
}

export type PdlData = {
	hentIdenter: { identer: [Ident] }
	hentPerson: HentPerson
	hentGeografiskTilknytning: object
}

export type Ident = {
	gruppe: string
	ident: string
	historisk: boolean
}

export type HentPerson = {
	bostedsadresse: Array<BostedData>
	oppholdsadresse: Array<Oppholdsadresse>
	kontaktadresse: Array<Kontaktadresse>
	adressebeskyttelse: Array<{}>
	fullmakt: [FullmaktData]
	telefonnummer: Array<TelefonData>
	tilrettelagtKommunikasjon: Array<TilrettelagtKommunikasjonData>
	sikkerhetstiltak: [SikkerhetstiltakData]
	forelderBarnRelasjon: [ForelderBarnRelasjon]
	sivilstand: [Sivilstand]
	doedfoedtBarn: [DoedfoedtBarn]
	statsborgerskap: [Statsborgerskap]
	innflyttingTilNorge: [InnflyttingTilNorge]
	utflyttingFraNorge: [UtflyttingFraNorge]
}

export type BostedData = {
	angittFlyttedato?: Date
	coAdressenavn?: string
	gyldigFraOgMed?: Date
	gyldigTilOgMed?: Date
	vegadresse?: Vegadresse
	matrikkeladresse?: Matrikkeladresse
	ukjentBosted?: UkjentBosted
	utenlandskAdresse?: UtenlandskAdresse
	metadata?: Metadata
}

export type Oppholdsadresse = {
	utenlandskAdresse?: UtenlandskAdresse
	vegadresse?: Vegadresse
	matrikkeladresse?: Matrikkeladresse
	oppholdAnnetSted?: string
	coAdressenavn?: string
	gyldigFraOgMe?: Date
	metadata?: Metadata
}

export type Kontaktadresse = {
	gyldigFraOgMed?: Date
	gyldigTilOgMed?: Date
	type?: string
	coAdressenavn?: string
	postboksadresse?: Postboksadresse
	vegadresse?: Vegadresse
	postadresseIFrittFormat?: PostadresseIFrittFormat
	utenlandskAdresse?: UtenlandskAdresse
	utenlandskAdresseIFrittFormat?: UtenlandskAdresseIFrittFormat
	metadata?: Metadata
}

type Postboksadresse = {
	postbokseier?: string
	postboks?: string
	postnummer?: string
}

type PostadresseIFrittFormat = {
	adresselinje1?: string
	adresselinje2?: string
	adresselinje3?: string
	postnummer?: string
}

type UtenlandskAdresseIFrittFormat = {
	adresselinje1?: string
	adresselinje2?: string
	adresselinje3?: string
	postkode?: string
	byEllerStedsnavn?: string
	landkode?: string
}

type Vegadresse = {
	matrikkelId?: string
	husbokstav?: string
	husnummer?: string
	adressenavn?: string
	bruksenhetsnummer?: string
	tilleggsnavn?: string
	postnummer?: string
	kommunenummer?: string
	koordinater?: Koordinater
}

type Matrikkeladresse = {
	matrikkelId?: string
	bruksenhetsnummer?: string
	tilleggsnavn?: string
	postnummer?: string
	kommunenummer?: string
	koordinater?: Koordinater
}

type Koordinater = {
	x?: string
	y?: string
	z?: string
	kvalitet?: string
}

type UkjentBosted = {
	bostedskommune?: string
}

type UtenlandskAdresse = {
	adressenavnNummer?: string
	postboksNummerNavn?: string
	postkode?: string
	bySted?: string
	landkode?: string
	bygningEtasjeLeilighet?: string
	regionDistriktOmraade?: string
}

export type FullmaktData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	motpartsPersonident: string
	motpartsRolle: string
	omraader: Array<string>
	id: number
}

export type UtenlandskAdresseData = {
	utenlandskAdresse?: UtenlandskAdresse
}

export type TelefonData = {
	landskode: string
	nummer: string
	prioritet: number
}

export type TilrettelagtKommunikasjonData = {
	talespraaktolk: { spraak: string }
	tegnspraaktolk: { spraak: string }
	spraakForTaletolk: string
	spraakForTegnspraakTolk: string
}

export type SikkerhetstiltakData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	tiltakstype: string
	beskrivelse: string
	kontaktperson: Kontaktperson
	omraader: []
}

export type Kontaktperson = {
	personident: string
	enhet: string
}

export type Relasjon = {
	relasjonType: string
	relatertPerson: {
		ident: string
		navn: Array<{
			fornavn: string
			mellomnavn?: string
			etternavn: string
		}>
		kjoenn: Array<{
			kjoenn: string
		}>
	}
}

export type ForelderBarnRelasjon = {
	relatertPersonsIdent: string
	relatertPersonsRolle: Rolle
	minRolleForPerson: Rolle
}

export enum Rolle {
	BARN = 'BARN',
	MOR = 'MOR',
	FAR = 'FAR',
	FORELDER = 'FORELDER',
	MEDMOR = 'MEDMOR',
}

export type Sivilstand = {
	type: string
	gyldigFraOgMed: Date
	relatertVedSivilstand: string
	bekreftelsesdato: Date
	metadata: Metadata
}

export type DoedfoedtBarn = {
	dato: Date
}

export type Kodeverk = {
	values: Array<Array<KodeverkValues>>
}

export type KodeverkValues = {
	data: string
	label: string
	value: string
}

export type Statsborgerskap = {
	land: string
	gyldigFraOgMed: Date
	gyldigTilOgMed?: Date
	metadata: Metadata
}

export type InnflyttingTilNorge = {
	fraflyttingsland: string
	fraflyttingsstedIUtlandet: string
	metadata: Metadata
}

export type UtflyttingFraNorge = {
	tilflyttingsland: string
	tilflyttingsstedIUtlandet: string
	metadata: Metadata
}

type Metadata = {
	historisk: boolean
}
