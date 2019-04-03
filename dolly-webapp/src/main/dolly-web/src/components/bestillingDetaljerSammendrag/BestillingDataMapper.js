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
		const tpsfKriterier = JSON.parse(bestillingData.tpsfKriterier)
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
			const adresse = {
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
			data.push(adresse)
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

	if (bestillingData.bestKriterier) {
		const registreKriterier = JSON.parse(bestillingData.bestKriterier)

		const sigrunStubKriterier = registreKriterier.sigrunStub && registreKriterier.sigrunStub
		if (sigrunStubKriterier) {
			console.log(sigrunStubKriterier, 'ha')
			const sigrunStub = {
				header: 'Inntekter',

				itemRows: []
			}

			sigrunStubKriterier.forEach(inntekt => {
				sigrunStub.itemRows.push([
					{
						label: 'År',
						value: inntekt.inntektsaar
					},
					{ label: 2, value: 2 }
				])
			})
			data.push(sigrunStub)
		}
	}

	return data
}
