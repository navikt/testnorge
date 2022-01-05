import React from 'react'
import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'

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
	fullmakt: [FullmaktData]
	telefonnummer: Array<TelefonData>
	tilrettelagtKommunikasjon: Array<TilrettelagtKommunikasjonData>
	sikkerhetstiltak: [SikkerhetstiltakData]
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
	omraader: Array<String>
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
