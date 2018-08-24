import FormatDate from '~/utils/FormatDate'

/*
    Transformer for TPSF-data.
*/

const TpsfTransformer = data => {
	return data.map(i => mapDataToDolly(i))
}

const kjonnTranslator = kjonn => (kjonn === 'M' ? 'Mann' : 'Kvinne')

const mapDataToDolly = i => {
	let res = {
		dollyId: i.personId,
		id: i.ident,
		idType: i.identtype,
		navn: `${i.fornavn} ${i.etternavn}`,
		kjonn: kjonnTranslator(i.kjonn),
		alder: i.alder,
		data: [
			{
				header: 'Personlig informasjon',
				data: [
					{
						id: 'ident',
						label: i.identtype,
						value: i.ident
					},
					{
						id: 'fornavn',
						label: 'Fornavn',
						value: i.fornavn
					},
					{
						id: 'mellomnavn',
						label: 'Mellomnavn',
						value: i.mellomnavn
					},
					{
						id: 'etternavn',
						label: 'Etternavn',
						value: i.etternavn
					},
					{
						id: 'kjonn',
						label: 'Kjønn',
						value: kjonnTranslator(i.kjonn)
					},
					{
						id: 'alder',
						label: 'Alder',
						value: i.alder || 'Udefinert'
					}
				]
			}
		]
	}

	if (i.statsborgerskap) {
		res.data.push({
			header: 'Nasjonalitet',
			data: [
				{
					id: 'innvandretFra',
					label: 'Innvandret fra',
					value: i.innvandretFra || 'Udefinert'
				},
				{
					id: 'statsborgerskap',
					label: 'Statsborgerskap',
					value: i.statsborgerskap
				}
			]
		})
	}

	if (i.boadresse) {
		res.data.push({
			header: 'Bostedadresse',
			data: [
				{
					parent: 'boadresse',
					id: 'gateadresse',
					label: 'Gatenavn',
					value: i.boadresse.gateadresse
				},
				{
					parent: 'boadresse',
					id: 'husnummer',
					label: 'Husnummer',
					value: i.boadresse.husnummer
				},
				{
					parent: 'boadresse',
					id: 'gatekode',
					label: 'Gatekode',
					value: i.boadresse.gatekode
				},
				{
					parent: 'boadresse',
					id: 'postnr',
					label: 'Postnummer',
					value: i.boadresse.postnr
				},
				{
					parent: 'boadresse',
					id: 'flyttedato',
					label: 'Flyttedato',
					value: FormatDate(i.boadresse.flyttedato)
				}
			]
		})
	}

	return res
}

export default TpsfTransformer
