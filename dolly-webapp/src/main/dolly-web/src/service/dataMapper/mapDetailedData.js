import { relasjonTranslator } from './Utils'
import Formatters from '~/utils/DataFormatter'

export default function mapDetailedData(brukerData, bestillingData) {
	let data = [
		{
			header: 'Personlig informasjon',
			data: [
				{
					id: 'ident',
					label: brukerData.identtype,
					value: brukerData.ident
				},
				{
					id: 'fornavn',
					label: 'Fornavn',
					value: brukerData.fornavn
				},
				{
					id: 'mellomnavn',
					label: 'Mellomnavn',
					value: brukerData.mellomnavn
				},
				{
					id: 'etternavn',
					label: 'Etternavn',
					value: brukerData.etternavn
				},
				{
					id: 'kjonn',
					label: 'Kjønn',
					value: Formatters.kjonnToString(brukerData.kjonn)
				},
				{
					id: 'alder',
					label: 'Alder',
					value: Formatters.formatAlder(brukerData.alder, brukerData.doedsdato)
				},
				{
					id: 'miljoer',
					label: 'Miljøer',
					value: Formatters.arrayToString(bestillingData.environments)
				}
			]
		}
	]

	if (brukerData.statsborgerskap) {
		data.push({
			header: 'Nasjonalitet',
			data: [
				{
					id: 'innvandretFra',
					label: 'Innvandret fra',
					value: brukerData.innvandretFra
				},
				{
					id: 'statsborgerskap',
					label: 'Statsborgerskap',
					value: brukerData.statsborgerskap
				}
			]
		})
	}

	if (brukerData.boadresse) {
		data.push({
			header: 'Bostedadresse',
			data: [
				{
					parent: 'boadresse',
					id: 'gateadresse',
					label: 'Gatenavn',
					value: brukerData.boadresse.gateadresse
				},
				{
					parent: 'boadresse',
					id: 'husnummer',
					label: 'Husnummer',
					value: brukerData.boadresse.husnummer
				},
				{
					parent: 'boadresse',
					id: 'gatekode',
					label: 'Gatekode',
					value: brukerData.boadresse.gatekode
				},
				{
					parent: 'boadresse',
					id: 'postnr',
					label: 'Postnummer',
					value: brukerData.boadresse.postnr
				},
				{
					parent: 'boadresse',
					id: 'flyttedato',
					label: 'Flyttedato',
					value: Formatters.formatDate(brukerData.boadresse.flyttedato)
				}
			]
		})
	}
	if (brukerData.relasjoner.length) {
		data.push({
			header: 'Familierelasjoner',
			multiple: true,
			data: brukerData.relasjoner.map(relasjon => {
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
							value: Formatters.kjonnToString(relasjon.personRelasjonMed.kjonn)
						}
					]
				}
			})
		})
	}

	return data
}
