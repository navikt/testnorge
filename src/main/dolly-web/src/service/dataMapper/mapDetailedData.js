import { relasjonTranslator } from './Utils'
import Formatters from '~/utils/DataFormatter'

export function mapTpsfData(tpsfData, testIdent) {
	if (!tpsfData) return null
	let data
	data = [
		{
			header: 'Personlig informasjon',
			data: [
				{
					id: 'ident',
					label: tpsfData.identtype,
					value: tpsfData.ident
				},
				{
					id: 'fornavn',
					label: 'Fornavn',
					value: tpsfData.fornavn
				},
				{
					id: 'mellomnavn',
					label: 'Mellomnavn',
					value: tpsfData.mellomnavn
				},
				{
					id: 'etternavn',
					label: 'Etternavn',
					value: tpsfData.etternavn
				},
				{
					id: 'kjonn',
					label: 'Kjønn',
					value: tpsfData.kjonn
				},
				{
					id: 'alder',
					label: 'Alder',
					value: Formatters.formatAlder(tpsfData.alder, tpsfData.doedsdato)
				},
				{
					id: 'sivilstand',
					label: 'Sivilstand',
					value: tpsfData.sivilstand
				},

				{
					id: 'miljoer',
					label: 'Miljøer',
					value: Formatters.commaToSpace(testIdent.tpsfSuccessEnv)
				},
				{
					id: 'spesreg',
					label: 'Diskresjonskoder',
					value: tpsfData.spesreg
				},
				{
					id: 'gtVerdi',
					label: 'Geo. Tilhør',
					value: tpsfData.gtVerdi,
					extraLabel: Formatters.gtTypeLabel(tpsfData.gtType),
					apiKodeverkId: Formatters.gtApiKodeverkId(tpsfData.gtType)
				},
				{
					id: 'tknr',
					label: 'TK nummer',
					tknr: tpsfData.tknr
				},
				{
					id: 'egenAnsattDatoFom',
					label: 'Egenansatt',
					value: tpsfData.egenAnsattDatoFom && 'JA'
				}
			]
		}
	]

	if (tpsfData.statsborgerskap) {
		data.push({
			header: 'Nasjonalitet',
			data: [
				{
					id: 'innvandretFra',
					label: 'Innvandret fra',
					value: tpsfData.innvandretFra
				},
				{
					id: 'statsborgerskap',
					label: 'Statsborgerskap',
					value: tpsfData.statsborgerskap
				},
				{
					id: 'sprakKode',
					label: 'Språk',
					value: tpsfData.sprakKode
				}
			]
		})
	}

	if (tpsfData.boadresse) {
		data.push({
			header: 'Bostedadresse',
			data: [
				{
					parent: 'boadresse',
					id: 'gateadresse',
					label: 'Gatenavn',
					value: tpsfData.boadresse.gateadresse
				},
				{
					parent: 'boadresse',
					id: 'husnummer',
					label: 'Husnummer',
					value: tpsfData.boadresse.husnummer
				},
				{
					parent: 'boadresse',
					id: 'postnr',
					label: 'Postnummer',
					extraLabel: tpsfData.boadresse.postnr,
					apiKodeverkId: 'Postnummer',
					value: tpsfData.boadresse.postnr
				},
				{
					parent: 'boadresse',
					id: 'flyttedato',
					label: 'Flyttedato',
					value: Formatters.formatDate(tpsfData.boadresse.flyttedato)
				}
			]
		})
	}

	if (tpsfData.postadresse) {
		data.push({
			header: 'Postadresse',
			data: [
				{
					parent: 'postadresse',
					id: 'postLand',
					label: 'Land',
					value: tpsfData.postadresse[0].postLand
				},
				{
					parent: 'postadresse',
					id: 'postLinje1',
					label: 'Adresselinje 1',
					value: tpsfData.postadresse[0].postLinje1
				},
				{
					parent: 'postadresse',
					id: 'postLinje2',
					label: 'Adresselinje 2',
					value: tpsfData.postadresse[0].postLinje2
				},
				{
					parent: 'postadresse',
					id: 'postLinje3',
					label: 'Adresselinje 3',
					value: tpsfData.postadresse[0].postLinje3
				}
			]
		})
	}

	if (tpsfData.relasjoner && tpsfData.relasjoner.length) {
		data.push({
			header: 'Familierelasjoner',
			multiple: true,
			data: tpsfData.relasjoner.map(relasjon => {
				return {
					parent: 'relasjoner',
					id: relasjon.id,
					label: relasjonTranslator(relasjon.relasjonTypeNavn),
					value: [
						{
							id: 'ident',
							label: relasjon.personRelasjonMed.identtype,
							value: relasjon.personRelasjonMed.ident
						},
						{
							id: 'fornavn',
							label: 'Fornavn',
							value: relasjon.personRelasjonMed.fornavn
						},
						{
							id: 'mellomnavn',
							label: 'Mellomnavn',
							value: relasjon.personRelasjonMed.mellomnavn
						},
						{
							id: 'etternavn',
							label: 'Etternavn',
							value: relasjon.personRelasjonMed.etternavn
						},
						{
							id: 'kjonn',
							label: 'Kjønn',
							value: relasjon.personRelasjonMed.kjonn
						},
						{
							id: 'alder',
							label: 'Alder',
							value: Formatters.formatAlder(
								relasjon.personRelasjonMed.alder,
								relasjon.personRelasjonMed.doedsdato
							)
						},
						{
							id: 'statsborgerskap',
							label: 'Statsborgerskap',
							value: relasjon.personRelasjonMed.statsborgerskap
						},
						{
							id: 'sprakKode',
							label: 'Språk',
							value: relasjon.personRelasjonMed.sprakKode
						},
						{
							id: 'sivilstand',
							label: 'Sivilstand',
							value: relasjon.personRelasjonMed.sivilstand
						},
						{
							id: 'spesreg',
							label: 'Diskresjonskoder',
							value: relasjon.personRelasjonMed.spesreg
						},
						{
							id: 'egenAnsattDatoFom',
							label: 'Egenansatt',
							value: relasjon.personRelasjonMed.egenAnsattDatoFom && 'JA'
						}
					]
				}
			})
		})
	}
	return data
}

export function mapBestillingId(testIdent) {
	if (!testIdent) return null
	return {
		header: 'Tidligere bestilling-ID',
		data: [
			{
				id: 'bestillingID',
				value: testIdent.bestillingId.slice(1).join(', ')
			}
		]
	}
}

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
