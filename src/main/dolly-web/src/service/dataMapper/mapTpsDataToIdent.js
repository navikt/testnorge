import { relasjonTranslator } from './Utils'
import Formatters from '~/utils/DataFormatter'

export function mapTpsfData(tpsfData, testIdent) {
	console.log('tpsfData :', tpsfData)
	console.log('testIdent :', testIdent)
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
