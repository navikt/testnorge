import FormatDate from '~/utils/FormatDate'

/*
    Transformer for TPSF-data.
*/

const TpsfTransformer = response => {
	//return response.data.map(i => mapDataToDolly(i))
	if (!response) return null
	return response.map(i => mapDataToDolly(i))
}

const kjonnTranslator = kjonn => (kjonn === 'M' ? 'Mann' : 'Kvinne')

const relasjonTranslator = relasjon => {
	switch (relasjon) {
		case 'EKTEFELLE':
			return 'PARTNER'
		case 'MOR':
		case 'FAR':
			return 'BARN'
		default:
			return 'UKJENT'
	}
}

const mapDataToDolly = i => {
	let res = {
		personId: i.personId,
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
						value: i.alder
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
					value: i.innvandretFra
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
	if (i.relasjoner.length) {
		res.data.push({
			header: 'Familierelasjoner',
			multiple: true,
			data: i.relasjoner.map(relasjon => {
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
							value: kjonnTranslator(relasjon.personRelasjonMed.kjonn)
						}
					]
				}
			})
		})
	}

	return res
}

export default TpsfTransformer
