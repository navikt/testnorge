import React from 'react'
import {
	DoedfoedtBarn,
	ForeldreBarnRelasjon,
	Metadata,
	Sivilstand,
	Vergemaal,
} from '~/components/fagsystem/pdlf/PdlTypes'

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
	foedsel: Array<Foedsel>
	bostedsadresse: Array<BostedData>
	oppholdsadresse: Array<Oppholdsadresse>
	kontaktadresse: Array<Kontaktadresse>
	adressebeskyttelse: Array<AdressebeskyttelseData>
	fullmakt: [FullmaktData]
	telefonnummer: Array<TelefonData>
	vergemaalEllerFremtidsfullmakt: Array<Vergemaal>
	tilrettelagtKommunikasjon: Array<TilrettelagtKommunikasjonData>
	sikkerhetstiltak: [SikkerhetstiltakData]
	sivilstand: Array<Sivilstand>
	forelderBarnRelasjon: Array<ForeldreBarnRelasjon>
	doedfoedtBarn: Array<DoedfoedtBarn>
	kontaktinformasjonForDoedsbo: Array<{}>
	statsborgerskap: [Statsborgerskap]
	innflyttingTilNorge: [InnflyttingTilNorge]
	utflyttingFraNorge: [UtflyttingFraNorge]
	doedsfall: [Doedsfall]
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
type AdressebeskyttelseData = {
	gradering: string
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

type Foedsel = {
	foedselsaar: string
	foedselsdato: string
	foedeland: string
	foedested: string
	foedekommune: string
	metadata: Metadata
}

type Doedsfall = {
	doedsdato: string
	metadata: Metadata
}

export type InnvandringValues = {
	fraflyttingsland: string
	fraflyttingsstedIUtlandet: string
	innflyttingsdato: string
}

export type UtvandringValues = {
	tilflyttingsland: string
	tilflyttingsstedIUtlandet: string
	utflyttingsdato: string
}
