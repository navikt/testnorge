import Formatters from '~/utils/DataFormatter'

export function mapBestillingData(bestillingData) {
	if (!bestillingData) return null
	const data = []

	const bestillingsInfo = {
		header: 'Bestillingsinformasjon',
		items: [
			{
				label: 'Antall',
				value: bestillingData.antallIdenter.toString()
			},
			{
				label: 'Sist Oppdatert',
				value: Formatters.formatDate(bestillingData.sistOppdatert)
			},
			{
				label: 'Gjenopprett fra',
				value: bestillingData.opprettetFraId && 'Bestilling #' + bestillingData.opprettetFraId
			}
		]
	}
	data.push(bestillingsInfo)

	// Gamle bestillinger har ikke tpsfKriterie
	if (bestillingData.tpsfKriterier) {
		const tpsfKriterier = bestillingData.tpsfKriterier && JSON.parse(bestillingData.tpsfKriterier)
		const personinfo = {
			header: 'Personlig informasjon',
			items: [
				{
					label: 'Identtype',
					value: tpsfKriterier.identtype
				},
				{
					label: 'Født etter',
					value: Formatters.formatDate(tpsfKriterier.foedtEtter)
				},
				{
					label: 'Født før',
					value: Formatters.formatDate(tpsfKriterier.foedtFoer)
				},
				{
					label: 'Dødsdato',
					value: Formatters.formatDate(tpsfKriterier.doedsdato)
				},
				{
					label: 'Statsborgerskap',
					value: tpsfKriterier.statsborgerskap
				},
				{
					label: 'Kjønn',
					value: Formatters.kjonnToString(tpsfKriterier.kjonn)
				},
				{
					label: 'Sivilstand',
					value: tpsfKriterier.sivilstand
				},
				{
					label: 'Diskresjonskoder',
					value: tpsfKriterier.spesreg
				},
				{
					label: 'Språk',
					value: tpsfKriterier.sprakKode
				},
				{
					label: 'Egenansatt',
					value: Formatters.oversettBoolean(tpsfKriterier.egenansattDatoFom)
				}
			]
		}
		data.push(personinfo)

		if (tpsfKriterier.boadresse) {
			const boadresse = {
				header: 'Bostedadresse',
				items: [
					{
						header: 'Bosted'
					},
					{
						label: 'Gatenavn',
						value: tpsfKriterier.boadresse.gateadresse
					},
					{
						label: 'Husnummer',
						value: tpsfKriterier.boadresse.husnummer
					},
					{
						label: 'Postnummer',
						value: tpsfKriterier.boadresse.postnr
					},
					{
						label: 'Kommunenummer',
						value: tpsfKriterier.boadresse.kommunenr
					},
					{
						label: 'Flyttedato',
						value: Formatters.formatDate(tpsfKriterier.boadresse.flyttedato)
					}
				]
			}
			data.push(boadresse)
		}

		if (tpsfKriterier.postadresse) {
			const postadresse = {
				header: 'Postadresse',
				items: [
					{
						label: 'Land',
						value: tpsfKriterier.postadresse[0].postLand
					},
					{
						label: 'Adresselinje 1',
						value: tpsfKriterier.postadresse[0].postLinje1
					},
					{
						label: 'Adresselinje 2',
						value: tpsfKriterier.postadresse[0].postLinje2
					},
					{
						label: 'Adresselinje 3',
						value: tpsfKriterier.postadresse[0].postLinje3
					}
				]
			}
			data.push(postadresse)
		}

		if (tpsfKriterier.relasjoner) {
			const familie = {
				header: 'Familierelasjoner',
				items: []
			}
			if (tpsfKriterier.relasjoner.partner) {
				familie.items.push({
					label: 'Partner',
					value: 'Har partner'
				})
			}
			if (tpsfKriterier.relasjoner.barn) {
				let antallBarn = tpsfKriterier.relasjoner.barn.length.toString()
				familie.items.push({
					label: 'Barn',
					value: 'Har ' + antallBarn + ' barn'
				})
			}
			data.push(familie)
		}
	}
	return data
}
