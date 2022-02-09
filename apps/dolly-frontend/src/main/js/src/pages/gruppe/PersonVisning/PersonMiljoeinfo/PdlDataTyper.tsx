import React from 'react'
import {
	DoedfoedtBarn,
	FoedselData,
	ForeldreBarnRelasjon,
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
	foedsel: Array<FoedselData>
	bostedsadresse: Array<BostedData>
	oppholdsadresse: Array<{}>
	kontaktadresse: Array<{}>
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
	opphold: Array<OppholdData>
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

export type OppholdData = {
	oppholdFra: Date
	oppholdTil: Date
	type: string
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
