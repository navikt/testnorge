import _get from 'lodash/get'
import _has from 'lodash/has'
import _dropRight from 'lodash/dropRight'
import _takeRight from 'lodash/takeRight'
import _isEmpty from 'lodash/isEmpty'
import Formatters from '~/utils/DataFormatter'
import {
	AdresseKodeverk,
	ArbeidKodeverk,
	PersoninformasjonKodeverk,
	SigrunKodeverk,
	VergemaalKodeverk
} from '~/config/kodeverk'

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
		obj('Alder', data.alder),
		obj('Dødsdato', data.doedsdato === null ? 'Ingen' : Formatters.formatDate(data.doedsdato)),
		obj('Statsborgerskap', data.statsborgerskap, AdresseKodeverk.StatsborgerskapLand),
		obj('Statsborgerskap fra', Formatters.formatDate(data.statsborgerskapRegdato)),
		obj('Statsborgerskap til', Formatters.formatDate(data.statsborgerskapTildato)),
		obj('Kjønn', Formatters.kjonn(data.kjonn, data.alder)),
		obj('Har mellomnavn', Formatters.oversettBoolean(data.harMellomnavn)),
		obj('Sivilstand', data.sivilstand, PersoninformasjonKodeverk.Sivilstander),
		obj('Diskresjonskoder', data.spesreg !== 'UFB' && data.spesreg, 'Diskresjonskoder'),
		obj('Uten fast bopel', (data.utenFastBopel || data.spesreg === 'UFB') && 'JA'),
		obj('Språk', data.sprakKode, PersoninformasjonKodeverk.Spraak),
		obj('Innvandret fra land', data.innvandretFraLand, AdresseKodeverk.InnvandretUtvandretLand),
		obj('Innvandret dato', Formatters.formatDate(data.innvandretFraLandFlyttedato)),
		obj('Utvandret til land', data.utvandretTilLand, AdresseKodeverk.InnvandretUtvandretLand),
		obj('Utvandret dato', Formatters.formatDate(data.utvandretTilLandFlyttedato)),
		obj('Er forsvunnet', Formatters.oversettBoolean(data.erForsvunnet)),
		obj('Forsvunnet dato', Formatters.formatDate(data.forsvunnetDato)),
		obj('Har bankkontonummer', Formatters.oversettBoolean(data.harBankkontonr)),
		obj('Bankkonto opprettet', Formatters.formatDate(data.bankkontonrRegdato)),
		obj(
			data.telefonnummer_2 ? 'Telefonnummer 1' : 'Telefonnummer',
			data.telefonnummer_1 && `${data.telefonLandskode_1} ${data.telefonnummer_1}`
		),
		obj(
			'Telefonnummer 2',
			data.telefonnummer_2 && `${data.telefonLandskode_2} ${data.telefonnummer_2}`
		),
		obj('Skjerming fra', Formatters.formatDate(data.egenAnsattDatoFom)),
		obj('Skjerming til', Formatters.formatDate(data.egenAnsattDatoTom))
	]
}

export function mapBestillingData(bestillingData, bestillingsinformasjon) {
	if (!bestillingData) return null

	const data = []

	if (bestillingsinformasjon) {
		const bestillingsInfo = {
			header: 'Bestillingsinformasjon',
			items: [
				obj(
					'Antall',
					bestillingsinformasjon.antallIdenter && bestillingsinformasjon.antallIdenter.toString()
				),
				obj(
					'Type testperson',
					bestillingsinformasjon.navSyntetiskIdent ? 'NAV syntetisk' : 'Standard'
				),
				obj('Sist Oppdatert', Formatters.formatDate(bestillingsinformasjon.sistOppdatert)),
				obj(
					'Gjenopprettet fra',
					bestillingsinformasjon.opprettetFraId
						? `Bestilling # ${bestillingsinformasjon.opprettetFraId}`
						: bestillingsinformasjon.opprettetFraGruppeId &&
								`Gruppe # ${bestillingsinformasjon.opprettetFraGruppeId}`
				)
			]
		}
		data.push(bestillingsInfo)
	}

	if (bestillingData.tpsf) {
		const {
			boadresse,
			postadresse,
			midlertidigAdresse,
			adresseNrInfo,
			identHistorikk,
			relasjoner,
			vergemaal,
			fullmakt,
			...persondetaljer
		} = bestillingData.tpsf

		if (!_isEmpty(persondetaljer)) {
			const personinfo = {
				header: 'Persondetaljer',
				items: _getTpsfBestillingData(bestillingData.tpsf)
			}

			data.push(personinfo)
		}
		if (boadresse) {
			if (adresseNrInfo) {
				const adresseNrInfoObj = {
					header: `Boadresse basert på ${Formatters.showLabel(
						'adresseNrType',
						adresseNrInfo.nummertype
					)}`,
					items: [
						obj(
							`${Formatters.showLabel('adresseNrType', adresseNrInfo.nummertype)}`,
							adresseNrInfo.nummer ? adresseNrInfo.nummer : 'Tilfeldig'
						),
						obj('Bruksenhetsnummer', boadresse.bolignr),
						obj('Flyttedato', Formatters.formatDate(boadresse.flyttedato))
					]
				}
				data.push(adresseNrInfoObj)
			} else {
				const adresse = {
					header: 'Bostedadresse',
					items: [
						{
							header: 'Bosted'
						},
						obj('Adressetype', Formatters.adressetypeToString(boadresse.adressetype)),
						obj('Gatenavn', boadresse.gateadresse),
						obj('Husnummer', boadresse.husnummer),
						obj('Stedsnavn', boadresse.mellomnavn),
						obj('Gårdsnummer', boadresse.gardsnr),
						obj('Bruksnummer', boadresse.bruksnr),
						obj('Festenummer', boadresse.festenr),
						obj('Undernummer', boadresse.undernr),
						obj('Postnummer', boadresse.postnr),
						obj('Kommunenummer', boadresse.kommunenr),
						obj('Bruksenhetsnummer', boadresse.bolignr),
						obj('Flyttedato', Formatters.formatDate(boadresse.flyttedato))
					]
				}
				data.push(adresse)
			}
			if (boadresse.tilleggsadresse) {
				const tilleggsadresse = {
					header: 'Tilleggsadresse',
					items: [
						obj('Tilfeldig adresse', boadresse.tilleggsadresse === {} && 'Ja'),
						obj(
							'Tilleggstype',
							Formatters.showLabel('tilleggstype', boadresse.tilleggsadresse.tilleggType)
						),
						obj('Nummer', boadresse.tilleggsadresse.nummer)
					]
				}
				data.push(tilleggsadresse)
			}
		}

		if (postadresse) {
			const postadr = bestillingData.tpsf.postadresse[0]
			const postadresse = {
				header: 'Postadresse',
				items: [
					obj('Land', postadr.postLand),
					obj('Adresselinje 1', postadr.postLinje1),
					obj('Adresselinje 2', postadr.postLinje2),
					obj('Adresselinje 3', postadr.postLinje3)
				]
			}
			data.push(postadresse)
		}

		if (midlertidigAdresse) {
			let typeGateadresse = null
			if (midlertidigAdresse.adressetype === 'GATE') {
				if (midlertidigAdresse.gateadresseNrInfo) {
					typeGateadresse = `Tilfeldig, basert på ${Formatters.showLabel(
						'adresseNrType',
						midlertidigAdresse.gateadresseNrInfo.nummertype
					)}`
				} else if (midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.gatenavn) {
					typeGateadresse = 'Detaljert'
				} else {
					typeGateadresse = 'Tilfeldig'
				}
			}

			const midlertidigAdresseObj = {
				header: 'Midlertidig adresse',
				items: [
					obj('Adressetype', Formatters.showLabel('adresseType', midlertidigAdresse.adressetype)),
					obj('Gyldig t.o.m.', Formatters.formatDate(midlertidigAdresse.gyldigTom)),
					obj('Type gateadresse', typeGateadresse),
					obj(
						midlertidigAdresse.gateadresseNrInfo &&
							Formatters.showLabel(
								'adresseNrType',
								midlertidigAdresse.gateadresseNrInfo.nummertype
							),
						midlertidigAdresse.gateadresseNrInfo && midlertidigAdresse.gateadresseNrInfo.nummer
					),
					obj(
						'Gatenavn',
						midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.gatenavn
					),
					obj(
						'Husnummer',
						midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.husnr
					),
					obj(
						'Eiendomsnavn',
						midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.eiendomsnavn
					),
					obj(
						'Postboksnummer',
						midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.postboksnr
					),
					obj(
						'Postboksanlegg',
						midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.postboksAnlegg
					),
					obj(
						'Postnummer',
						midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.postnr
					),
					obj(
						'Postlinje 1',
						midlertidigAdresse.utenlandskAdresse && midlertidigAdresse.utenlandskAdresse.postLinje1
					),
					obj(
						'Postlinje 2',
						midlertidigAdresse.utenlandskAdresse && midlertidigAdresse.utenlandskAdresse.postLinje2
					),
					obj(
						'Postlinje 3',
						midlertidigAdresse.utenlandskAdresse && midlertidigAdresse.utenlandskAdresse.postLinje3
					),
					obj(
						'Land',
						midlertidigAdresse.utenlandskAdresse && midlertidigAdresse.utenlandskAdresse.postLand,
						AdresseKodeverk.PostadresseLand
					)
				]
			}
			data.push(midlertidigAdresseObj)

			if (midlertidigAdresse.norskAdresse && midlertidigAdresse.norskAdresse.tilleggsadresse) {
				const midlertidigTilleggsadresseObj = {
					header: 'Midlertidig tilleggsadresse',
					items: [
						obj(
							'Tilleggstype',
							Formatters.showLabel(
								'tilleggstypeMidlertidig',
								midlertidigAdresse.norskAdresse.tilleggsadresse.tilleggType
							)
						),
						obj('Nummer', midlertidigAdresse.norskAdresse.tilleggsadresse.nummer)
					]
				}
				data.push(midlertidigTilleggsadresseObj)
			}
		}

		if (identHistorikk) {
			const identhistorikkData = {
				header: 'Identhistorikk',
				itemRows: identHistorikk.map((ident, idx) => {
					return [
						{
							numberHeader: `Identhistorikk ${idx + 1}`
						},
						obj('Identtype', ident.identtype),
						obj('Kjønn', Formatters.kjonnToString(ident.kjonn)),
						obj('Utgått dato', Formatters.formatDate(ident.regdato)),
						obj('Født før', Formatters.formatDate(ident.foedtFoer)),
						obj('Født Etter', Formatters.formatDate(ident.foedtEtter))
					]
				})
			}
			data.push(identhistorikkData)
		}

		if (relasjoner) {
			const partnere = relasjoner.partner || relasjoner.partnere
			const barn = relasjoner.barn
			const foreldre = relasjoner.foreldre

			if (partnere) {
				const partner = {
					header: 'Partner',
					itemRows: []
				}

				partnere.forEach((item, j) => {
					const sivilstander = _get(item, 'sivilstander', [])
					const sisteSivilstand = _takeRight(sivilstander)

					const tidligereSivilstander = _dropRight(sivilstander)
						.reverse()
						.map(s => s.sivilstand)

					partner.itemRows.push([
						{
							label: '',
							value: `#${j + 1}`,
							width: 'x-small'
						},
						..._getTpsfBestillingData(item),
						obj('Fnr/dnr/bost', item.ident),
						obj('Bor sammen', Formatters.oversettBoolean(item.harFellesAdresse)),
						obj(
							'Sivilstand',
							sisteSivilstand.length > 0 && sisteSivilstand[0].sivilstand,
							PersoninformasjonKodeverk.Sivilstander
						),
						obj('Tidligere sivilstander', Formatters.arrayToString(tidligereSivilstander))
					])
				})

				data.push(partner)
			}

			if (barn && barn.length > 0) {
				const barn = {
					header: 'Barn',
					itemRows: []
				}

				relasjoner.barn.forEach((item, i) => {
					barn.itemRows.push([
						{
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						..._getTpsfBestillingData(item),
						obj('Fnr/dnr/bost', item.ident),
						obj('Forelder 2', item.partnerIdent),
						obj('Foreldre', Formatters.showLabel('barnType', item.barnType)), //Bruke samme funksjon som i bestillingsveileder
						obj('Bor hos', Formatters.showLabel('barnBorHos', item.borHos)),
						obj('Er adoptert', Formatters.oversettBoolean(item.erAdoptert)),
						obj('Fødselsdato', Formatters.formatDate(item.foedselsdato))
					])
				})

				data.push(barn)
			}
			if (foreldre && foreldre.length > 0) {
				const foreldreRows = {
					header: 'Foreldre',
					itemRows: []
				}

				relasjoner.foreldre.forEach((item, i) => {
					foreldreRows.itemRows.push([
						{
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						..._getTpsfBestillingData(item),
						obj('Fnr/dnr/bost', item.ident),
						obj('ForeldreType', Formatters.showLabel('foreldreType', item.foreldreType)),
						obj('Foreldre bor sammen', Formatters.oversettBoolean(item.harFellesAdresse)),
						obj('Diskresjonskoder', item.spesreg !== 'UFB' && item.spesreg, 'Diskresjonskoder'),
						obj('Fødselsdato', Formatters.formatDate(item.foedselsdato))
					])
				})

				data.push(foreldreRows)
			}
		}

		if (vergemaal) {
			const vergemaalKriterier = {
				header: 'Vergemål',
				items: [
					obj('Fylkesmannsembete', vergemaal.embete, VergemaalKodeverk.Fylkesmannsembeter),
					obj('Sakstype', vergemaal.sakType, VergemaalKodeverk.Sakstype),
					obj('Mandattype', vergemaal.mandatType, VergemaalKodeverk.Mandattype),
					obj('Vedtaksdato', Formatters.formatDate(vergemaal.vedtakDato)),
					obj('Verges identtype', vergemaal.identType),
					obj('Verge har mellomnavn', Formatters.oversettBoolean(vergemaal.harMellomnavn))
				]
			}
			data.push(vergemaalKriterier)
		}

		if (fullmakt) {
			const fullmaktKriterier = {
				header: 'Fullmakt',
				items: [
					obj('Kilde', fullmakt.kilde),
					obj('Områder', Formatters.omraaderArrayToString(fullmakt.omraader)),
					obj('Gyldig fra og med', Formatters.formatDate(fullmakt.gyldigFom)),
					obj('Gyldig til og med', Formatters.formatDate(fullmakt.gyldigTom)),
					obj('Fullmektiges identtype', fullmakt.identType),
					obj('Fullmektig har mellomnavn', Formatters.oversettBoolean(fullmakt.harMellomnavn))
				]
			}
			data.push(fullmaktKriterier)
		}
	}

	const aaregKriterier = bestillingData.aareg
	if (aaregKriterier) {
		const aareg = {
			header: 'Arbeidsforhold (Aareg)',
			itemRows: []
		}

		aaregKriterier.forEach((arbeidsforhold, i) => {
			aareg.itemRows.push([
				{
					numberHeader: `Arbeidsforhold ${i + 1}`
				},
				obj('Startdato', Formatters.formatDate(arbeidsforhold.ansettelsesPeriode.fom)),
				obj('Sluttdato', Formatters.formatDate(arbeidsforhold.ansettelsesPeriode.tom)),
				{
					label: 'Type arbeidsforhold',
					value: arbeidsforhold.arbeidsforholdstype,
					apiKodeverkId: ArbeidKodeverk.Arbeidsforholdstyper
				},
				obj('Type av arbeidsgiver', arbeidsforhold.arbeidsgiver.aktoertype),
				obj('Orgnummer', arbeidsforhold.arbeidsgiver.orgnummer),
				obj('Arbeidsgiver Ident', arbeidsforhold.arbeidsgiver.ident),
				{
					label: 'Yrke',
					value: arbeidsforhold.arbeidsavtale.yrke,
					apiKodeverkId: ArbeidKodeverk.Yrker,
					width: 'xlarge',
					showKodeverkValue: true
				},
				obj(
					'Stillingprosent',
					arbeidsforhold.arbeidsavtale.stillingsprosent === 0
						? '0'
						: arbeidsforhold.arbeidsavtale.stillingsprosent
				),
				obj(
					'Endringsdato stillingprosent',
					Formatters.formatDate(arbeidsforhold.arbeidsavtale.endringsdatoStillingsprosent)
				),
				{
					label: 'Arbeidstidsordning',
					value: arbeidsforhold.arbeidsavtale.arbeidstidsordning,
					apiKodeverkId: ArbeidKodeverk.Arbeidstidsordninger
				},
				obj('Antall konverterte timer', arbeidsforhold.arbeidsavtale.antallKonverterteTimer),
				obj('Avtalte timer per uke', arbeidsforhold.arbeidsavtale.avtaltArbeidstimerPerUke),
				obj(
					'Perioder med antall timer for timelønnet',
					arbeidsforhold.permisjon && arbeidsforhold.permisjon.length
				),
				obj('Perioder med permisjon', arbeidsforhold.permisjon && arbeidsforhold.permisjon.length),
				obj(
					'Perioder med utenlandsopphold',
					arbeidsforhold.utenlandsopphold && arbeidsforhold.utenlandsopphold.length
				)
			])
		})
		data.push(aareg)
	}
	const sigrunStubKriterier = bestillingData.sigrunstub

	if (sigrunStubKriterier) {
		// Flatter ut sigrunKriterier for å gjøre det lettere å mappe
		const flatSigrunStubKriterier = []
		sigrunStubKriterier.forEach(inntekt => {
			const inntektObj = { inntektsaar: inntekt.inntektsaar, tjeneste: inntekt.tjeneste }
			inntekt.grunnlag &&
				inntekt.grunnlag.forEach(gr => {
					flatSigrunStubKriterier.push({
						...inntektObj,
						grunnlag: gr.tekniskNavn,
						verdi: gr.verdi,
						inntektssted: 'Fastlands-Norge'
					})
				})
			inntekt.svalbardGrunnlag &&
				inntekt.svalbardGrunnlag.forEach(gr => {
					flatSigrunStubKriterier.push({
						...inntektObj,
						svalbardGrunnlag: gr.tekniskNavn,
						verdi: gr.verdi,
						inntektssted: 'Svalbard'
					})
				})
		})

		const sigrunStub = {
			header: 'Skatteoppgjør (Sigrun)',
			itemRows: []
		}

		flatSigrunStubKriterier.forEach((inntekt, i) => {
			sigrunStub.itemRows.push([
				{
					numberHeader: `Inntekt ${i + 1}`
				},
				obj('År', inntekt.inntektsaar),
				obj('Beløp', inntekt.verdi),
				obj('Tjeneste', Formatters.uppercaseAndUnderscoreToCapitalized(inntekt.tjeneste)),
				{
					label: 'Grunnlag (Fastlands-Norge)',
					value: inntekt.grunnlag,
					width: 'xlarge',
					apiKodeverkId: SigrunKodeverk[inntekt.tjeneste]
				},
				{
					label: 'Grunnlag (Svalbard)',
					value: inntekt.svalbardGrunnlag,
					width: 'xlarge',
					apiKodeverkId: SigrunKodeverk[inntekt.tjeneste]
				}
			])
		})

		data.push(sigrunStub)
	}

	const inntektStubKriterier = bestillingData.inntektstub

	if (inntektStubKriterier) {
		const inntektStub = {
			header: 'A-ordningen (Inntektskomponenten)',
			// items: [
			// 	obj('Prosentøkning per år', inntektStubKriterier.prosentOekningPerAaar)
			// ],
			itemRows: []
		}

		inntektStubKriterier.inntektsinformasjon &&
			inntektStubKriterier.inntektsinformasjon.forEach((inntektsinfo, i) => {
				inntektStub.itemRows.push([
					{ numberHeader: `Inntektsinformasjon ${i + 1}` },
					obj('År/måned', inntektsinfo.sisteAarMaaned),
					obj('Generer antall måneder', inntektsinfo.antallMaaneder),
					obj('Virksomhet (orgnr/id)', inntektsinfo.virksomhet),
					obj('Opplysningspliktig (orgnr/id)', inntektsinfo.opplysningspliktig),
					obj(
						'Antall registrerte inntekter',
						inntektsinfo.inntektsliste && inntektsinfo.inntektsliste.length
					),
					obj(
						'Antall registrerte fradrag',
						inntektsinfo.fradragsliste && inntektsinfo.fradragsliste.length
					),
					obj(
						'Antall registrerte forskuddstrekk',
						inntektsinfo.forskuddstrekksliste && inntektsinfo.forskuddstrekksliste.length
					),
					obj(
						'Antall registrerte arbeidsforhold',
						inntektsinfo.arbeidsforholdsliste && inntektsinfo.arbeidsforholdsliste.length
					),
					obj(
						'Antall registrerte inntektsendringer (historikk)',
						inntektsinfo.historikk && inntektsinfo.historikk.length
					)
				])
			})

		data.push(inntektStub)
	}

	const sykemeldingKriterier = _get(bestillingData, 'sykemelding')

	if (sykemeldingKriterier) {
		const sykemelding = {
			header: 'Sykemelding',
			items: sykemeldingKriterier.syntSykemelding
				? [
						obj('Startdato', Formatters.formatDate(sykemeldingKriterier.syntSykemelding.startDato)),
						obj('Organisasjonsnummer', sykemeldingKriterier.syntSykemelding.orgnummer),
						obj('Arbeidsforhold-ID', sykemeldingKriterier.syntSykemelding.arbeidsforholdId)
				  ]
				: sykemeldingKriterier.detaljertSykemelding
				? [
						obj(
							'Startdato',
							Formatters.formatDate(sykemeldingKriterier.detaljertSykemelding.startDato)
						),
						obj(
							'Trenger umiddelbar bistand',
							sykemeldingKriterier.detaljertSykemelding.umiddelbarBistand ? 'JA' : 'NEI'
						),
						obj(
							'Manglende tilrettelegging på arbeidsplassen',
							sykemeldingKriterier.detaljertSykemelding.manglendeTilretteleggingPaaArbeidsplassen
								? 'JA'
								: 'NEI'
						),
						obj(
							'Diagnose',
							_get(sykemeldingKriterier.detaljertSykemelding, 'hovedDiagnose.diagnose')
						),
						obj(
							'Diagnosekode',
							_get(sykemeldingKriterier.detaljertSykemelding, 'hovedDiagnose.diagnosekode')
						),
						obj(
							'Antall registrerte bidiagnoser',
							sykemeldingKriterier.detaljertSykemelding.biDiagnoser &&
								sykemeldingKriterier.detaljertSykemelding.biDiagnoser.length
						),
						obj(
							'Helsepersonell navn',
							sykemeldingKriterier.detaljertSykemelding.helsepersonell &&
								`${sykemeldingKriterier.detaljertSykemelding.helsepersonell.fornavn} ${
									sykemeldingKriterier.detaljertSykemelding.helsepersonell.mellomnavn
										? sykemeldingKriterier.detaljertSykemelding.helsepersonell.mellomnavn
										: ''
								} ${sykemeldingKriterier.detaljertSykemelding.helsepersonell.etternavn}`
						),
						obj(
							'Helsepersonell ident',
							_get(sykemeldingKriterier.detaljertSykemelding, 'helsepersonell.ident')
						),
						obj(
							'HPR-nummer',
							_get(sykemeldingKriterier.detaljertSykemelding, 'helsepersonell.hprId')
						),
						obj(
							'SamhandlerType',
							_get(sykemeldingKriterier.detaljertSykemelding, 'helsepersonell.samhandlerType')
						),
						obj(
							'Arbeidsgiver',
							_get(sykemeldingKriterier.detaljertSykemelding, 'arbeidsgiver.navn')
						),
						obj(
							'Yrkesbetegnelse',
							_get(sykemeldingKriterier.detaljertSykemelding, 'arbeidsgiver.yrkesbetegnelse'),
							ArbeidKodeverk.Yrker
						),
						obj(
							'Stillingsprosent',
							_get(sykemeldingKriterier.detaljertSykemelding, 'arbeidsgiver.stillingsprosent')
						),
						obj(
							'Antall registrerte perioder',
							sykemeldingKriterier.detaljertSykemelding.perioder.length
						),
						obj(
							'Tiltak fra NAV',
							_get(sykemeldingKriterier.detaljertSykemelding, 'detaljer.tiltakNav')
						),
						obj(
							'Tiltak på arbeidsplass',
							_get(sykemeldingKriterier.detaljertSykemelding, 'detaljer.tiltakArbeidsplass')
						),
						obj(
							'Hensyn på arbeidsplass',
							_get(
								sykemeldingKriterier.detaljertSykemelding,
								'detaljer.beskrivHensynArbeidsplassen'
							)
						),
						obj(
							'Arbeidsfør etter endt periode',
							sykemeldingKriterier.detaljertSykemelding.detaljer &&
								(sykemeldingKriterier.detaljertSykemelding.detaljer.arbeidsforEtterEndtPeriode
									? 'JA'
									: 'NEI')
						)
				  ]
				: null
		}
		data.push(sykemelding)
	}

	const brregstubKriterier = bestillingData.brregstub

	if (brregstubKriterier) {
		const brregstub = {
			header: 'Brønnøysundregistrene',
			items: [obj('Understatuser', Formatters.arrayToString(brregstubKriterier.understatuser))],
			itemRows: []
		}
		brregstubKriterier.enheter.forEach((enhet, i) => {
			brregstub.itemRows.push([
				{ numberHeader: `Enhet ${i + 1}` },
				obj('Rolle', enhet.rolle),
				obj('Registreringsdato', Formatters.formatDate(enhet.registreringsdato)),
				obj('Organisasjonsnummer', enhet.orgNr),
				obj('Foretaksnavn', enhet.foretaksNavn.navn1),
				obj('Antall registrerte personroller', enhet.personroller && enhet.personroller.length)
			])
		})

		data.push(brregstub)
	}

	const krrKriterier = bestillingData.krrstub

	if (krrKriterier) {
		const krrStub = {
			header: 'Kontaktinformasjon og reservasjon',
			items: [
				obj('Registrert i KRR', krrKriterier.registrert ? 'JA' : 'NEI'),
				{
					label: 'RESERVERT MOT DIGITALKOMMUNIKASJON',
					value: krrKriterier.reservert === null ? null : krrKriterier.reservert ? 'JA' : 'NEI',
					width: 'medium'
				},
				obj('Epost', krrKriterier.epost),
				obj('Mobilnummer', krrKriterier.mobil),
				obj('Språk', Formatters.showLabel('spraaktype', krrKriterier.spraak)),
				obj('Gyldig fra', Formatters.formatDate(krrKriterier.gyldigFra)),
				obj('Adresse', krrKriterier.sdpAdresse),
				obj('Leverandør', krrKriterier.sdpLeverandoer)
			]
		}

		data.push(krrStub)
	}

	const pdlforvalterKriterier = bestillingData.pdlforvalter

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
					obj('Land', doedsboKriterier.landkode, AdresseKodeverk.PostadresseLand),
					obj('Skifteform', doedsboKriterier.skifteform),
					obj('Dato utstedt', Formatters.formatDate(doedsboKriterier.utstedtDato)),
					obj('Gyldig fra', Formatters.formatDate(doedsboKriterier.gyldigFom)),
					obj('Gyldig til', Formatters.formatDate(doedsboKriterier.gyldigTom))
				]
			}
			data.push(doedsbo)
		}

		if (pdlforvalterKriterier.utenlandskIdentifikasjonsnummer) {
			const uidnr = pdlforvalterKriterier.utenlandskIdentifikasjonsnummer

			const flatUidnrKriterier = []
			uidnr.forEach(ui => {
				flatUidnrKriterier.push({
					identifikasjonsnummer: ui.identifikasjonsnummer,
					kilde: ui.kilde,
					opphoert: ui.opphoert,
					utstederland: ui.utstederland
				})
			})

			const uidnrObj = {
				header: 'Utenlandsk identifikasjonsnummer',
				itemRows: []
			}

			flatUidnrKriterier.forEach((uidr, i) => {
				uidnrObj.itemRows.push([
					{
						numberHeader: `Utenlandsk identifikasjonsnummer ${i + 1}`
					},
					obj('Utenlandsk ID', uidr.identifikasjonsnummer),
					obj('Kilde', uidr.kilde),
					obj('Utenlandsk ID opphørt', Formatters.oversettBoolean(uidr.opphoert)),
					obj('Utstederland', uidr.utstederland, AdresseKodeverk.Utstederland)
				])
			})
			data.push(uidnrObj)
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
			} else if (falskIdData.identitetType === 'OMTRENTLIG') {
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
	const arenaKriterier = bestillingData.arenaforvalter

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
				obj('Fra dato', arenaKriterier.aap && Formatters.formatDate(arenaKriterier.aap[0].fraDato)),
				obj('Til dato', arenaKriterier.aap && Formatters.formatDate(arenaKriterier.aap[0].tilDato))
			]
		}
		data.push(arenaforvalter)
	}

	const instKriterier = bestillingData.instdata

	if (instKriterier) {
		// Flater ut instKriterier for å gjøre det lettere å mappe

		const flatInstKriterier = []
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
					numberHeader: `Institusjonsopphold ${i + 1}`
				},
				obj('Institusjonstype', Formatters.showLabel('institusjonstype', inst.institusjonstype)),
				obj('Varighet', inst.varighet && Formatters.showLabel('varighet', inst.varighet)),
				obj('Startdato', Formatters.formatDate(inst.startdato)),
				obj('Sluttdato', Formatters.formatDate(inst.faktiskSluttdato))
			])
		})
		data.push(instObj)
	}

	const udiStubKriterier = bestillingData.udistub

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
				? 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår'
				: null

		const oppholdsrett = Boolean(currentOppholdsrettType)
		const tredjelandsborger = Boolean(currentTredjelandsborgereStatus)

		const aliaserListe = []
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
					Formatters.formatDate(
						_get(oppholdKriterier, `${currentOppholdsrettType}Periode.fra`) ||
							_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra')
					)
				),
				obj(
					'Oppholdstillatelse til dato',
					Formatters.formatDate(
						_get(oppholdKriterier, `${currentOppholdsrettType}Periode.til`) ||
							_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til')
					)
				),
				obj(
					'Effektueringsdato',
					Formatters.formatDate(
						_get(oppholdKriterier, `${currentOppholdsrettType}Effektuering`) ||
							_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering')
					)
				),
				obj(
					'Grunnlag for opphold',
					oppholdsrett &&
						Formatters.showLabel(currentOppholdsrettType, oppholdKriterier[currentOppholdsrettType])
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
					Formatters.formatDate(
						_get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato')
					)
				),
				obj(
					'Avgjørelsesdato',
					Formatters.formatDate(
						_get(
							oppholdKriterier,
							'ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avgjorelsesDato'
						)
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
					Formatters.formatDate(_get(arbeidsadgangKriterier, 'periode.fra'))
				),
				obj(
					'Arbeidsadgang til dato',
					Formatters.formatDate(_get(arbeidsadgangKriterier, 'periode.til'))
				),
				obj('Hjemmel', _get(arbeidsadgangKriterier, 'hjemmel')),
				obj('Forklaring', _get(arbeidsadgangKriterier, 'forklaring')),
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

	const pensjonKriterier = bestillingData.pensjonforvalter

	if (pensjonKriterier) {
		const pensjonforvalter = {
			header: 'Pensjonsgivende inntekt (POPP)',
			items: [
				obj('Fra og med år', pensjonKriterier.inntekt.fomAar),
				obj('Til og med år', pensjonKriterier.inntekt.tomAar),
				obj('Beløp', pensjonKriterier.inntekt.belop),
				obj(
					'Nedjuster med grunnbeløp',
					Formatters.oversettBoolean(pensjonKriterier.inntekt.redusertMedGrunnbelop)
				)
			]
		}

		data.push(pensjonforvalter)
	}

	const inntektsmeldingKriterier = bestillingData.inntektsmelding

	const mapInntektsmeldingKriterier = meldinger => ({
		header: 'Inntektsmelding (fra Altinn)',
		itemRows: meldinger.map((inntekt, i) => [
			{
				numberHeader: `Inntekt ${i + 1}`
			},
			obj('Årsak til innsending', Formatters.codeToNorskLabel(inntekt.aarsakTilInnsending)),
			obj('Ytelse', Formatters.codeToNorskLabel(inntekt.ytelse)),
			obj('Nær relasjon', Formatters.oversettBoolean(inntekt.naerRelasjon)),
			obj(
				'Innsendingstidspunkt',
				Formatters.formatDate(inntekt.avsendersystem.innsendingstidspunkt)
			),

			obj('Arbeidsgiver (orgnr)', inntekt.arbeidsgiver && inntekt.arbeidsgiver.virksomhetsnummer),
			obj(
				'Arbeidsgiver (fnr/dnr/bost)',
				inntekt.arbeidsgiverPrivat && inntekt.arbeidsgiverPrivat.arbeidsgiverFnr
			),
			obj('Arbeidsforhold-ID', inntekt.arbeidsforhold.arbeidsforholdId),
			obj('Beløp', inntekt.arbeidsforhold.beregnetInntekt.beloep),
			obj(
				'Årsak ved endring',
				Formatters.codeToNorskLabel(inntekt.arbeidsforhold.aarsakVedEndring)
			),
			obj('Første fraværsdag', Formatters.formatDate(inntekt.arbeidsforhold.foersteFravaersdag)),
			obj(
				'Avtalte ferier',
				inntekt.arbeidsforhold.avtaltFerieListe && inntekt.arbeidsforhold.avtaltFerieListe.length
			),
			//Refusjon
			obj('Refusjonsbeløp per måned', inntekt.refusjon.refusjonsbeloepPrMnd),
			obj('Opphørsdato refusjon', Formatters.formatDate(inntekt.refusjon.refusjonsopphoersdato)),
			obj(
				'Endring i refusjon',
				_has(inntekt, 'refusjon.endringIRefusjonListe') && inntekt.refusjon.endringIRefusjonListe
			),
			//Omsorg
			obj('Har utbetalt pliktige dager', _get(inntekt, 'omsorgspenger.harUtbetaltPliktigeDager')),
			obj(
				'Fraværsperioder',
				_has(inntekt, 'omsorgspenger.fravaersPerioder') &&
					inntekt.omsorgspenger.fravaersPerioder.length
			),
			obj(
				'Delvis fravær',
				_has(inntekt, 'omsorgspenger.delvisFravaersListe') &&
					inntekt.omsorgspenger.delvisFravaersListe.length
			),
			//Sykepenger
			obj('Brutto utbetalt', _get(inntekt, 'sykepengerIArbeidsgiverperioden.bruttoUtbetalt')),
			obj(
				'Begrunnelse for reduksjon eller ikke utbetalt',
				Formatters.codeToNorskLabel(
					_get(inntekt, 'sykepengerIArbeidsgiverperioden.begrunnelseForReduksjonEllerIkkeUtbetalt')
				)
			),
			obj(
				'Arbeidsgiverperioder',
				_has(inntekt, 'sykepengerIArbeidsgiverperioden.arbeidsgiverperiodeListe') &&
					inntekt.sykepengerIArbeidsgiverperioden.arbeidsgiverperiodeListe.length
			),
			//Foreldrepenger
			obj('Startdato foreldrepenger', Formatters.formatDate(inntekt.startdatoForeldrepengeperiode)),
			//Pleiepenger
			obj('Pleiepengerperioder', inntekt.pleiepengerPerioder && inntekt.pleiepengerPerioder.length),
			//Naturalytelse
			obj(
				'Gjenopptagelse Naturalytelse',
				inntekt.gjenopptakelseNaturalytelseListe && inntekt.gjenopptakelseNaturalytelseListe.length
			),
			obj(
				'Opphør av Naturalytelse',
				inntekt.opphoerAvNaturalytelseListe && inntekt.opphoerAvNaturalytelseListe.length
			)
		])
	})

	const tomInntektsmelding = {
		header: 'Inntektsmelding (fra Altinn)',
		items: [obj('Inntektsmelding', 'Tom bestilling')]
	}

	if (inntektsmeldingKriterier) {
		if (_isEmpty(inntektsmeldingKriterier.inntekter)) {
			data.push(tomInntektsmelding)
		} else data.push(mapInntektsmeldingKriterier(inntektsmeldingKriterier.inntekter))
	}

	const dokarkivKriterier = bestillingData.dokarkiv

	if (dokarkivKriterier) {
		const dokarkiv = {
			header: 'Dokumenter',
			items: [
				obj('Brevkode', dokarkivKriterier.dokumenter[0].brevkode),
				obj('Tittel', dokarkivKriterier.tittel),
				obj('Tema', dokarkivKriterier.tema),
				obj('Journalførende enhet', dokarkivKriterier.journalfoerendeEnhet)
			]
		}

		data.push(dokarkiv)
	}

	const organisasjonKriterier = bestillingData.organisasjon

	if (organisasjonKriterier) {
		const forretningsadresse = organisasjonKriterier[0].forretningsadresse
		const postadresse = organisasjonKriterier[0].postadresse
		const organisasjon = {
			header: 'Organisasjonsdetaljer',
			items: [
				obj('Enhetstype', organisasjonKriterier[0].enhetstype),
				obj('Næringskode', organisasjonKriterier[0].naeringskode),
				obj('Sektorkode', organisasjonKriterier[0].sektorkode),
				obj('Formål', organisasjonKriterier[0].formaal),
				obj('Stiftelsesdato', Formatters.formatDate(organisasjonKriterier[0].stiftelsesdato)),
				obj('Målform', Formatters.showLabel('maalform', organisasjonKriterier[0].maalform)),
				obj('Telefon', organisasjonKriterier[0].telefon),
				obj('E-postadresse', organisasjonKriterier[0].epost),
				obj('Internettadresse', organisasjonKriterier[0].nettside)
			]
		}

		const forretningsadresseKriterier = {
			header: 'Forretningsadresse',
			items: [
				obj('Land', forretningsadresse && forretningsadresse.landkode),
				obj('Postnummer', forretningsadresse && forretningsadresse.postnr),
				obj('Poststed', forretningsadresse && forretningsadresse.poststed),
				obj('Kommunenummer', forretningsadresse && forretningsadresse.kommunenr),
				obj('Adresselinje 1', forretningsadresse && forretningsadresse.adresselinjer[0]),
				obj('Adresselinje 2', forretningsadresse && forretningsadresse.adresselinjer[1]),
				obj('Adresselinje 3', forretningsadresse && forretningsadresse.adresselinjer[2])
			]
		}

		const postadresseKriterier = {
			header: 'Postadresse',
			items: [
				obj('Land', postadresse && postadresse.landkode),
				obj('Postnummer', postadresse && postadresse.postnr),
				obj('Poststed', postadresse && postadresse.poststed),
				obj('Kommunenummer', postadresse && postadresse.kommunenr),
				obj('Adresselinje 1', postadresse && postadresse.adresselinjer[0]),
				obj('Adresselinje 2', postadresse && postadresse.adresselinjer[1]),
				obj('Adresselinje 3', postadresse && postadresse.adresselinjer[2])
			]
		}

		data.push(organisasjon)
		forretningsadresse && data.push(forretningsadresseKriterier)
		postadresse && data.push(postadresseKriterier)
	}

	const importFraTps = bestillingData.importFraTps

	if (importFraTps) {
		const importData = {
			header: 'Import',
			items: [
				obj('Identer', Formatters.arrayToString(importFraTps)),
				obj('Importert fra', bestillingData.kildeMiljoe.toUpperCase())
			]
		}

		data.push(importData)
	}

	return data
}
