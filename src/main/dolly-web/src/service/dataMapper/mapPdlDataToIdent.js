import Formatters from '~/utils/DataFormatter'

export function mapPdlfData(pdlfData) {
	if (!pdlfData || pdlfData.length === 0) return null

	// Når utenlandsid skal inn må returverdi endres
	if (pdlfData.kontaktinformasjonForDoedsbo) {
		const adressatType = Object.keys(pdlfData.kontaktinformasjonForDoedsbo[0].adressat)[0]
		const navnType = pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType].navn
			? 'navn'
			: 'kontaktperson'
		const data = {
			header: 'Kontaktinformasjon for dødsbo',
			data: [
				{
					id: 'fornavn',
					label: 'Fornavn',
					value:
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][navnType] &&
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][
							navnType
						].fornavn.toUpperCase()
				},
				{
					id: 'mellomnavn',
					label: 'Mellomnavn',
					value:
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][navnType] &&
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][navnType].mellomnavn &&
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][
							navnType
						].mellomnavn.toUpperCase()
				},
				{
					id: 'etternavn',
					label: 'Etternavn',
					value:
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][navnType] &&
						pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType][
							navnType
						].etternavn.toUpperCase()
				},
				{
					id: 'foedselsdato',
					label: 'Fødselsdato',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType].foedselsdato
				},
				{
					id: 'idNummer',
					label: 'Fnr/dnr',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType].idNummer
				},
				{
					id: 'organisasjonsnavn',
					label: 'Organisasjonsnavn',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType].organisasjonsnavn
				},
				{
					id: 'organisasjonsnummer',
					label: 'Organisasjonsnummer',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].adressat[adressatType].organisasjonsnummer
				},
				{
					id: 'adresselinje1',
					label: 'Adresselinje 1',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].adresselinje1
				},
				{
					id: 'adresselinje2',
					label: 'Adresselinje 2',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].adresselinje2
				},
				{
					id: 'postnummer',
					label: 'Postnummer og -sted',
					value:
						pdlfData.kontaktinformasjonForDoedsbo[0].postnummer +
						' ' +
						pdlfData.kontaktinformasjonForDoedsbo[0].poststedsnavn
				},
				{
					id: 'landkode',
					label: 'Land',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].landkode,
					apiKodeverkId: 'Landkoder'
				},
				{
					id: 'skifteform',
					label: 'Skifteform',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].skifteform
				},
				{
					id: 'utstedtDato',
					label: 'Dato utstedt',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].utstedtDato
				},
				{
					id: 'gyldigFom',
					label: 'Gyldig fra',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].gyldigFom
				},
				{
					id: 'gyldigTom',
					label: 'Gyldig til',
					value: pdlfData.kontaktinformasjonForDoedsbo[0].gyldigTom
				}
			]
		}
		return data
	}
}
