import { Innhold, Statsborger } from '../../../hodejegeren/types'

export const getBoadresse = (data: Innhold) => {
	const boadresseData = data.boadresse
	const type = boadresseData.matrikkelGardsnr ? 'MATR' : 'GATE'
	return [
		{
			adressetype: type,
			gateadresse: boadresseData.adresse,
			husnummer: boadresseData.offentligHusnr,
			mellomnavn: '',
			gardsnr: boadresseData.matrikkelGardsnr,
			bruksnr: boadresseData.matrikkelBruksnr,
			festenr: boadresseData.matrikkelFestenr,
			undernr: boadresseData.matrikkelUndernr,
			postnr: boadresseData.postnr,
			flyttedato: boadresseData.fraDato
		}
	]
}

const getStatsborgerskap = (data: Statsborger) => {
	return [
		{
			statsborgerskap: data.land,
			statsborgerskapRegdato: data.fraDato ? data.fraDato : '',
			statsborgerskapTildato: data.tilDato ? data.tilDato : ''
		}
	]
}

export const getNasjonalitet = (data: Innhold) => {
	let innutvandret: string[]
	innutvandret = []
	return {
		statsborgerskap: getStatsborgerskap(data.statsborger),
		innvandretUtvandret: innutvandret
	}
}

const getAlder = (datoFoedt: Date) => {
	const foedselsdato = new Date(datoFoedt)
	const diff_ms = Date.now() - foedselsdato.getTime()
	const age_dt = new Date(diff_ms)

	return Math.abs(age_dt.getUTCFullYear() - 1970)
}

export const getPersonInfo = (data: Innhold) => {
	const tlf1 = data.telefonPrivat.nummer ? 'privat' : 'mobil'
	const personInfo = {
		identtype: data.personIdent.type,
		ident: data.personIdent.id,
		fornavn: data.navn.fornavn,
		mellomnavn: data.navn.mellomnavn,
		etternavn: data.navn.slektsnavn,
		kjonn: data.personInfo.kjoenn,
		alder: getAlder(data.personInfo.datoFoedt),
		doedsdato: data.doedshistorikk.dato,
		sivilstand: data.sivilstand.type,
		telefonnummer_1: tlf1 === 'privat' ? data.telefonPrivat.nummer : data.telefonMobil.nummer,
		telefonLandskode_1:
			tlf1 === 'privat' ? data.telefonPrivat.retningslinje : data.telefonMobil.retningslinje,
		telefonnummer_2: tlf1 === 'mobil' ? data.telefonPrivat.nummer : data.telefonMobil.nummer,
		telefonLandskode_2:
			tlf1 === 'mobil' ? data.telefonPrivat.retningslinje : data.telefonMobil.retningslinje,
		bankkontonr: data.giro.nummer,
		bankkontonrRegdato: data.giro.fraDato
	}

	if (!personInfo.telefonLandskode_1) personInfo.telefonLandskode_1 = ''
	if (!personInfo.telefonLandskode_2) personInfo.telefonLandskode_2 = ''
	if (personInfo.sivilstand === 'NULL') personInfo.sivilstand = ''
	return personInfo
}

export const getPostAdresse = (data: Innhold) => {
	return [
		{
			postLinje1: data.post.adresse1,
			postLinje2: data.post.adresse2,
			postLinje3: data.post.adresse3,
			postLand: data.post.postland
		}
	]
}

export const getRelasjoner = (data: Innhold) => {
	let emptyArray: string[]
	emptyArray = []
	let dollyRelasjoner = []
	for (let i = 0; i < data.relasjoner.length; i++) {
		const type = getRelasjonsType(data.relasjoner[i].rolle)
		if (type === Relasjon.MOR || type === Relasjon.FAR) continue
		dollyRelasjoner.push({
			relasjonTypeNavn: type,
			personRelasjonMed: {
				ident: data.relasjoner[i].ident,
				identtype: data.relasjoner[i].type,
				boadresse: emptyArray,
				postadresse: emptyArray,
				sivilstander: emptyArray,
				relasjoner: emptyArray,
				adoptert: 'Ukjent'
			}
		})
	}
	return dollyRelasjoner
}

enum Relasjon {
	MOR = 'MOR',
	FAR = 'FAR',
	EKTEFELLE = 'EKTEFELLE',
	PARTNER = 'PARTNER',
	BARN = 'BARN'
}

const getRelasjonsType = (rolle: string) => {
	switch (rolle) {
		case 'EKTE':
			return 'EKTEFELLE'
		case 'REPA':
			return 'PARTNER'
		case 'FARA':
			return 'FAR'
		case 'MORA':
			return 'MOR'
		default:
			return rolle
	}
}
