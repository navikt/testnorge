import _get from 'lodash/get'
import _has from 'lodash/has'
import _isEmpty from 'lodash/isEmpty'
import Formatters from '~/utils/DataFormatter'
import {
	AdresseKodeverk,
	ArbeidKodeverk,
	PersoninformasjonKodeverk,
	SigrunKodeverk,
	VergemaalKodeverk,
} from '~/config/kodeverk'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'

// TODO: Flytte til selector?
// - Denne kan forminskes ved bruk av hjelpefunksjoner
// - Når vi får på plass en bedre struktur for bestillingsprosessen, kan
//   mest sannsynlig visse props fjernes herfra (width?)

const obj = (label, value, apiKodeverkId) => ({
	label,
	value,
	...(apiKodeverkId && { apiKodeverkId }),
})

const expandable = (expandableHeader, vis, objects) => ({
	expandableHeader,
	vis,
	objects,
})

const _getTpsfBestillingData = (data) => {
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
		obj('Har nytt navn', Formatters.oversettBoolean(data.harNyttNavn)),
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
		obj('Skjerming fra', Formatters.formatDate(data.egenAnsattDatoFom)),
		obj('Skjerming til', Formatters.formatDate(data.egenAnsattDatoTom)),
		obj(
			'Type sikkerhetstiltak',
			data.beskrSikkerhetTiltak === 'Opphørt'
				? data.beskrSikkerhetTiltak
				: data.typeSikkerhetTiltak && `${data.typeSikkerhetTiltak} - ${data.beskrSikkerhetTiltak}`
		),
		obj('Sikkerhetstiltak starter', Formatters.formatDate(data.sikkerhetTiltakDatoFom)),
		obj('Sikkerhetstiltak opphører', Formatters.formatDate(data.sikkerhetTiltakDatoTom)),
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
				obj('Type person', bestillingsinformasjon.navSyntetiskIdent ? 'NAV syntetisk' : 'Standard'),
				obj('Sist Oppdatert', Formatters.formatDate(bestillingsinformasjon.sistOppdatert)),
				obj(
					'Gjenopprettet fra',
					bestillingsinformasjon.opprettetFraId
						? `Bestilling # ${bestillingsinformasjon.opprettetFraId}`
						: bestillingsinformasjon.opprettetFraGruppeId &&
								`Gruppe # ${bestillingsinformasjon.opprettetFraGruppeId}`
				),
			],
		}
		data.push(bestillingsInfo)
	}

	if (bestillingData.tpsf) {
		const { identHistorikk, relasjoner, vergemaal, fullmakt, harIngenAdresse, ...persondetaljer } =
			bestillingData.tpsf

		if (!_isEmpty(persondetaljer)) {
			const personinfo = {
				header: 'Persondetaljer',
				items: _getTpsfBestillingData(bestillingData.tpsf),
			}

			data.push(personinfo)
		}

		if (identHistorikk) {
			const identhistorikkData = {
				header: 'Identhistorikk',
				itemRows: identHistorikk.map((ident, idx) => {
					return [
						{
							numberHeader: `Identhistorikk ${idx + 1}`,
						},
						obj('Identtype', ident.identtype),
						obj('Kjønn', Formatters.kjonnToString(ident.kjonn)),
						obj('Utgått dato', Formatters.formatDate(ident.regdato)),
						obj('Født før', Formatters.formatDate(ident.foedtFoer)),
						obj('Født Etter', Formatters.formatDate(ident.foedtEtter)),
					]
				}),
			}
			data.push(identhistorikkData)
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
					obj('Verge har mellomnavn', Formatters.oversettBoolean(vergemaal.harMellomnavn)),
				],
			}
			data.push(vergemaalKriterier)
		}
	}

	const pdldataKriterier = bestillingData.pdldata?.person
	const pdlNyPersonKriterier = bestillingData.pdldata?.opprettNyPerson

	if (pdlNyPersonKriterier) {
		const { alder, foedtEtter, foedtFoer } = pdlNyPersonKriterier
		const nyPersonData = {
			header: 'Persondetaljer',
			items: [
				obj('Alder', alder),
				obj('Født etter', Formatters.formatDate(foedtEtter)),
				obj('Født før', Formatters.formatDate(foedtFoer)),
			],
		}
		if (alder || foedtEtter || foedtFoer) data.push(nyPersonData)
	}

	if (pdldataKriterier) {
		const {
			foedsel,
			kjoenn,
			navn,
			telefonnummer,
			fullmakt,
			bostedsadresse,
			oppholdsadresse,
			kontaktadresse,
			adressebeskyttelse,
			falskIdentitet,
			utenlandskIdentifikasjonsnummer,
			innflytting,
			utflytting,
			kontaktinformasjonForDoedsbo,
			doedsfall,
			statsborgerskap,
			sikkerhetstiltak,
			tilrettelagtKommunikasjon,
			sivilstand,
			vergemaal,
			forelderBarnRelasjon,
			doedfoedtBarn,
			foreldreansvar,
			nyident,
		} = pdldataKriterier

		const personRelatertTil = (personData, path) => {
			if (!personData || !_get(personData, path)) return [expandable(null, false, null)]
			const {
				identtype,
				kjoenn,
				foedtEtter,
				foedtFoer,
				alder,
				statsborgerskapLandkode,
				gradering,
				syntetisk,
				nyttNavn,
			} = _get(personData, path)

			return [
				expandable('PERSON RELATERT TIL', !isEmpty(_get(personData, path)), [
					obj('Identtype', identtype),
					obj('Kjønn', kjoenn),
					obj('Født etter', Formatters.formatDate(foedtEtter)),
					obj('Født før', Formatters.formatDate(foedtFoer)),
					obj('Alder', alder),
					obj('Statsborgerskap', statsborgerskapLandkode, AdresseKodeverk.StatsborgerskapLand),
					obj('Gradering', Formatters.showLabel('gradering', gradering)),
					obj('Er syntetisk', syntetisk && 'JA'),
					obj('Har mellomnavn', nyttNavn?.hasMellomnavn && 'JA'),
				]),
			]
		}

		if (foedsel) {
			const foedselData = {
				header: 'Fødsel',
				itemRows: foedsel.map((item, idx) => {
					return [
						{ numberHeader: `Fødsel ${idx + 1}` },
						obj('Fødselsdato', Formatters.formatDate(item.foedselsdato)),
						obj('Fødselsår', item.foedselsaar),
						obj('Fødested', item.foedested),
						obj('Fødekommune', item.foedekommune, AdresseKodeverk.Kommunenummer),
						obj('Fødeland', item.foedeland, AdresseKodeverk.InnvandretUtvandretLand),
					]
				}),
			}
			data.push(foedselData)
		}

		if (innflytting) {
			const innflyttingData = {
				header: 'Innvandring',
				itemRows: innflytting.map((item, idx) => {
					return [
						{ numberHeader: `Innvandring ${idx + 1}` },
						obj('Fraflyttingsland', item.fraflyttingsland, AdresseKodeverk.InnvandretUtvandretLand),
						obj('Fraflyttingssted', item.fraflyttingsstedIUtlandet),
						obj('Fraflyttingsdato', Formatters.formatDate(item.innflyttingsdato)),
					]
				}),
			}
			data.push(innflyttingData)
		}

		if (utflytting) {
			const utflyttingData = {
				header: 'Utvandring',
				itemRows: utflytting.map((item, idx) => {
					return [
						{ numberHeader: `Utvandring ${idx + 1}` },
						obj('Tilflyttingsland', item.tilflyttingsland, AdresseKodeverk.InnvandretUtvandretLand),
						obj('Tilflyttingssted', item.tilflyttingsstedIUtlandet),
						obj('Utvandringsdato', Formatters.formatDate(item.utflyttingsdato)),
					]
				}),
			}
			data.push(utflyttingData)
		}

		if (kjoenn) {
			const kjoennData = {
				header: 'Kjønn',
				itemRows: kjoenn.map((item, idx) => {
					return [
						{ numberHeader: `Kjønn ${idx + 1}` },
						obj('Kjønn', Formatters.showLabel('kjoenn', item.kjoenn)),
					]
				}),
			}
			data.push(kjoennData)
		}

		if (navn) {
			const navnData = {
				header: 'Navn',
				itemRows: navn.map((item, idx) => {
					return [
						{ numberHeader: `Navn ${idx + 1}` },
						obj('Fornavn', item.fornavn),
						obj('Mellomnavn', item.mellomnavn),
						obj('Etternavn', item.etternavn),
						obj('Har tilfeldig mellomnavn', Formatters.oversettBoolean(item.hasMellomnavn)),
					]
				}),
			}
			data.push(navnData)
		}

		if (telefonnummer) {
			const telefonnummerData = {
				header: 'Telefonnummer',
				itemRows: telefonnummer.map((item, idx) => {
					return [
						{ numberHeader: `Telefonnummer ${idx + 1}` },
						obj('Telefonnummer', `${item.landskode} ${item.nummer}`),
						obj('Prioritet', item.prioritet),
					]
				}),
			}
			data.push(telefonnummerData)
		}

		if (vergemaal) {
			const vergemaalData = {
				header: 'Vergemål',
				itemRows: vergemaal.map((item, idx) => {
					return [
						{ numberHeader: `Vergemål ${idx + 1}` },
						obj('Fylkesmannsembete', item.vergemaalEmbete, VergemaalKodeverk.Fylkesmannsembeter),
						obj('Sakstype', item.sakType, VergemaalKodeverk.Sakstype),
						obj('Mandattype', item.mandatType, VergemaalKodeverk.Mandattype),
						obj('Gyldig f.o.m.', Formatters.formatDate(item.gyldigFraOgMed)),
						obj('Gyldig t.o.m.', Formatters.formatDate(item.gyldigTilOgMed)),
						obj('Verge', item.vergeIdent),
						...personRelatertTil(item, 'nyVergeIdent'),
					]
				}),
			}
			data.push(vergemaalData)
		}

		if (fullmakt) {
			const fullmaktData = {
				header: 'Fullmakt',
				itemRows: fullmakt.map((item, idx) => {
					return [
						{ numberHeader: `Fullmakt ${idx + 1}` },
						obj('Områder', Formatters.omraaderArrayToString(item.omraader)),
						obj('Gyldig fra og med', Formatters.formatDate(item.gyldigFraOgMed)),
						obj('Gyldig til og med', Formatters.formatDate(item.gyldigTilOgMed)),
						obj('Fullmektig', item.motpartsPersonident),
						...personRelatertTil(item, 'nyFullmektig'),
					]
				}),
			}
			data.push(fullmaktData)
		}

		const vegadresse = (adresseData) => {
			return [
				obj('Adressekode', adresseData.adressekode),
				obj('Adressenavn', adresseData.adressenavn),
				obj('Tilleggsnavn', adresseData.tilleggsnavn),
				obj('Bruksenhetsnummer', adresseData.bruksenhetsnummer),
				obj('Husnummer', adresseData.husnummer),
				obj('Husbokstav', adresseData.husbokstav),
				obj('Postnummer', adresseData.postnummer),
				obj('Kommunenummer', adresseData.kommunenummer),
			]
		}

		const matrikkeladresse = (adresseData) => {
			return [
				obj('Gårdsnummer', adresseData.gaardsnummer),
				obj('Bruksnummer', adresseData.bruksnummer),
				obj('Bruksenhetsnummer', adresseData.bruksenhetsnummer),
				obj('Tilleggsnavn', adresseData.tilleggsnavn),
				obj('Postnummer', adresseData.postnummer),
				obj('Kommunenummer', adresseData.kommunenummer),
			]
		}

		const utenlandskAdresse = (adresseData) => {
			return [
				obj('Gatenavn og husnummer', adresseData.adressenavnNummer),
				obj('Postnummer og -navn', adresseData.postboksNummerNavn),
				obj('Postkode', adresseData.postkode),
				obj('By eller sted', adresseData.bySted),
				obj('Land', adresseData.landkode, AdresseKodeverk.StatsborgerskapLand),
				obj('Bygg-/leilighetsinfo', adresseData.bygningEtasjeLeilighet),
				obj('Region/distrikt/område', adresseData.regionDistriktOmraade),
			]
		}

		const datoer = (datoData) => {
			return [
				obj('Flyttedato', Formatters.formatDate(datoData.angittFlyttedato)),
				obj('Gyldig f.o.m.', Formatters.formatDate(datoData.gyldigFraOgMed)),
				obj('Gyldig t.o.m.', Formatters.formatDate(datoData.gyldigTilOgMed)),
			]
		}

		if (tilrettelagtKommunikasjon) {
			const tilrettelagtKommunikasjonData = {
				header: 'Tilrettelagt Kommunikasjon',
				itemRows: tilrettelagtKommunikasjon.map((item, idx) => {
					return [
						{ numberHeader: `Tolk ${idx + 1}` },
						obj('Talespråk', item.spraakForTaletolk, PersoninformasjonKodeverk.Spraak),
						obj('Tegnspråk', item.spraakForTegnspraakTolk, PersoninformasjonKodeverk.Spraak),
					]
				}),
			}
			data.push(tilrettelagtKommunikasjonData)
		}

		if (statsborgerskap) {
			const statsborgerskapData = {
				header: 'Statsborgerskap',
				itemRows: statsborgerskap.map((item, idx) => {
					return [
						{ numberHeader: `Statsborgerskap ${idx + 1}` },
						obj('Statsborgerskap', item.landkode, AdresseKodeverk.StatsborgerskapLand),
						obj('Statsborgerskap fra', Formatters.formatDate(item.gyldigFraOgMed)),
						obj('Statsborgerskap til', Formatters.formatDate(item.gyldigTilOgMed)),
						obj('Bekreftelsesdato', Formatters.formatDate(item.bekreftelsesdato)),
					]
				}),
			}
			data.push(statsborgerskapData)
		}

		if (doedsfall) {
			const doedsfallData = {
				header: 'Dødsfall',
				itemRows: doedsfall.map((item, idx) => {
					return [
						{ numberHeader: `Dødsfall ${idx + 1}` },
						obj('Dødsdato', Formatters.formatDate(item.doedsdato)),
					]
				}),
			}
			data.push(doedsfallData)
		}

		if (bostedsadresse) {
			const bostedsadresseData = {
				header: 'Bostedsadresse',
				itemRows: bostedsadresse.map((item, idx) => {
					if (item.utenlandskAdresse) {
						const adresseData = item.utenlandskAdresse
						return [
							{ numberHeader: `Utenlandsk boadresse ${idx + 1}` },
							obj('', isEmpty(adresseData) && 'Ingen verdier satt'),
							obj('Gatenavn og husnummer', adresseData.adressenavnNummer),
							obj('Postnummer og -navn', adresseData.postboksNummerNavn),
							obj('Postkode', adresseData.postkode),
							obj('By eller sted', adresseData.bySted),
							obj('Land', adresseData.landkode, AdresseKodeverk.StatsborgerskapLand),
							obj('Bygg-/leilighetsinfo', adresseData.bygningEtasjeLeilighet),
							obj('Region/distrikt/område', adresseData.regionDistriktOmraade),
						]
					}
					if (item.vegadresse) {
						const adresseData = item.vegadresse
						return [
							{ numberHeader: `Bostedsadresse ${idx + 1}: Vegadresse` },
							obj('Vegadresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...vegadresse(adresseData),
							...datoer(item),
						]
					}
					if (item.matrikkeladresse) {
						const adresseData = item.matrikkeladresse
						return [
							{ numberHeader: `Bostedsadresse ${idx + 1}: Matrikkeladresse` },
							obj('Matrikkeladresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...matrikkeladresse(adresseData),
							...datoer(item),
						]
					}
					if (item.utenlandskAdresse) {
						const adresseData = item.utenlandskAdresse
						return [
							{ numberHeader: `Bostedsadresse ${idx + 1}: Utenlandsk adresse` },
							obj('Utenlandsk adresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...utenlandskAdresse(adresseData),
							...datoer(item),
						]
					}
					if (item.ukjentBosted) {
						const adresseData = item.ukjentBosted
						return [
							{ numberHeader: `Bostedsadresse ${idx + 1}: Ukjent bosted` },
							obj('Ukjent bosted', isEmpty(adresseData) && 'Ingen verdier satt'),
							obj('Bostedskommune', adresseData.bostedskommune),
							...datoer(item),
						]
					}
					return [obj('Bostedsadresse', 'Ingen verdier satt'), ...datoer(item)]
				}),
			}
			data.push(bostedsadresseData)
		}

		if (oppholdsadresse) {
			const oppholdsadresseData = {
				header: 'Oppholdsadresse',
				itemRows: oppholdsadresse.map((item, idx) => {
					if (item.vegadresse) {
						const adresseData = item.vegadresse
						return [
							{ numberHeader: `Oppholdsadresse ${idx + 1}: Vegadresse` },
							obj('Vegadresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...vegadresse(adresseData),
							...datoer(item),
						]
					}
					if (item.matrikkeladresse) {
						const adresseData = item.matrikkeladresse
						return [
							{ numberHeader: `Oppholdsadresse ${idx + 1}: Matrikkeladresse` },
							obj('Matrikkeladresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...matrikkeladresse(adresseData),
							...datoer(item),
						]
					}
					if (item.utenlandskAdresse) {
						const adresseData = item.utenlandskAdresse
						return [
							{ numberHeader: `Oppholdsadresse ${idx + 1}: Utenlandsk adresse` },
							obj('Utenlandsk adresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...utenlandskAdresse(adresseData),
							...datoer(item),
						]
					}
					if (item.oppholdAnnetSted) {
						return [
							{ numberHeader: `Oppholdsadresse ${idx + 1}: Opphold annet sted` },
							obj(
								'Opphold annet sted',
								Formatters.showLabel('oppholdAnnetSted', item.oppholdAnnetSted)
							),
							...datoer(item),
						]
					}
					return [obj('Oppholdsadresse', 'Ingen verdier satt'), ...datoer(item)]
				}),
			}
			data.push(oppholdsadresseData)
		}

		if (kontaktadresse) {
			const kontaktadresseData = {
				header: 'Kontaktadresse',
				itemRows: kontaktadresse.map((item, idx) => {
					if (item.vegadresse) {
						const adresseData = item.vegadresse
						return [
							{ numberHeader: `Kontaktadresse ${idx + 1}: Vegadresse` },
							obj('Vegadresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...vegadresse(adresseData),
							...datoer(item),
						]
					}
					if (item.utenlandskAdresse) {
						const adresseData = item.utenlandskAdresse
						return [
							{ numberHeader: `Kontaktadresse ${idx + 1}: Utenlandsk adresse` },
							obj('Utenlandsk adresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							...utenlandskAdresse(adresseData),
							...datoer(item),
						]
					}
					if (item.postboksadresse) {
						const adresseData = item.postboksadresse
						return [
							{ numberHeader: `Kontaktadresse ${idx + 1}: Postboksadresse` },
							obj('Postboksadresse', isEmpty(adresseData) && 'Ingen verdier satt'),
							obj('Postbokseier', adresseData.postbokseier),
							obj('Postboks', adresseData.postboks),
							obj('Postnummer', adresseData.postnummer),
							...datoer(item),
						]
					}
					return [obj('Kontaktadresse', 'Ingen verdier satt'), ...datoer(item)]
				}),
			}
			data.push(kontaktadresseData)
		}

		if (adressebeskyttelse) {
			const adressebeskyttelseData = {
				header: 'Adressebeskyttelse',
				itemRows: adressebeskyttelse.map((item, idx) => {
					return [
						{ numberHeader: `Adressebeskyttelse ${idx + 1}` },
						obj('Gradering', Formatters.showLabel('gradering', item.gradering)),
					]
				}),
			}
			data.push(adressebeskyttelseData)
		}

		if (sikkerhetstiltak) {
			const sikkerhetstiltakData = {
				header: 'Sikkerhetstiltak',
				itemRows: sikkerhetstiltak.map((item, idx) => {
					return [
						{ numberHeader: `Sikkerhetstiltak ${idx + 1}` },
						obj('Type sikkerhetstiltak', item.tiltakstype),
						obj('Beskrivelse', item.beskrivelse),
						obj('Kontaktperson', item.kontaktperson.personident),
						obj('Navkontor kode', item.kontaktperson.enhet),
						obj('Gyldig fra og med', Formatters.formatDate(item.gyldigFraOgMed)),
						obj('Gyldig til og med', Formatters.formatDate(item.gyldigTilOgMed)),
					]
				}),
			}
			data.push(sikkerhetstiltakData)
		}

		if (sivilstand) {
			const sivilstandData = {
				header: 'Sivilstand (partner)',
				itemRows: sivilstand.map((item, idx) => {
					return [
						{ numberHeader: `Sivilstand ${idx + 1}` },
						obj('Type sivilstand', Formatters.showLabel('sivilstandType', item.type)),
						obj('Gyldig fra og med', Formatters.formatDate(item.sivilstandsdato)),
						obj('Bekreftelsesdato', Formatters.formatDate(item.bekreftelsesdato)),
						obj('Bor ikke sammen', Formatters.oversettBoolean(item.borIkkeSammen)),
						obj('Person relatert til', item.relatertVedSivilstand),
						...personRelatertTil(item, 'nyRelatertPerson'),
					]
				}),
			}
			data.push(sivilstandData)
		}

		const deltBosted = (personData, path) => {
			if (!personData || !_get(personData, path)) return [expandable(null, false, null)]
			const deltBostedData = _get(personData, path)

			const fellesVerdier = [
				obj(
					'Adressetype',
					Formatters.showLabel('adressetypeDeltBosted', deltBostedData.adressetype)
				),
				obj('Startdato for kontrakt', Formatters.formatDate(deltBostedData.startdatoForKontrakt)),
				obj('Sluttdato for kontrakt', Formatters.formatDate(deltBostedData.sluttdatoForKontrakt)),
			]

			if (deltBostedData.vegadresse !== undefined && deltBostedData.vegadresse) {
				return [
					expandable('DELT BOSTED', !isEmpty(deltBostedData), [
						...fellesVerdier,
						obj(
							'Vegadresse',
							deltBostedData.adressetype === 'VEGADRESSE' &&
								isEmpty(deltBostedData.vegadresse) &&
								'Ingen verdier satt'
						),
						...vegadresse(deltBostedData.vegadresse),
					]),
				]
			} else if (deltBostedData.matrikkeladresse !== undefined && deltBostedData.matrikkeladresse) {
				return [
					expandable('DELT BOSTED', !isEmpty(deltBostedData), [
						...fellesVerdier,
						obj(
							'Matrikkeladresse',
							deltBostedData.adressetype === 'MATRIKKELADRESSE' &&
								isEmpty(deltBostedData.matrikkeladresse) &&
								'Ingen verdier satt'
						),
						...matrikkeladresse(deltBostedData.matrikkeladresse),
					]),
				]
			} else if (deltBostedData.ukjentBosted !== undefined && deltBostedData.ukjentBosted) {
				return [
					expandable('DELT BOSTED', !isEmpty(deltBostedData), [
						...fellesVerdier,
						obj('Bostedskommune', deltBostedData.ukjentBosted.bostedskommune),
					]),
				]
			} else {
				return [expandable('DELT BOSTED', !isEmpty(deltBostedData), [...fellesVerdier])]
			}
		}

		if (forelderBarnRelasjon) {
			const foreldreBarnData = {
				header: 'Barn/foreldre',
				itemRows: forelderBarnRelasjon.map((item, idx) => {
					return [
						{ numberHeader: `Relasjon ${idx + 1}` },
						obj('Relasjon', Formatters.showLabel('pdlRelasjonTyper', item.relatertPersonsRolle)),
						obj(
							'Rolle for barn',
							item.relatertPersonsRolle === 'BARN' &&
								Formatters.showLabel('pdlRelasjonTyper', item.minRolleForPerson)
						),
						obj('Bor ikke sammen', Formatters.oversettBoolean(item.borIkkeSammen)),
						obj('Partner ikke forelder', Formatters.oversettBoolean(item.partnerErIkkeForelder)),
						obj('Person relatert til', item.relatertPerson),
						...deltBosted(item, 'deltBosted'),
						...personRelatertTil(item, 'nyRelatertPerson'),
					]
				}),
			}
			data.push(foreldreBarnData)
		}

		if (foreldreansvar) {
			const foreldreansvarData = {
				header: 'Foreldreansvar',
				itemRows: foreldreansvar.map((item, idx) => {
					return [
						{ numberHeader: `Foreldreansvar ${idx + 1}` },
						obj('Hvem har ansvaret', Formatters.showLabel('foreldreansvar', item.ansvar)),
						obj('Gyldig fra og med', Formatters.formatDate(item.gyldigFraOgMed)),
						obj('Gyldig til og med', Formatters.formatDate(item.gyldigTilOgMed)),
						obj(
							'Type ansvarlig',
							(item.ansvarlig && 'Eksisterende person') ||
								(item.nyAnsvarlig && 'Ny person') ||
								(item.ansvarligUtenIdentifikator && 'Person uten identifikator')
						),
						obj('Ansvarlig', Formatters.showLabel('foreldreansvar', item.ansvarlig)),
						obj('Identtype', item.nyAnsvarlig?.identtype),
						obj('Kjønn', item.nyAnsvarlig?.kjoenn),
						obj('Født etter', Formatters.formatDate(item.nyAnsvarlig?.foedtEtter)),
						obj('Født før', Formatters.formatDate(item.nyAnsvarlig?.foedtFoer)),
						obj('Alder', item.nyAnsvarlig?.alder),
						obj(
							'Statsborgerskap',
							item.nyAnsvarlig?.statsborgerskapLandkode,
							AdresseKodeverk.StatsborgerskapLand
						),
						obj('Gradering', Formatters.showLabel('gradering', item.nyAnsvarlig?.gradering)),
						obj('Syntetisk', item.nyAnsvarlig?.syntetisk && 'JA'),
						obj('Har mellomnavn', item.nyAnsvarlig?.nyttNavn?.hasMellomnavn && 'JA'),
						obj('Kjønn', item.ansvarligUtenIdentifikator?.kjoenn),
						obj(
							'Fødselsdato',
							Formatters.formatDate(item.ansvarligUtenIdentifikator?.foedselsdato)
						),
						obj(
							'Statsborgerskap',
							item.ansvarligUtenIdentifikator?.statsborgerskap,
							AdresseKodeverk.StatsborgerskapLand
						),
						obj('Fornavn', item.ansvarligUtenIdentifikator?.navn?.fornavn),
						obj('Mellomnavn', item.ansvarligUtenIdentifikator?.navn?.mellomnavn),
						obj('Etternavn', item.ansvarligUtenIdentifikator?.navn?.etternavn),
					]
				}),
			}
			data.push(foreldreansvarData)
		}

		if (doedfoedtBarn) {
			const doedfoedtBarnData = {
				header: 'Dødfødt barn',
				itemRows: [],
			}

			doedfoedtBarn.forEach((item, i) => {
				doedfoedtBarnData.itemRows.push([
					{
						label: '',
						value: `#${i + 1}`,
						width: 'x-small',
					},
					obj('Dødsdato', Formatters.formatDate(item.dato)),
				])
			})

			data.push(doedfoedtBarnData)
		}

		const sjekkRettIdent = (item) => {
			if (_has(item, 'rettIdentitetErUkjent')) {
				return 'Ukjent'
			} else if (_has(item, 'rettIdentitetVedIdentifikasjonsnummer')) {
				return 'Ved identifikasjonsnummer'
			}
			return _has(item, 'rettIdentitetVedOpplysninger') ? 'Ved personopplysninger' : 'Ingen'
		}

		if (falskIdentitet) {
			const falskIdentitetData = {
				header: 'Falsk identitet',
				itemRows: falskIdentitet.map((item, idx) => {
					return [
						{ numberHeader: `Falsk identitet ${idx + 1}` },
						obj('Opplysninger om rett ident', sjekkRettIdent(item)),
						obj('Identifikasjonsnummer', item.rettIdentitetVedIdentifikasjonsnummer),
						obj('Fornavn', item.rettIdentitetVedOpplysninger?.personnavn?.fornavn),
						obj('Mellomnavn', item.rettIdentitetVedOpplysninger?.personnavn?.mellomnavn),
						obj('Etternavn', item.rettIdentitetVedOpplysninger?.personnavn?.etternavn),
						obj(
							'Fødselsdato',
							Formatters.formatDate(item.rettIdentitetVedOpplysninger?.foedselsdato)
						),
						obj('Kjønn', item.rettIdentitetVedOpplysninger?.kjoenn),
						obj('Statsborgerskap', item.rettIdentitetVedOpplysninger?.statsborgerskap?.join(', ')),
					]
				}),
			}
			data.push(falskIdentitetData)
		}

		if (utenlandskIdentifikasjonsnummer) {
			const utenlandskIdentData = {
				header: 'Utenlandsk identifikasjonsnummer',
				itemRows: utenlandskIdentifikasjonsnummer.map((item, idx) => {
					return [
						{
							numberHeader: `Utenlandsk ID ${idx + 1}`,
						},
						obj('Utenlandsk ID', item.identifikasjonsnummer),
						obj('Utenlandsk ID opphørt', Formatters.oversettBoolean(item.opphoert)),
						obj('Utstederland', item.utstederland, AdresseKodeverk.Utstederland),
					]
				}),
			}
			data.push(utenlandskIdentData)
		}

		if (nyident) {
			const nyidentData = {
				header: 'Ny identitet',
				itemRows: nyident.map((item, idx) => {
					return [
						{
							numberHeader: `Ny identitet ${idx + 1}`,
						},
						obj('Eksisterende ident', item.eksisterendeIdent),
						obj('Identtype', item.identtype),
						obj('Kjønn', item.kjoenn),
						obj('Født etter', Formatters.formatDate(item.foedtEtter)),
						obj('Født før', Formatters.formatDate(item.foedtFoer)),
						obj('Alder', item.alder),
						obj('Er syntetisk', item.syntetisk && 'JA'),
						obj('Har mellomnavn', item.nyttNavn?.hasMellomnavn && 'JA'),
					]
				}),
			}
			data.push(nyidentData)
		}

		if (kontaktinformasjonForDoedsbo) {
			const doedsboData = {
				header: 'Kontaktinformasjon for dødsbo',
				itemRows: kontaktinformasjonForDoedsbo.map((item, idx) => {
					const {
						skifteform,
						attestutstedelsesdato,
						kontaktType,
						advokatSomKontakt,
						organisasjonSomKontakt,
						personSomKontakt,
						adresse,
					} = item

					const getKontakttype = () => {
						if (advokatSomKontakt) {
							return 'Advokat'
						} else if (personSomKontakt) {
							return 'Person'
						} else if (organisasjonSomKontakt) {
							return 'Organisasjon'
						} else return null
					}

					return [
						{ numberHeader: `Kontaktinformasjon for dødsbo ${idx + 1}` },
						obj('Skifteform', skifteform),
						obj('Utstedelsesdato skifteattest', Formatters.formatDate(attestutstedelsesdato)),
						obj(
							'Kontakttype',
							kontaktType ? Formatters.showLabel('kontaktType', kontaktType) : getKontakttype()
						),

						obj(
							'Organisasjonsnummer',
							advokatSomKontakt?.organisasjonsnummer || organisasjonSomKontakt?.organisasjonsnummer
						),
						obj(
							'Organisasjonsnavn',
							advokatSomKontakt?.organisasjonsnavn || organisasjonSomKontakt?.organisasjonsnavn
						),
						obj('Identifikasjonsnummer', personSomKontakt?.identifikasjonsnummer),
						obj('Fødselsdato', Formatters.formatDate(personSomKontakt?.foedselsdato)),

						obj(
							'Kontaktperson fornavn',
							advokatSomKontakt?.kontaktperson?.fornavn ||
								organisasjonSomKontakt?.kontaktperson?.fornavn ||
								personSomKontakt?.navn?.fornavn
						),
						obj(
							'Kontaktperson mellomnavn',
							advokatSomKontakt?.kontaktperson?.mellomnavn ||
								organisasjonSomKontakt?.kontaktperson?.mellomnavn ||
								personSomKontakt?.navn?.mellomnavn
						),
						obj(
							'Kontaktperson etternavn',
							advokatSomKontakt?.kontaktperson?.etternavn ||
								organisasjonSomKontakt?.kontaktperson?.etternavn ||
								personSomKontakt?.navn?.etternavn
						),
						obj('Land', adresse?.landkode, AdresseKodeverk.PostadresseLand),
						obj('Adresselinje 1', adresse?.adresselinje1),
						obj('Adresselinje 2', adresse?.adresselinje2),
						obj(
							'Postnummer og -sted',
							(adresse?.postnummer || adresse?.poststedsnavn) &&
								`${adresse?.postnummer} ${adresse?.poststedsnavn}`
						),
						...personRelatertTil(item, 'personSomKontakt.nyKontaktperson'),
					]
				}),
			}
			data.push(doedsboData)
		}
	}

	const tpsMessaging = _get(bestillingData, 'tpsMessaging')

	if (
		tpsMessaging?.spraakKode ||
		tpsMessaging?.egenAnsattDatoFom ||
		tpsMessaging?.egenAnsattDatoTom ||
		tpsMessaging?.norskBankkonto ||
		tpsMessaging?.utenlandskBankkonto
	) {
		const tpsMessagingData = {
			header: 'Personinformasjon',
			items: [
				obj('Språk', tpsMessaging.spraakKode, PersoninformasjonKodeverk.Spraak),
				obj('Skjerming fra', Formatters.formatDate(tpsMessaging.egenAnsattDatoFom)),
				obj('Skjerming til', Formatters.formatDate(tpsMessaging.egenAnsattDatoTom)),
			],
		}
		data.push(tpsMessagingData)

		if (tpsMessaging.norskBankkonto) {
			const norskBankkontoData = {
				header: 'Norsk bankkonto',
				items: [obj('Kontonummer', tpsMessaging.norskBankkonto.kontonummer)],
			}
			data.push(norskBankkontoData)
		}

		if (tpsMessaging.utenlandskBankkonto) {
			const utenlandskBankkontoData = {
				header: 'Utenlandsk bankkonto',
				items: [
					obj('Kontonummer', tpsMessaging.utenlandskBankkonto.kontonummer),
					obj('Swift kode', tpsMessaging.utenlandskBankkonto.swift),
					obj('Land', tpsMessaging.utenlandskBankkonto.landkode),
					obj('Banknavn', tpsMessaging.utenlandskBankkonto.banknavn),
					obj('IBAN', tpsMessaging.utenlandskBankkonto.iban),
					obj('Valuta', tpsMessaging.utenlandskBankkonto.valuta),
					obj('Adresselinje 1', tpsMessaging.utenlandskBankkonto.bankAdresse1),
					obj('Adresselinje 2', tpsMessaging.utenlandskBankkonto.bankAdresse2),
					obj('Adresselinje 3', tpsMessaging.utenlandskBankkonto.bankAdresse3),
				],
			}
			data.push(utenlandskBankkontoData)
		}
	}

	const aaregKriterier = bestillingData.aareg
	if (aaregKriterier) {
		const aareg = {
			header: 'Arbeidsforhold (Aareg)',
			items: [],
			itemRows: [],
			paginering: [],
			pagineringPages: [],
		}

		const harAmelding = _has(aaregKriterier[0], 'amelding')
		const arbeidsforholdVisning = (arbeidsforhold, i) => [
			{
				numberHeader: `Arbeidsforhold ${i + 1}`,
			},
			{
				label: 'Type arbeidsforhold',
				value:
					arbeidsforhold.arbeidsforholdstype ||
					(!harAmelding && aaregKriterier.arbeidsforholdstype),
				apiKodeverkId: ArbeidKodeverk.Arbeidsforholdstyper,
			},
			obj('Orgnummer', arbeidsforhold.arbeidsgiver?.orgnummer),
			obj('Arbeidsgiver ident', arbeidsforhold.arbeidsgiver?.ident),
			obj('Arbeidsforhold-ID', arbeidsforhold.arbeidsforholdID),
			obj('Ansatt fra', Formatters.formatDate(arbeidsforhold.ansettelsesPeriode?.fom)),
			obj('Ansatt til', Formatters.formatDate(arbeidsforhold.ansettelsesPeriode?.tom)),
			{
				label: 'Sluttårsak',
				value: arbeidsforhold.ansettelsesPeriode?.sluttaarsak,
				apiKodeverkId: ArbeidKodeverk.SluttaarsakAareg,
			},
			{
				label: 'Yrke',
				value: arbeidsforhold.arbeidsavtale?.yrke,
				apiKodeverkId: ArbeidKodeverk.Yrker,
			},
			{
				label: 'Ansettelsesform',
				value: arbeidsforhold.arbeidsavtale?.ansettelsesform,
				apiKodeverkId: ArbeidKodeverk.AnsettelsesformAareg,
			},
			obj(
				'Stillingprosent',
				arbeidsforhold.arbeidsavtale?.stillingsprosent === 0
					? '0'
					: arbeidsforhold.arbeidsavtale?.stillingsprosent
			),
			obj(
				'Endringsdato stillingprosent',
				Formatters.formatDate(arbeidsforhold.arbeidsavtale?.endringsdatoStillingsprosent)
			),
			obj(
				'Endringsdato lønn',
				Formatters.formatDate(arbeidsforhold.arbeidsavtale?.sisteLoennsendringsdato)
			),
			{
				label: 'Arbeidstidsordning',
				value: arbeidsforhold.arbeidsavtale?.arbeidstidsordning,
				apiKodeverkId: ArbeidKodeverk.Arbeidstidsordninger,
			},
			obj('Avtalte arbeidstimer per uke', arbeidsforhold.arbeidsavtale?.avtaltArbeidstimerPerUke),
			{
				label: 'Skipsregister',
				value: arbeidsforhold.fartoy?.[0].skipsregister,
				apiKodeverkId: ArbeidKodeverk.Skipsregistre,
			},
			{
				label: 'Fartøystype',
				value: arbeidsforhold.fartoy?.[0].skipstype,
				apiKodeverkId: ArbeidKodeverk.Skipstyper,
			},
			{
				label: 'Fartsområde',
				value: arbeidsforhold.fartoy?.[0].fartsomraade,
				apiKodeverkId: ArbeidKodeverk.Fartsområder,
			},
			obj(
				'Perioder med antall timer for timelønnet',
				arbeidsforhold.antallTimerForTimeloennet?.length
			),
			obj('Perioder med utenlandsopphold', arbeidsforhold.utenlandsopphold?.length),
			obj('Perioder med permisjon', arbeidsforhold.permisjon?.length),
			obj('Perioder med permittering', arbeidsforhold.permittering?.length),
		]

		if (harAmelding) {
			aareg.items.push(
				{
					label: 'Type arbeidsforhold',
					value: aaregKriterier[0]?.arbeidsforholdstype,
					apiKodeverkId: ArbeidKodeverk.Arbeidsforholdstyper,
				},
				obj('F.o.m. kalendermåned', Formatters.formatDate(aaregKriterier[0]?.genererPeriode?.fom)),
				obj('T.o.m. kalendermåned', Formatters.formatDate(aaregKriterier[0]?.genererPeriode?.tom))
			)
			aaregKriterier[0]?.amelding.forEach((maaned) => {
				const data = {
					itemRows: [],
				}
				maaned.arbeidsforhold.forEach((arbeidsforhold, i) => {
					data.itemRows.push(arbeidsforholdVisning(arbeidsforhold, i))
				})
				aareg.pagineringPages.push(maaned.maaned)
				aareg.paginering.push(data)
			})
		} else if (aaregKriterier[0]?.arbeidsgiver) {
			aaregKriterier.forEach((arbeidsforhold, i) => {
				aareg.itemRows.push(arbeidsforholdVisning(arbeidsforhold, i))
			})
		}

		data.push(aareg)
	}

	const sigrunStubKriterier = bestillingData.sigrunstub

	if (sigrunStubKriterier) {
		// Flatter ut sigrunKriterier for å gjøre det lettere å mappe
		const flatSigrunStubKriterier = []
		sigrunStubKriterier.forEach((inntekt) => {
			const inntektObj = { inntektsaar: inntekt.inntektsaar, tjeneste: inntekt.tjeneste }
			inntekt.grunnlag &&
				inntekt.grunnlag.forEach((gr) => {
					flatSigrunStubKriterier.push({
						...inntektObj,
						grunnlag: gr.tekniskNavn,
						verdi: gr.verdi,
						inntektssted: 'Fastlands-Norge',
					})
				})
			inntekt.svalbardGrunnlag &&
				inntekt.svalbardGrunnlag.forEach((gr) => {
					flatSigrunStubKriterier.push({
						...inntektObj,
						svalbardGrunnlag: gr.tekniskNavn,
						verdi: gr.verdi,
						inntektssted: 'Svalbard',
					})
				})
		})

		const sigrunStub = {
			header: 'Skatteoppgjør (Sigrun)',
			itemRows: [],
		}

		flatSigrunStubKriterier.forEach((inntekt, i) => {
			sigrunStub.itemRows.push([
				{
					numberHeader: `Inntekt ${i + 1}`,
				},
				obj('År', inntekt.inntektsaar),
				obj('Beløp', inntekt.verdi),
				obj('Tjeneste', Formatters.uppercaseAndUnderscoreToCapitalized(inntekt.tjeneste)),
				{
					label: 'Grunnlag (Fastlands-Norge)',
					value: inntekt.grunnlag,
					width: 'xlarge',
					apiKodeverkId: SigrunKodeverk[inntekt.tjeneste],
				},
				{
					label: 'Grunnlag (Svalbard)',
					value: inntekt.svalbardGrunnlag,
					width: 'xlarge',
					apiKodeverkId: SigrunKodeverk[inntekt.tjeneste],
				},
			])
		})

		data.push(sigrunStub)
	}

	const inntektStubKriterier = bestillingData.inntektstub

	if (inntektStubKriterier) {
		const inntektStub = {
			header: 'A-ordningen (Inntektstub)',
			// items: [
			// 	obj('Prosentøkning per år', inntektStubKriterier.prosentOekningPerAaar)
			// ],
			itemRows: [],
		}

		inntektStubKriterier.inntektsinformasjon &&
			inntektStubKriterier.inntektsinformasjon.forEach((inntektsinfo, i) => {
				inntektStub.itemRows.push([
					{ numberHeader: `Inntektsinformasjon ${i + 1}` },
					obj('År/måned', inntektsinfo.sisteAarMaaned),
					obj('Rapporteringstidspunkt', inntektsinfo.rapporteringsdato),
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
					),
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
						obj('Arbeidsforhold-ID', sykemeldingKriterier.syntSykemelding.arbeidsforholdId),
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
						),
				  ]
				: null,
		}
		data.push(sykemelding)
	}

	const brregstubKriterier = bestillingData.brregstub

	if (brregstubKriterier) {
		const brregstub = {
			header: 'Brønnøysundregistrene',
			items: [obj('Understatuser', Formatters.arrayToString(brregstubKriterier.understatuser))],
			itemRows: [],
		}
		brregstubKriterier.enheter.forEach((enhet, i) => {
			brregstub.itemRows.push([
				{ numberHeader: `Enhet ${i + 1}` },
				obj('Rolle', enhet.rolle),
				obj('Registreringsdato', Formatters.formatDate(enhet.registreringsdato)),
				obj('Organisasjonsnummer', enhet.orgNr),
				obj('Foretaksnavn', enhet.foretaksNavn.navn1),
				obj('Antall registrerte personroller', enhet.personroller && enhet.personroller.length),
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
					width: 'medium',
				},
				obj('Epost', krrKriterier.epost),
				obj('Mobilnummer', krrKriterier.mobil),
				obj('Språk', Formatters.showLabel('spraaktype', krrKriterier.spraak)),
				obj('Gyldig fra', Formatters.formatDate(krrKriterier.gyldigFra)),
				obj('Adresse', krrKriterier.sdpAdresse),
				obj('Leverandør', krrKriterier.sdpLeverandoer),
			],
		}

		data.push(krrStub)
	}

	const arenaKriterier = bestillingData.arenaforvalter

	if (arenaKriterier) {
		const arenaforvalter = {
			header: 'Arbeidsytelser',
			items: [
				obj(
					'Brukertype',
					Formatters.uppercaseAndUnderscoreToCapitalized(arenaKriterier.arenaBrukertype)
				),
				obj('Servicebehov', arenaKriterier.kvalifiseringsgruppe),
				obj(
					'Automatisk innsending av meldekort',
					Formatters.oversettBoolean(arenaKriterier.automatiskInnsendingAvMeldekort)
				),
				obj('Inaktiv fra dato', Formatters.formatDate(arenaKriterier.inaktiveringDato)),
				obj('Har 11-5 vedtak', Formatters.oversettBoolean(arenaKriterier.aap115?.[0] && true)),
				obj(
					'AAP 11-5 fra dato',
					arenaKriterier.aap115?.[0] && Formatters.formatDate(arenaKriterier.aap115[0].fraDato)
				),
				obj(
					'Har AAP vedtak UA - positivt utfall',
					Formatters.oversettBoolean(arenaKriterier.aap?.[0] && true)
				),
				obj(
					'AAP fra dato',
					arenaKriterier.aap?.[0] && Formatters.formatDate(arenaKriterier.aap[0].fraDato)
				),
				obj(
					'AAP til dato',
					arenaKriterier.aap?.[0] && Formatters.formatDate(arenaKriterier.aap[0].tilDato)
				),
				obj(
					'Har dagpengevedtak',
					Formatters.oversettBoolean(arenaKriterier.dagpenger?.[0] && true)
				),
				obj(
					'RettighetKode',
					arenaKriterier.dagpenger?.[0] &&
						Formatters.showLabel('rettighetKode', arenaKriterier.dagpenger[0].rettighetKode)
				),
				obj(
					'Dagpenger fra dato',
					arenaKriterier.dagpenger?.[0] &&
						Formatters.formatDate(arenaKriterier.dagpenger[0].fraDato)
				),
				obj(
					'Dagpenger til dato',
					arenaKriterier.dagpenger?.[0] &&
						Formatters.formatDate(arenaKriterier.dagpenger[0].tilDato)
				),
			],
		}
		data.push(arenaforvalter)
	}

	const instKriterier = bestillingData.instdata

	if (instKriterier) {
		// Flater ut instKriterier for å gjøre det lettere å mappe

		const flatInstKriterier = []
		instKriterier.forEach((i) => {
			flatInstKriterier.push({
				institusjonstype: i.institusjonstype,
				varighet: i.varighet,
				startdato: i.startdato,
				sluttdato: i.sluttdato,
			})
		})

		const instObj = {
			header: 'Institusjonsopphold',
			itemRows: [],
		}

		flatInstKriterier.forEach((inst, i) => {
			instObj.itemRows.push([
				{
					numberHeader: `Institusjonsopphold ${i + 1}`,
				},
				obj('Institusjonstype', Formatters.showLabel('institusjonstype', inst.institusjonstype)),
				obj('Varighet', inst.varighet && Formatters.showLabel('varighet', inst.varighet)),
				obj('Startdato', Formatters.formatDate(inst.startdato)),
				obj('Sluttdato', Formatters.formatDate(inst.sluttdato)),
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
			'eosEllerEFTAOppholdstillatelse',
		]
		const currentOppholdsrettType =
			oppholdKriterier && oppholdsrettTyper.find((type) => oppholdKriterier[type])

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
				),
			],
		}
		data.push(udistub)
	}

	const pensjonKriterier = bestillingData.pensjonforvalter

	if (pensjonKriterier) {
		if (pensjonKriterier.inntekt) {
			const pensjonforvalterPopp = {
				header: 'Pensjonsgivende inntekt (POPP)',
				items: [
					obj('Fra og med år', pensjonKriterier.inntekt.fomAar),
					obj('Til og med år', pensjonKriterier.inntekt.tomAar),
					obj('Beløp', pensjonKriterier.inntekt.belop),
					obj(
						'Nedjuster med grunnbeløp',
						Formatters.oversettBoolean(pensjonKriterier.inntekt.redusertMedGrunnbelop)
					),
				],
			}

			data.push(pensjonforvalterPopp)
		}

		if (pensjonKriterier.tp) {
			const pensjonforvalterTp = {
				header: 'Tjenestepensjon (TP)',
				items: [obj('Ordning', pensjonKriterier.tp.ordning)],
			}

			data.push(pensjonforvalterTp)
		}
	}

	const inntektsmeldingKriterier = bestillingData.inntektsmelding

	const mapInntektsmeldingKriterier = (meldinger) => ({
		header: 'Inntektsmelding (fra Altinn)',
		itemRows: meldinger.map((inntekt, i) => [
			{
				numberHeader: `Inntekt ${i + 1}`,
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
				'Arbeidsgiver (fnr/dnr/npid)',
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
				'Endringer i refusjon',
				_has(inntekt, 'refusjon.endringIRefusjonListe') &&
					inntekt.refusjon.endringIRefusjonListe.length
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
			),
		]),
	})

	const tomInntektsmelding = {
		header: 'Inntektsmelding (fra Altinn)',
		items: [obj('Inntektsmelding', 'Tom bestilling')],
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
				obj('Avsender type', dokarkivKriterier.avsenderMottaker?.idType),
				obj('Avsender ID', dokarkivKriterier.avsenderMottaker?.id),
				obj('Avsender navn', dokarkivKriterier.avsenderMottaker?.navn),
				obj('Tema', dokarkivKriterier.tema),
				obj('Journalførende enhet', dokarkivKriterier.journalfoerendeEnhet),
				obj('Antall vedlegg', dokarkivKriterier.dokumenter.length),
			],
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
				obj('Internettadresse', organisasjonKriterier[0].nettside),
			],
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
				obj('Adresselinje 3', forretningsadresse && forretningsadresse.adresselinjer[2]),
			],
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
				obj('Adresselinje 3', postadresse && postadresse.adresselinjer[2]),
			],
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
				obj('Importert fra', bestillingData.kildeMiljoe.toUpperCase()),
			],
		}

		data.push(importData)
	}

	return data
}
