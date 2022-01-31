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
	oppholdsadresse: Array<{}>
	kontaktadresse: Array<{}>
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
	metadata: {
		historisk: boolean
	}
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
	utenlandskAdresse?: {
		adressenavnNummer?: string
		postboksNummerNavn?: string
		postkode?: string
		bySted?: string
		landkode?: string
		bygningEtasjeLeilighet?: string
		regionDistriktOmraade?: string
	}
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
	gyldigTilOgMed: Date
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
