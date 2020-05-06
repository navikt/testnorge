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