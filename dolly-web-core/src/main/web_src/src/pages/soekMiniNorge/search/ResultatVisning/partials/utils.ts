
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
	return {
		statsborgerskap: getStatsborgerskap(data.statsborger)
	}
}