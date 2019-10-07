import _get from 'lodash/get'
import Formatters from '~/utils/DataFormatter'

// TODO: Flytte til selector?
// - Denne kan forminskes ved bruk av hjelpefunksjoner
// - Når vi får på plass en bedre struktur for bestillingsprosessen, kan
//   mest sannsynlig visse props fjernes herfra (width?)

const obj = (label, value, apiKodeverkId) => ({
	label,
	value,
	...(apiKodeverkId && { apiKodeverkId })
})

const _getTpsfBestillingData = data => {
	return [
		obj('Identtype', data.identtype),
		obj('Født etter', Formatters.formatDate(data.foedtEtter)),
		obj('Født før', Formatters.formatDate(data.foedtFoer)),
		obj('Dødsdato', Formatters.formatDate(data.doedsdato)),
		obj('Statsborgerskap', data.statsborgerskap, 'Landkoder'),
		obj('Kjønn', Formatters.kjonnToString(data.kjonn)),
		obj('Har mellomnavn', Formatters.oversettBoolean(data.harMellomnavn)),
		obj('Sivilstand', data.sivilstand, 'Sivilstander'),
		obj('Diskresjonskoder', data.spesreg, 'Diskresjonskoder'),
		obj('Uten fast bopel', Formatters.oversettBoolean(data.utenFastBopel)),
		obj('Språk', data.sprakKode, 'Språk'),
		obj('Innvandret fra land', data.innvandretFraLand, 'Landkoder'),
		obj('Innvandret dato', Formatters.formatDate(data.innvandretFraLandFlyttedato)),
		obj('Utvandret til land', data.utvandretTilLand, 'Landkoder'),
		obj('Utvandret dato', Formatters.formatDate(data.utvandretTilLandFlyttedato)),
		obj('Er forsvunnet', Formatters.oversettBoolean(data.erForsvunnet)),
		obj('Forsvunnet dato', Formatters.formatDate(data.forsvunnetDato)),
		obj('Egenansatt', Formatters.oversettBoolean(data.egenansattDatoFom)),
	]
}

export function mapBestillingData(bestillingData) {
	if (!bestillingData) return null
	const data = []

	const bestillingsInfo = {
		header: 'Bestillingsinformasjon',
		items: [
			obj('Antall', bestillingData.antallIdenter.toString()),
			obj('Sist Oppdatert', Formatters.formatDate(bestillingData.sistOppdatert)),
			obj(
				'Gjenopprett fra',
				bestillingData.opprettetFraId && `Bestilling # ${bestillingData.opprettetFraId}`
			)
		]
	}
	data.push(bestillingsInfo)

	// Gamle bestillinger har ikke tpsfKriterie
	if (bestillingData.tpsfKriterier) {
		const tpsfKriterier = JSON.parse(bestillingData.tpsfKriterier)
		const personinfo = {
			header: 'Persondetaljer',
			items: _getTpsfBestillingData(tpsfKriterier)
		}

		// For å mappe utenlands-ID under persondetaljer
		if (bestillingData.bestKriterier) {
			const registreKriterier = JSON.parse(bestillingData.bestKriterier)
			const uidnr = _get(registreKriterier, 'pdlforvalter.utenlandskIdentifikasjonsnummer')

			if (uidnr) {
				const pdlf = [
					obj('Utenlands-ID', uidnr.identifikasjonsnummer),
					obj('Utenlands-ID kilde', uidnr.kilde),
					obj('Utenlands-ID opphørt', Formatters.oversettBoolean(uidnr.opphoert)),
					obj('Utstederland (ID)', uidnr.utstederland, 'Landkoder')
				]
				pdlf.forEach(item => {
					personinfo.items.push(item)
				})
			}
		}
		data.push(personinfo)
		if (tpsfKriterier.boadresse) {
			const adr = tpsfKriterier.boadresse
			const adresse = {
				header: 'Bostedadresse',
				items: [
					{
						header: 'Bosted'
					},
					obj('Adressetype', Formatters.adressetypeToString(adr.adressetype)),
					obj('Gatenavn', adr.gateadresse),
					obj('Husnummer', adr.husnummer),
					obj('Stedsnavn', adr.mellomnavn),
					obj('Gårdsnummer', adr.gardsnr),
					obj('Bruksnummer', adr.bruksnr),
					obj('Festenummer', adr.festenr),
					obj('Undernummer', adr.undernr),
					obj('Postnummer', adr.postnr),
					obj('Kommunenummer', adr.kommunenr),
					obj('Flyttedato', Formatters.formatDate(adr.flyttedato))
				]
			}
			data.push(adresse)
		}

		if (tpsfKriterier.postadresse) {
			const postadr = tpsfKriterier.postadresse[0]
			const postadresse = {
				header: 'Postadresse',
				items: [
					obj('Land', postadr.postland),
					obj('Adresselinje 1', postadr.postLinje1),
					obj('Adresselinje 2', postadr.postLinje2),
					obj('Adresselinje 3', postadr.postLinje3)
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
		const aaregKriterier = registreKriterier.aareg
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
					obj('Startdato', arbeidsforhold.ansettelsesPeriode.fom.split('T')[0]),
					obj(
						'Sluttdato',
						arbeidsforhold.ansettelsesPeriode.tom &&
							arbeidsforhold.ansettelsesPeriode.tom.split('T')[0]
					),
					obj('Stillingprosent', arbeidsforhold.arbeidsavtale.stillingsprosent),
					obj('Type av arbeidsgiver', arbeidsforhold.arbeidsgiver.aktoertype),
					obj('Orgnummer', arbeidsforhold.arbeidsgiver.orgnummer),
					obj('Arbeidsgiver Ident', arbeidsforhold.arbeidsgiver.ident),
					{
						label: 'Yrke',
						value: arbeidsforhold.arbeidsavtale.yrke,
						apiKodeverkId: 'Yrker',
						width: 'xlarge',
						showKodeverkValue: true
					},
					obj('Permisjon', arbeidsforhold.permisjon && arbeidsforhold.permisjon.length),
					obj(
						'Utenlandsopphold',
						arbeidsforhold.utenlandsopphold && arbeidsforhold.utenlandsopphold.length
					)
				])
			})
			data.push(aareg)
		}
		const sigrunStubKriterier = registreKriterier.sigrunstub

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
					obj('År', inntekt.inntektsaar),
					obj('Beløp', inntekt.verdi),
					obj('Tjeneste', inntekt.tjeneste),
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

		const krrKriterier = registreKriterier.krrstub

		if (krrKriterier) {
			const krrStub = {
				header: 'Kontaktinformasjon og reservasjon',
				items: [
					obj('Mobilnummer', krrKriterier.mobil),
					obj('Epost', krrKriterier.epost),
					{
						label: 'RESERVERT MOT DIGITALKOMMUNIKASJON',
						value: krrKriterier.reservert ? 'JA' : 'NEI',
						width: 'medium'
					}
				]
			}

			data.push(krrStub)
		}

		const pdlforvalterKriterier = registreKriterier.pdlforvalter

		if (pdlforvalterKriterier) {
			const doedsboKriterier = pdlforvalterKriterier.kontaktinformasjonForDoedsbo
			if (doedsboKriterier) {
				const navnType = doedsboKriterier.adressat.navn
					? 'navn'
					: doedsboKriterier.adressat.kontaktperson
						? 'kontaktperson'
						: null
				const doedsbo = {
					header: 'Kontaktinformasjon for dødsbo',
					items: [
						obj('Fornavn', navnType && doedsboKriterier.adressat[navnType].fornavn),
						obj('Mellomnavn', navnType && doedsboKriterier.adressat[navnType].mellomnavn),
						obj('Etternavn', navnType && doedsboKriterier.adressat[navnType].etternavn),
						obj('Fnr/dnr/BOST', doedsboKriterier.adressat.idnummer),
						obj('Fødselsdato', Formatters.formatDate(doedsboKriterier.adressat.foedselsdato)),
						obj('Organisasjonsnavn', doedsboKriterier.adressat.organisasjonsnavn),
						obj('Organisasjonsnummer', doedsboKriterier.adressat.organisasjonsnummer),
						obj('Adresselinje 1', doedsboKriterier.adresselinje1),
						obj('Adresselinje 2', doedsboKriterier.adresselinje2),
						obj(
							'Postnummer og -sted',
							`${doedsboKriterier.postnummer} ${doedsboKriterier.poststedsnavn}`
						),
						obj('Land', doedsboKriterier.landkode, 'Landkoder'),
						obj('Skifteform', doedsboKriterier.skifteform),
						obj('Dato utstedt', Formatters.formatDate(doedsboKriterier.utstedtDato)),
						obj('Gyldig fra', Formatters.formatDate(doedsboKriterier.gyldigFom)),
						obj('Gyldig til', Formatters.formatDate(doedsboKriterier.gyldigTom))
					]
				}
				data.push(doedsbo)
			}

			if (pdlforvalterKriterier.falskIdentitet) {
				const falskIdData = pdlforvalterKriterier.falskIdentitet.rettIdentitet

				if (falskIdData.identitetType === 'UKJENT') {
					const falskId = {
						header: 'Falsk identitet',
						items: [
							{
								label: 'Rett identitet',
								value: 'Ukjent'
							}
						]
					}
					data.push(falskId)
				} else if (falskIdData.identitetType === 'ENTYDIG') {
					const falskId = {
						header: 'Falsk identitet',
						items: [
							{
								label: 'Rett fødselsnummer',
								value: falskIdData.rettIdentitetVedIdentifikasjonsnummer
							}
						]
					}
					data.push(falskId)
				} else {
					const falskId = {
						header: 'Falsk identitet',
						items: [
							obj('Rett identitet', 'Kjent ved personopplysninger'),
							obj('Fornavn', falskIdData.personnavn.fornavn),
							obj('Mellomnavn', falskIdData.personnavn.mellomnavn),
							obj('Etternavn', falskIdData.personnavn.etternavn),
							obj('Kjønn', falskIdData.kjoenn),
							obj('Fødselsdato', Formatters.formatDate(falskIdData.foedselsdato)),
							obj('Statsborgerskap', Formatters.arrayToString(falskIdData.statsborgerskap))
						]
					}
					data.push(falskId)
				}
			}
		}
		const arenaKriterier = registreKriterier.arenaforvalter

		if (arenaKriterier) {
			const arenaforvalter = {
				header: 'Arena',
				items: [
					obj(
						'Brukertype',
						Formatters.uppercaseAndUnderscoreToCapitalized(arenaKriterier.arenaBrukertype)
					),
					obj('Servicebehov', arenaKriterier.kvalifiseringsgruppe),
					obj('Inaktiv fra dato', Formatters.formatDate(arenaKriterier.inaktiveringDato)),
					obj('Har 11-5 vedtak', Formatters.oversettBoolean(arenaKriterier.aap115 && true)),
					obj(
						'Fra dato',
						arenaKriterier.aap115 && Formatters.formatDate(arenaKriterier.aap115[0].fraDato)
					),
					obj(
						'Har AAP vedtak UA - positivt utfall',
						Formatters.oversettBoolean(arenaKriterier.aap && true)
					),
					obj(
						'Fra dato',
						arenaKriterier.aap && Formatters.formatDate(arenaKriterier.aap[0].fraDato)
					),
					obj(
						'Til dato',
						arenaKriterier.aap && Formatters.formatDate(arenaKriterier.aap[0].tilDato)
					)
				]
			}
			data.push(arenaforvalter)
		}

		const instKriterier = registreKriterier.instdata

		if (instKriterier) {
			// Flater ut instKriterier for å gjøre det lettere å mappe

			let flatInstKriterier = []
			instKriterier.forEach(i => {
				flatInstKriterier.push({
					institusjonstype: i.institusjonstype,
					varighet: i.varighet,
					startdato: i.startdato,
					faktiskSluttdato: i.faktiskSluttdato
				})
			})

			const instObj = {
				header: 'Institusjonsopphold',
				itemRows: []
			}

			flatInstKriterier.forEach((inst, i) => {
				instObj.itemRows.push([
					{
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					obj('Institusjonstype', Formatters.showLabel('institusjonstype', inst.institusjonstype)),
					obj('Varighet', inst.varighet && Formatters.showLabel('varighet', inst.varighet)),
					obj('Startdato', Formatters.formatDate(inst.startdato)),
					obj('Sluttdato', Formatters.formatDate(inst.faktiskSluttdato))
				])
			})
			data.push(instObj)
		}

		const udiStubKriterier = registreKriterier.udistub

		if (udiStubKriterier) {
			const oppholdKriterier = udiStubKriterier.oppholdStatus
			const arbeidsadgangKriterier = udiStubKriterier.arbeidsadgang

			const oppholdsrettTyper = [
				'eosEllerEFTABeslutningOmOppholdsrett',
				'eosEllerEFTAVedtakOmVarigOppholdsrett',
				'eosEllerEFTAOppholdstillatelse'
			]
			const currentOppholdsrettType =
				oppholdKriterier && oppholdsrettTyper.find(type => oppholdKriterier[type])

			const currentTredjelandsborgereStatus =
				oppholdKriterier && oppholdKriterier.oppholdSammeVilkaar
					? 'Oppholdstillatelse eller opphold på samme vilkår'
					: oppholdKriterier && oppholdKriterier.uavklart
						? 'Uavklart'
						: udiStubKriterier.harOppholdsTillatelse === false
							? 'Ikke oppholdstillatalse eller ikke opphold på samme vilkår'
							: null

			const oppholdsrett = Boolean(currentOppholdsrettType)
			const tredjelandsborger = Boolean(currentTredjelandsborgereStatus)

			let aliaserListe = []
			udiStubKriterier.aliaser &&
				udiStubKriterier.aliaser.forEach((alias, i) => {
					if (alias.nyIdent === false) {
						aliaserListe.push(`#${i + 1} Navn\n`)
					} else {
						aliaserListe.push(`#${i + 1} ID-nummer - ${alias.identtype}\n`)
					}
				})

			const udistub = {
				header: 'UDI',
				items: [
					obj(
						'Oppholdsstatus',
						oppholdsrett ? 'EØS-eller EFTA-opphold' : tredjelandsborger ? 'Tredjelandsborger' : null
					),
					obj(
						'Type opphold',
						oppholdsrett && Formatters.showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)
					),
					obj('Status', currentTredjelandsborgereStatus),
					obj(
						'Oppholdstillatelse fra dato',
						Formatters.formateStringDates(
							_get(oppholdKriterier, `${currentOppholdsrettType}Periode.fra`) ||
								_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra')
						)
					),
					obj(
						'Oppholdstillatelse til dato',
						Formatters.formateStringDates(
							_get(oppholdKriterier, `${currentOppholdsrettType}Periode.til`) ||
								_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til')
						)
					),
					obj(
						'Effektueringsdato',
						Formatters.formateStringDates(
							_get(oppholdKriterier, `${currentOppholdsrettType}Effektuering`) ||
								_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering')
						)
					),
					obj(
						'Grunnlag for opphold',
						oppholdsrett &&
							Formatters.showLabel(
								currentOppholdsrettType,
								oppholdKriterier[currentOppholdsrettType]
							)
					),
					obj(
						'Type oppholdstillatelse',
						Formatters.showLabel(
							'oppholdstillatelseType',
							_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdstillatelseType')
						)
					),
					obj(
						'Vedtaksdato',
						Formatters.formateStringDates(
							_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato')
						)
					),
					obj(
						'Har arbeidsadgang',
						Formatters.allCapsToCapitalized(
							arbeidsadgangKriterier && arbeidsadgangKriterier.harArbeidsAdgang
						)
					),
					obj(
						'Type arbeidsadgang',
						Formatters.showLabel(
							'typeArbeidsadgang',
							arbeidsadgangKriterier && arbeidsadgangKriterier.typeArbeidsadgang
						)
					),
					obj(
						'Arbeidsomfang',
						Formatters.showLabel(
							'arbeidsOmfang',
							arbeidsadgangKriterier && arbeidsadgangKriterier.arbeidsOmfang
						)
					),
					obj(
						'Arbeidsadgang fra dato',
						Formatters.formateStringDates(_get(arbeidsadgangKriterier, 'periode.fra'))
					),
					obj(
						'Arbeidsadgang til dato',
						Formatters.formateStringDates(_get(arbeidsadgangKriterier, 'periode.til'))
					),
					obj('Alias', aliaserListe.length > 0 && aliaserListe),
					obj('Flyktningstatus', Formatters.oversettBoolean(udiStubKriterier.flyktning)),
					obj(
						'Asylsøker',
						Formatters.showLabel(
							'jaNeiUavklart',
							udiStubKriterier.soeknadOmBeskyttelseUnderBehandling
						)
					)
				]
			}
			data.push(udistub)
		}
	}
	return data
}
