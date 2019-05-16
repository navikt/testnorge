import Formatters from '~/utils/DataFormatter'

export function mapSigrunData(sigrunData) {
	if (!sigrunData || sigrunData.length === 0) return null
	return {
		header: 'Inntekter',
		multiple: true,
		data: sigrunData.map((data, i) => {
			return {
				parent: 'inntekter',
				id: data.personidentifikator,
				value: [
					{
						id: 'id',
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						id: 'aar',
						label: 'År',
						value: data.inntektsaar
					},
					{
						id: 'verdi',
						label: 'Beløp',
						value: data.verdi
					},
					,
					{
						id: 'tjeneste',
						label: 'Tjeneste',
						width: 'medium',
						value: data.tjeneste
					},

					{
						id: 'grunnlag',
						label: 'Grunnlag',
						width: 'xlarge',
						value: Formatters.camelCaseToLabel(data.grunnlag)
					}
				]
			}
		})
	}
}

export function mapKrrData(krrData) {
	if (!krrData) return null

	return {
		header: 'Kontaktinformasjon og reservasjon',
		data: [
			{
				id: 'mobil',
				label: 'Mobilnummer',
				value: krrData.mobil
			},
			{
				id: 'epost',
				label: 'Epost',
				value: krrData.epost
			},
			{
				id: 'reservert',
				label: 'Reservert mot digitalkommunikasjon',
				value: krrData.reservert ? 'JA' : 'NEI'
			}
		]
	}
}

export function mapAaregData(aaregData) {
	if (!aaregData) return null

	return {
		header: 'Arbeidsforhold',
		multiple: true,
		data: aaregData.map((data, i) => {
			return {
				parent: 'arbeidsforhold',
				id: data.arbeidsforholdId,
				label: data.inntektsaar,
				value: [
					{
						id: 'id',
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						id: 'yrke',
						label: 'Yrke',
						value: data.arbeidsavtaler[0].yrke,
						apiKodeverkId: 'Yrker'
					},
					{
						id: 'startdato',
						label: 'Startdato',
						value: data.ansettelsesperiode.periode.fom
					},
					{
						id: 'sluttdato',
						label: 'Sluttdato',
						value: data.ansettelsesperiode.periode.tom
					},
					{
						id: 'stillingprosent',
						label: 'Stillingprosent',
						value: data.arbeidsavtaler[0].stillingsprosent
					},
					{
						id: 'typearbeidsgiver',
						label: 'Type av arbeidsgiver',
						value: data.arbeidsgiver.type
					},

					{
						id: 'orgnr',
						label: 'Orgnummer',
						value: data.arbeidsgiver.organisasjonsnummer
					},
					{
						id: 'orgnr',
						label: 'Arbeidsgiver Ident',
						value: data.arbeidsgiver.offentligIdent
					}
				]
			}
		})
	}
}
