
type Statsborger = {
	land: string
	fraDato?: Date
	tilDato?: Date
}

export const getBoadresse = (data: any) => {
	const type = data.matrikkelGardsnr ? 'MATR' : 'GATE'
	return [{
		adressetype: type,
		gateadresse: data.adresse,
		husnummer: data.offentligHusnr,
		mellomnavn: '',
		gardsnr: data.matrikkelGardsnr,
		bruksnr: data.matrikkelBruksnr,
		festenr: data.matrikkelFestenr,
		undernr: data.matrikkelUndernr,
		postnr: data.postnr,
		flyttedato: data.fraDato
	}]
}

const getStatsborgerskap = (data: Statsborger) => {
	return [{
		statsborgerskap: data.land,
		statsborgerskapRegdato: data.fraDato ? data.fraDato : ''
	}]
}

export const getNasjonalitet = (data: any) => {
	var innutvandret: string[];
	innutvandret = [];
	return {
		statsborgerskap: getStatsborgerskap(data.statsborger),
		innvandretUtvandret: innutvandret
	}
}

const getAlder = (datoFoedt: string) =>{
	const foedselsdato = new Date(datoFoedt)
	const diff_ms = Date.now() - foedselsdato.getTime()
	const age_dt = new Date(diff_ms)

	return Math.abs(age_dt.getUTCFullYear() - 1970)
}

export const getPersonInfo = (data: any) =>{
	const tlf1 = data.telefonPrivat.nummer ? 'privat' : 'mobil'
	return {
		identtype: data.personIdent.type,
		ident: data.personIdent.id,
		fornavn: data.navn.fornavn,
		mellomnavn: data.navn.mellomnavn,
		etternavn: data.navn.slektsnavn,
		kjonn: data.personInfo.kjoenn,
		alder: getAlder(data.personInfo.datoFoedt),
		doedsdato: data.doedshistorikk.dato,
		sivilstand: data.sivilstand.type,
		telefonnummer_1: tlf1==='privat' ? data.telefonPrivat.nummer : data.telefonMobil.nummer,
		telefonLandskode_1: tlf1==='privat' ? data.telefonPrivat.retningslinje : data.telefonMobil.retningslinje,
		telefonnummer_2: tlf1==='mobil' ? data.telefonPrivat.nummer : data.telefonMobil.nummer,
		telefonLandskode_2: tlf1==='mobil' ? data.telefonPrivat.retningslinje : data.telefonMobil.retningslinje
	}
}