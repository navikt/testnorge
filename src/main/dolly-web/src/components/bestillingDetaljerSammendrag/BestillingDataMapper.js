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
		console.log('registreKriterier :', registreKriterier)

		const aaregKriterier = registreKriterier.aareg && registreKriterier.aareg
		if (aaregKriterier) {
			const aareg = {
				header: 'Arbeidsforhold',
				itemRows: []
			}

			aaregKriterier.forEach((arbeidsforhold, i) => {
				aareg.itemRows.push([
					{
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						label: 'Yrke',
						// TODO: Apikodeverkid
						value: arbeidsforhold.arbeidsavtale.yrke
					},
					{ label: 'Startdato', value: arbeidsforhold.ansettelsesPeriode.fom },
					{
						label: 'Sluttdato',
						value: arbeidsforhold.ansettelsesPeriode.tom
					},
					{
						label: 'Stillingprosent',
						value: arbeidsforhold.arbeidsavtale.stillingsprosent
					},
					{
						label: 'Type av arbeidsgiver',
						value: arbeidsforhold.arbeidsgiver.aktoertype
					},

					{
						label: 'Type av arbeidsgiver',
						value: arbeidsforhold.arbeidsgiver.aktoertype
					}
				])
			})

			data.push(aareg)
		}
		const sigrunStubKriterier = registreKriterier.sigrunStub && registreKriterier.sigrunStub

		if (sigrunStubKriterier) {
			// Flatter ut sigrunKriterier for å gjøre det lettere å mappe

			let flatSigrunStubKriterier = []
			sigrunStubKriterier.forEach(inntekt => {
				inntekt.grunnlag.forEach(g => {
					flatSigrunStubKriterier.push({
						grunnlag: g.tekniskNavn,
						inntektsaar: inntekt.inntektsaar,
						tjeneste: inntekt.tjeneste,
						verdi: g.verdi
					})
				})
			})

			const sigrunStub = {
				header: 'Inntekter',
				itemRows: []
			}

			flatSigrunStubKriterier.forEach((inntekt, i) => {
				sigrunStub.itemRows.push([
					{
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						label: 'År',
						value: inntekt.inntektsaar
					},
					{ label: 'Beløp', value: inntekt.verdi },
					{
						label: 'Tjeneste',
						value: inntekt.tjeneste
					},
					{
						label: 'grunnlag',
						value: inntekt.grunnlag
					}
				])
			})
			data.push(sigrunStub)
		}

		const krrKriterier = registreKriterier.krrStub && registreKriterier.krrStub

		if (krrKriterier) {
			const krrStub = {
				header: 'Kontaktinformasjon og reservasjon',
				items: [
					{
						label: 'Mobilnummer',
						value: krrKriterier.mobil
					},
					{
						label: 'Epost',
						value: krrKriterier.epost
					},
					{
						label: 'RESERVERT MOT DIGITALKOMMUNIKASJON',
						value: krrKriterier.reservert ? 'JA' : 'NEI'
					}
				]
			}

			data.push(krrStub)
		}
	}

	return data
}
