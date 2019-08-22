import Formatters from '~/utils/DataFormatter'

const _getTpsfBestillingData = data => {
	return [
		{
			label: 'Identtype',
			value: data.identtype
		},
		{
			label: 'Født etter',
			value: Formatters.formatDate(data.foedtEtter)
		},
		{
			label: 'Født før',
			value: Formatters.formatDate(data.foedtFoer)
		},
		{
			label: 'Dødsdato',
			value: Formatters.formatDate(data.doedsdato)
		},
		{
			label: 'Statsborgerskap',
			value: data.statsborgerskap,
			apiKodeverkId: 'StatsborgerskapFreg'
		},
		{
			label: 'Kjønn',
			value: Formatters.kjonnToString(data.kjonn)
		},
		{
			label: 'Har mellomnavn',
			value: Formatters.oversettBoolean(data.harMellomnavn)
		},
		{
			label: 'Sivilstand',
			value: data.sivilstand,
			apiKodeverkId: 'Sivilstander'
		},
		{
			label: 'Diskresjonskoder',
			value: data.spesreg,
			apiKodeverkId: 'Diskresjonskoder'
		},
		{
			label: 'Uten fast bopel',
			value: Formatters.oversettBoolean(data.utenFastBopel)
		},
		{
			label: 'Språk',
			value: data.sprakKode,
			apiKodeverkId: 'Språk'
		},
		{
			label: 'Egenansatt',
			value: Formatters.oversettBoolean(data.egenansattDatoFom)
		}
	]
}

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
			items: _getTpsfBestillingData(tpsfKriterier)
		}
		// For å mappe utenlands-ID under personlig informasjon
		if (bestillingData.bestKriterier) {
			const registreKriterier = JSON.parse(bestillingData.bestKriterier)
			const pdlforvalter = registreKriterier.pdlforvalter && registreKriterier.pdlforvalter
			if (pdlforvalter && pdlforvalter.utenlandskIdentifikasjonsnummer) {
				const pdlf = {
					items: [
						{
							label: 'Utenlands-ID',
							value: pdlforvalter.utenlandskIdentifikasjonsnummer.identifikasjonsnummer
						},
						{
							label: 'Utenlands-ID kilde',
							value: pdlforvalter.utenlandskIdentifikasjonsnummer.kilde
						},
						{
							label: 'Utenlands-ID opphørt',
							value: Formatters.oversettBoolean(
								pdlforvalter.utenlandskIdentifikasjonsnummer.opphoert
							)
						},
						{
							label: 'Utstederland (ID)',
							value: pdlforvalter.utenlandskIdentifikasjonsnummer.utstederland,
							apiKodeverkId: 'StatsborgerskapFreg'
						}
					]
				}
				pdlf.items.forEach(item => {
					personinfo.items.push(item)
				})
			}
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
						label: 'Adressetype',
						value: Formatters.adressetypeToString(tpsfKriterier.boadresse.adressetype)
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
						label: 'Stedsnavn',
						value: tpsfKriterier.boadresse.mellomnavn
					},
					{
						label: 'Gårdsnummer',
						value: tpsfKriterier.boadresse.gardsnr
					},
					{
						label: 'Bruksnummer',
						value: tpsfKriterier.boadresse.bruksnr
					},
					{
						label: 'Festenummer',
						value: tpsfKriterier.boadresse.festenr
					},
					{
						label: 'Undernummer',
						value: tpsfKriterier.boadresse.undernr
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
			if (tpsfKriterier.relasjoner.partner) {
				const partner = {
					header: 'Partner',
					items: _getTpsfBestillingData(tpsfKriterier.relasjoner.partner)
				}

				data.push(partner)
			}

			if (tpsfKriterier.relasjoner.barn) {
				const barn = {
					header: 'Barn',
					itemRows: []
				}

				tpsfKriterier.relasjoner.barn.forEach((item, i) => {
					barn.itemRows.push([
						{
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						..._getTpsfBestillingData(item)
					])
				})

				data.push(barn)
			}
		}
	}

	if (bestillingData.bestKriterier) {
		const registreKriterier = JSON.parse(bestillingData.bestKriterier)
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
						label: 'Startdato',
						value: arbeidsforhold.ansettelsesPeriode.fom.split('T')[0]
					},
					{
						label: 'Sluttdato',
						value:
							arbeidsforhold.ansettelsesPeriode.tom &&
							arbeidsforhold.ansettelsesPeriode.tom.split('T')[0]
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
						label: 'Orgnummer',
						value: arbeidsforhold.arbeidsgiver.orgnummer
					},
					{
						label: 'Arbeidsgiver Ident',
						value: arbeidsforhold.arbeidsgiver.ident
					},
					{
						label: 'Yrke',
						value: arbeidsforhold.arbeidsavtale.yrke,
						apiKodeverkId: 'Yrker',
						width: 'xlarge',
						showKodeverkValue: true
					},
					{
						label: 'Permisjon',
						value: arbeidsforhold.permisjon && arbeidsforhold.permisjon.length
					},
					{
						label: 'Utenlandsopphold',
						value: arbeidsforhold.utenlandsopphold && arbeidsforhold.utenlandsopphold.length
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
						value: inntekt.grunnlag,
						width: 'xlarge',
						apiKodeverkId: inntekt.tjeneste
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
						value: krrKriterier.reservert ? 'JA' : 'NEI',
						width: 'medium'
					}
				]
			}

			data.push(krrStub)
		}

		const pdlforvalterKriterier = registreKriterier.pdlforvalter && registreKriterier.pdlforvalter

		if (pdlforvalterKriterier) {
			const doedsboKriterier =
				pdlforvalterKriterier.kontaktinformasjonForDoedsbo &&
				pdlforvalterKriterier.kontaktinformasjonForDoedsbo
			if (doedsboKriterier) {
				const navnType = doedsboKriterier.adressat.navn
					? 'navn'
					: doedsboKriterier.adressat.kontaktperson
						? 'kontaktperson'
						: null
				const doedsbo = {
					header: 'Kontaktinformasjon for dødsbo',
					items: [
						{
							label: 'Fornavn',
							value: navnType && doedsboKriterier.adressat[navnType].fornavn
						},
						{
							label: 'Mellomnavn',
							value: navnType && doedsboKriterier.adressat[navnType].mellomnavn
						},
						{
							label: 'Etternavn',
							value: navnType && doedsboKriterier.adressat[navnType].etternavn
						},
						{
							label: 'Fnr/dnr',
							value: doedsboKriterier.adressat.idnummer
						},
						{
							label: 'Fødselsdato',
							value: Formatters.formatDate(doedsboKriterier.adressat.foedselsdato)
						},
						{
							label: 'Organisasjonsnavn',
							value: doedsboKriterier.adressat.organisasjonsnavn
						},
						{
							label: 'Organisasjonsnummer',
							value: doedsboKriterier.adressat.organisasjonsnummer
						},
						{
							label: 'Adresselinje 1',
							value: doedsboKriterier.adresselinje1
						},
						{
							label: 'Adresselinje 2',
							value: doedsboKriterier.adresselinje2
						},
						{
							label: 'Postnummer og -sted',
							value: doedsboKriterier.postnummer + ' ' + doedsboKriterier.poststedsnavn
						},
						{
							label: 'Land',
							value: doedsboKriterier.landkode,
							apiKodeverkId: 'Landkoder'
						},
						{
							label: 'Skifteform',
							value: doedsboKriterier.skifteform
						},
						{
							label: 'Dato utstedt',
							value: Formatters.formatDate(doedsboKriterier.utstedtDato)
						},
						{
							label: 'Gyldig fra',
							value: Formatters.formatDate(doedsboKriterier.gyldigFom)
						},
						{
							label: 'Gyldig til',
							value: Formatters.formatDate(doedsboKriterier.gyldigTom)
						}
					]
				}
				data.push(doedsbo)
			}
		}
		const arenaKriterier = registreKriterier.arenaforvalter && registreKriterier.arenaforvalter

		if (arenaKriterier) {
			const arenaforvalter = {
				header: 'Arena',
				items: [
					{
						label: 'Brukertype',
						value: Formatters.uppercaseAndUnderscoreToCapitalized(arenaKriterier.arenaBrukertype)
					},
					{
						label: 'Servicebehov',
						value: arenaKriterier.kvalifiseringsgruppe
					},
					{
						label: 'Inaktiv fra dato',
						value: Formatters.formatDate(arenaKriterier.inaktiveringDato)
					},
					{
						label: 'Har 11-5 vedtak',
						value: Formatters.oversettBoolean(arenaKriterier.aap115 && true)
					},
					{
						label: 'Fra dato',
						value: arenaKriterier.aap115 && Formatters.formatDate(arenaKriterier.aap115[0].fraDato)
					},
					{
						label: 'Har AAP vedtak UA - positivt utfall',
						value: Formatters.oversettBoolean(arenaKriterier.aap && true)
					},
					{
						label: 'Fra dato',
						value: arenaKriterier.aap && Formatters.formatDate(arenaKriterier.aap[0].fraDato)
					},
					{
						label: 'Til dato',
						value: arenaKriterier.aap && Formatters.formatDate(arenaKriterier.aap[0].tilDato)
					}
				]
			}
			data.push(arenaforvalter)
		}
	}

	return data
}
