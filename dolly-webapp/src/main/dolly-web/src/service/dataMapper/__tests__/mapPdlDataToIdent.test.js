import { mapPdlData } from '../mapPdlDataToIdent'

describe('mapPdlDataToIdent.js', () => {
	describe('mapPdlData', () => {
		it('should return null without data', () => {
			expect(mapPdlData()).toBeNull()
		})
	})

	const testDoedsboData = {
		kontaktinformasjonForDoedsbo: [
			{
				adressat: {
					organisasjonSomAdressat: {
						kontaktperson: {
							fornavn: 'Inga',
							etternavn: 'Molnes'
						},
						organisasjonsnavn: 'Nettbuss'
					}
				},
				adresselinje1: 'testGrunnlag',
				postnummer: '1234',
				poststedsnavn: 'Oslo',
				landkode: 'NOR'
			}
		]
	}

	it('should return doedsbo-data with organisasjonsnavn', () => {
		const testRes = {
			header: 'Kontaktinformasjon for dødsbo',
			data: [
				{
					id: 'fornavn',
					label: 'Fornavn',
					value: 'INGA'
				},
				{
					id: 'mellomnavn',
					label: 'Mellomnavn',
					value: undefined
				},
				{
					id: 'etternavn',
					label: 'Etternavn',
					value: 'MOLNES'
				},
				{
					id: 'foedselsdato',
					label: 'Fødselsdato',
					value: undefined
				},
				{
					id: 'idNummer',
					label: 'Fnr/dnr/BOST',
					value: undefined
				},
				{
					id: 'organisasjonsnavn',
					label: 'Organisasjonsnavn',
					value: 'Nettbuss'
				},
				{
					id: 'organisasjonsnummer',
					label: 'Organisasjonsnummer',
					value: undefined
				},
				{
					id: 'adresselinje1',
					label: 'Adresselinje 1',
					value: 'testGrunnlag'
				},
				{
					id: 'adresselinje2',
					label: 'Adresselinje 2',
					value: undefined
				},
				{
					id: 'postnummer',
					label: 'Postnummer og -sted',
					value: '1234 Oslo'
				},
				{
					id: 'landkode',
					label: 'Land',
					value: 'NOR'
				},
				{
					id: 'skifteform',
					label: 'Skifteform',
					value: undefined
				},
				{
					id: 'utstedtDato',
					label: 'Dato utstedt',
					value: undefined
				},
				{
					id: 'gyldigFom',
					label: 'Gyldig fra',
					value: undefined
				},
				{
					id: 'gyldigTom',
					label: 'Gyldig til',
					value: undefined
				}
			]
		}
		expect(mapPdlData(testDoedsboData)).toMatchObject([testRes])
	})

	const testFalskIdData = {
		falskIdentitet: {
			rettIdentitetErUkjent: true
		}
	}

	it('should return falsk identitet with ukjent rett identitet', () => {
		const falskIdData = {
			header: 'Falsk identitet',
			data: [
				{
					id: 'rettIdentitetErUkjent',
					label: 'Rett identitet',
					value: 'UKJENT'
				},
				{
					id: 'rettIdentitetVedIdentifikasjonsnummer',
					label: 'Rett identitet',
					value: undefined
				},
				{
					id: 'identitetsnummer',
					label: 'Rett fnr/dnr/bnr',
					value: undefined
				},
				{
					id: 'fornavn',
					label: 'Fornavn',
					value: undefined
				},
				{
					id: 'mellomnavn',
					label: 'Mellomnavn',
					value: undefined
				},
				{
					id: 'etternavn',
					label: 'Etternavn',
					value: undefined
				},
				{
					id: 'foedselsdato',
					label: 'Fødselsdato',
					value: undefined
				},
				{
					id: 'statsborgerskap',
					label: 'Statsborgerskap',
					width: 'medium',
					apiKodeverkId: 'StatsborgerskapFreg',
					value: undefined
				}
			]
		}
		expect(mapPdlData(testFalskIdData)).toMatchObject([falskIdData])
	})
})
