import * as _ from 'lodash-es'
import {
	allCapsToCapitalized,
	arrayToString,
	codeToNorskLabel,
	formatDate,
	formatDateTime,
	formatDateTimeWithSeconds,
	omraaderArrayToString,
	oversettBoolean,
	showLabel,
	toTitleCase,
	uppercaseAndUnderscoreToCapitalized,
} from '@/utils/DataFormatter'
import {
	AdresseKodeverk,
	ArbeidKodeverk,
	PersoninformasjonKodeverk,
	VergemaalKodeverk,
} from '@/config/kodeverk'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { MedlKodeverk } from '@/components/fagsystem/medl/MedlConstants'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { showKodeverkLabel } from '@/components/fagsystem/skattekort/visning/Visning'
import { showTpNavn } from '@/components/fagsystem/afpOffentlig/visning/AfpOffentligVisning'
import { showTyperLabel } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretVisning'
import {
	kategoriKodeverk,
	tekniskNavnKodeverk,
} from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/GrunnlagArrayForm'
import { useTpOrdningKodeverk } from '@/utils/hooks/usePensjon' // TODO: Flytte til selector?

// TODO: Flytte til selector?
// - Denne kan forminskes ved bruk av hjelpefunksjoner
// - Når vi får på plass en bedre struktur for bestillingsprosessen, kan
//   mest sannsynlig visse props fjernes herfra (width?)

const obj = (label: string, value: any, apiKodeverkId?: any) => ({
	label,
	value,
	apiKodeverkId,
})

const expandable = (
	expandableHeader: null | string,
	vis: boolean,
	objects:
		| null
		| {
				apiKodeverkId: any
				label: string
				value: any
		  }[],
) => ({
	expandableHeader,
	vis,
	objects,
})

export const getTypePerson = (identNr: string) => {
	if (parseInt(identNr?.charAt(2)) < 4) {
		return 'Standard'
	}
	return parseInt(identNr?.charAt(2)) > 7 ? 'Test-Norge' : 'NAV-syntetisk'
}

const mapBestillingsinformasjon = (
	bestillingsinformasjon: any,
	data: any[],
	identtype: any,
	firstIdent: string,
) => {
	if (bestillingsinformasjon) {
		const bestillingsInfo = {
			header: 'Bestillingsinformasjon',
			items: [
				obj(
					'Antall bestilt',
					bestillingsinformasjon.antallIdenter && bestillingsinformasjon.antallIdenter.toString(),
				),
				obj(
					'Antall levert',
					bestillingsinformasjon.antallLevert && bestillingsinformasjon.antallLevert.toString(),
				),
				obj('Type person', getTypePerson(firstIdent)),
				obj('Identtype', identtype),
				obj('Sist oppdatert', formatDateTimeWithSeconds(bestillingsinformasjon.sistOppdatert)),
				obj(
					'Gjenopprettet fra',
					bestillingsinformasjon.opprettetFraId
						? `Bestilling # ${bestillingsinformasjon.opprettetFraId}`
						: bestillingsinformasjon.opprettetFraGruppeId &&
								`Gruppe # ${bestillingsinformasjon.opprettetFraGruppeId}`,
				),
			],
		}
		data.push(bestillingsInfo)
	}
}

const mapPdlNyPerson = (bestillingData, data, bestilling) => {
	const pdlNyPersonKriterier = bestillingData.pdldata?.opprettNyPerson
	if (
		pdlNyPersonKriterier &&
		(bestilling
			? _.has(pdlNyPersonKriterier, 'alder')
			: !isEmpty(pdlNyPersonKriterier, ['identtype', 'syntetisk']))
	) {
		const { alder, foedtEtter, foedtFoer } = pdlNyPersonKriterier
		const nyPersonData = {
			header: 'Persondetaljer',
			items: [
				obj('Alder', alder),
				obj('Født etter', formatDate(foedtEtter)),
				obj('Født før', formatDate(foedtFoer)),
				obj(
					'Persondetaljer',
					!alder && !foedtEtter && !foedtFoer && bestilling ? ingenVerdierSatt : null,
				),
			],
		}
		data.push(nyPersonData)
	}
}

const personRelatertTil = (personData, path) => {
	if (!personData || !_.get(personData, path)) return [expandable(null, false, null)]
	const {
		identtype,
		kjoenn,
		foedtEtter,
		foedtFoer,
		foedselsdato,
		alder,
		statsborgerskapLandkode,
		statsborgerskap,
		gradering,
		nyttNavn,
		navn,
	} = _.get(personData, path)

	return [
		expandable('PERSON RELATERT TIL', !isEmpty(_.get(personData, path), ['syntetisk']), [
			obj('Identtype', identtype),
			obj('Kjønn', kjoenn),
			obj('Født etter', formatDate(foedtEtter)),
			obj('Født før', formatDate(foedtFoer)),
			obj('Fødselsdato', formatDate(foedselsdato)),
			obj('Alder', alder),
			obj(
				'Statsborgerskap',
				statsborgerskapLandkode || statsborgerskap,
				AdresseKodeverk.StatsborgerskapLand,
			),
			obj('Gradering', showLabel('gradering', gradering)),
			obj('Har mellomnavn', nyttNavn?.hasMellomnavn && 'JA'),
			obj('Fornavn', navn?.fornavn),
			obj('Mellomnavn', navn?.mellomnavn),
			obj('Etternavn', navn?.etternavn),
		]),
	]
}

const mapFoedsel = (foedsel, data) => {
	if (foedsel) {
		const foedselData = {
			header: 'Fødsel',
			itemRows: foedsel.map((item, idx) => {
				return isEmpty(item, ['kilde', 'master'])
					? [obj('Fødsel', ingenVerdierSatt)]
					: [
							{ numberHeader: `Fødsel ${idx + 1}` },
							obj('Fødselsdato', formatDate(item.foedselsdato)),
							obj('Fødselsår', item.foedselsaar),
							obj('Fødested', item.foedested),
							obj('Fødekommune', item.foedekommune, AdresseKodeverk.Kommunenummer),
							obj('Fødeland', item.foedeland, AdresseKodeverk.InnvandretUtvandretLand),
						]
			}),
		}
		data.push(foedselData)
	}
}

const mapFoedested = (foedested, data) => {
	if (foedested) {
		const foedestedData = {
			header: 'Fødested',
			itemRows: foedested.map((item, idx) => {
				return isEmpty(item, ['kilde', 'master'])
					? [obj('Fødested', ingenVerdierSatt)]
					: [
							{ numberHeader: `Fødested ${idx + 1}` },
							obj('Fødested', item.foedested),
							obj('Fødekommune', item.foedekommune, AdresseKodeverk.Kommunenummer),
							obj('Fødeland', item.foedeland, AdresseKodeverk.InnvandretUtvandretLand),
						]
			}),
		}
		data.push(foedestedData)
	}
}

const mapFoedselsdato = (foedselsdato, data) => {
	if (foedselsdato) {
		const foedselsdatoData = {
			header: 'Fødselsdato',
			itemRows: foedselsdato.map((item, idx) => {
				return isEmpty(item, ['kilde', 'master'])
					? [obj('Fødselsdato', ingenVerdierSatt)]
					: [
							{ numberHeader: `Fødselsdato ${idx + 1}` },
							obj('Fødselsdato', formatDate(item.foedselsdato)),
							obj('Fødselsår', item.foedselsaar),
						]
			}),
		}
		data.push(foedselsdatoData)
	}
}

const mapInnflytting = (innflytting, data) => {
	if (innflytting) {
		const innflyttingData = {
			header: 'Innvandring',
			itemRows: innflytting.map((item, idx) => {
				return [
					{ numberHeader: `Innvandring ${idx + 1}` },
					obj('Fraflyttingsland', item.fraflyttingsland, AdresseKodeverk.InnvandretUtvandretLand),
					obj('Fraflyttingssted', item.fraflyttingsstedIUtlandet),
					obj('Fraflyttingsdato', formatDate(item.innflyttingsdato)),
				]
			}),
		}
		data.push(innflyttingData)
	}
}

const mapUtflytting = (utflytting, data) => {
	if (utflytting) {
		const utflyttingData = {
			header: 'Utvandring',
			itemRows: utflytting.map((item, idx) => {
				return [
					{ numberHeader: `Utvandring ${idx + 1}` },
					obj('Tilflyttingsland', item.tilflyttingsland, AdresseKodeverk.InnvandretUtvandretLand),
					obj('Tilflyttingssted', item.tilflyttingsstedIUtlandet),
					obj('Utvandringsdato', formatDate(item.utflyttingsdato)),
				]
			}),
		}
		data.push(utflyttingData)
	}
}

const mapKjoenn = (kjoenn, data) => {
	if (kjoenn) {
		const kjoennData = {
			header: 'Kjønn',
			itemRows: kjoenn.map((item, idx) => {
				return [
					{ numberHeader: kjoenn?.length > 1 && `Kjønn ${idx + 1}` },
					obj('Kjønn', showLabel('kjoenn', item.kjoenn)),
				]
			}),
		}
		data.push(kjoennData)
	}
}

const mapNavn = (navn, data) => {
	if (navn) {
		const navnData = {
			header: 'Navn',
			itemRows: navn.map((item, idx) => {
				return [
					{ numberHeader: `Navn ${idx + 1}` },
					obj('Fornavn', item.fornavn),
					obj('Mellomnavn', item.mellomnavn),
					obj('Etternavn', item.etternavn),
					obj('Har tilfeldig mellomnavn', oversettBoolean(item.hasMellomnavn)),
					obj('Gyldig f.o.m. dato', formatDate(item.gyldigFraOgMed)),
				]
			}),
		}
		data.push(navnData)
	}
}

const mapTelefonnummer = (telefonnummer, data) => {
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
}

const mapVergemaal = (vergemaal, data) => {
	if (vergemaal) {
		const vergemaalData = {
			header: 'Vergemål',
			itemRows: vergemaal.map((item, idx) => {
				return [
					{ numberHeader: `Vergemål ${idx + 1}` },
					obj('Fylkesembete', item.vergemaalEmbete, VergemaalKodeverk.Fylkesmannsembeter),
					obj('Sakstype', item.sakType, VergemaalKodeverk.Sakstype),
					obj('Mandattype', item.mandatType, VergemaalKodeverk.Mandattype),
					obj('Gyldig f.o.m.', formatDate(item.gyldigFraOgMed)),
					obj('Gyldig t.o.m.', formatDate(item.gyldigTilOgMed)),
					obj('Verge', item.vergeIdent),
					...personRelatertTil(item, 'nyVergeIdent'),
				]
			}),
		}
		data.push(vergemaalData)
	}
}

const mapFullmakt = (bestillingData, data) => {
	if (bestillingData?.fullmakt) {
		const fullmaktData = {
			header: 'Fullmakt',
			itemRows: bestillingData.fullmakt.map((item, idx) => {
				return [
					{ numberHeader: `Fullmakt ${idx + 1}` },
					obj('Områder', omraaderArrayToString(item.omraade)),
					obj('Gyldig fra og med', formatDate(item.gyldigFraOgMed)),
					obj('Gyldig til og med', formatDate(item.gyldigTilOgMed)),
					obj('Fullmektig', item.motpartsPersonident),
					...personRelatertTil(item, 'nyFullmektig'),
				]
			}),
		}
		data.push(fullmaktData)
	}
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
		obj('Bydelsnummer', adresseData.bydelsnummer),
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
		obj('Flyttedato', formatDate(datoData.angittFlyttedato)),
		obj('Gyldig f.o.m.', formatDate(datoData.gyldigFraOgMed)),
		obj('Gyldig t.o.m.', formatDate(datoData.gyldigTilOgMed)),
	]
}

const getNavn = (navn) => {
	return navn ? navn : ''
}

const coAdresse = (navn) => {
	const fornavn = navn?.fornavn
	const mellomnavn = navn?.mellomnavn
	const etternavn = navn?.etternavn

	return [
		obj(
			'C/O adressenavn',
			fornavn || mellomnavn || etternavn
				? `${getNavn(fornavn)} ${getNavn(mellomnavn)} ${getNavn(etternavn)}`
				: null,
		),
	]
}

const mapTilrettelagtKommunikasjon = (tilrettelagtKommunikasjon, data) => {
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
}

const mapStatsborgerskap = (statsborgerskap, data) => {
	if (statsborgerskap) {
		const statsborgerskapData = {
			header: 'Statsborgerskap',
			itemRows: statsborgerskap.map((item, idx) => {
				return [
					{ numberHeader: `Statsborgerskap ${idx + 1}` },
					obj('Statsborgerskap', item.landkode, AdresseKodeverk.StatsborgerskapLand),
					obj('Statsborgerskap fra', formatDate(item.gyldigFraOgMed)),
					obj('Statsborgerskap til', formatDate(item.gyldigTilOgMed)),
					obj('Bekreftelsesdato', formatDate(item.bekreftelsesdato)),
				]
			}),
		}
		data.push(statsborgerskapData)
	}
}

const mapDoedsfall = (doedsfall, data) => {
	if (doedsfall) {
		const doedsfallData = {
			header: 'Dødsfall',
			itemRows: doedsfall.map((item, idx) => {
				return [
					{ numberHeader: `Dødsfall ${idx + 1}` },
					obj('Dødsdato', formatDate(item.doedsdato)),
				]
			}),
		}
		data.push(doedsfallData)
	}
}

const ingenVerdierSatt = 'Ingen verdier satt'

const adresseVerdi = (adresseData) => {
	return isEmpty(adresseData) && ingenVerdierSatt
}

const mapBostedsadresse = (bostedsadresse, data) => {
	if (bostedsadresse) {
		const bostedsadresseData = {
			header: 'Bostedsadresse',
			itemRows: bostedsadresse.map((item, idx) => {
				if (item.vegadresse) {
					const adresseData = item.vegadresse
					return [
						{ numberHeader: `Bostedsadresse ${idx + 1}: Vegadresse` },
						obj('Vegadresse', adresseVerdi(adresseData)),
						...vegadresse(adresseData),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.matrikkeladresse) {
					const adresseData = item.matrikkeladresse
					return [
						{ numberHeader: `Bostedsadresse ${idx + 1}: Matrikkeladresse` },
						obj('Matrikkeladresse', adresseVerdi(adresseData)),
						...matrikkeladresse(adresseData),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.utenlandskAdresse) {
					const adresseData = item.utenlandskAdresse
					return [
						{ numberHeader: `Bostedsadresse ${idx + 1}: Utenlandsk adresse` },
						obj('Utenlandsk adresse', adresseVerdi(adresseData)),
						...utenlandskAdresse(adresseData),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.ukjentBosted) {
					const adresseData = item.ukjentBosted
					return [
						{ numberHeader: `Bostedsadresse ${idx + 1}: Ukjent bosted` },
						obj('Ukjent bosted', adresseVerdi(adresseData)),
						obj('Bostedskommune', adresseData.bostedskommune, AdresseKodeverk.Kommunenummer),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				return [
					obj('Bostedsadresse', ingenVerdierSatt),
					...datoer(item),
					...coAdresse(item.opprettCoAdresseNavn),
				]
			}),
		}
		data.push(bostedsadresseData)
	}
}

const mapOppholdsadresse = (oppholdsadresse, data) => {
	if (oppholdsadresse) {
		const oppholdsadresseData = {
			header: 'Oppholdsadresse',
			itemRows: oppholdsadresse.map((item, idx) => {
				if (item.vegadresse) {
					const adresseData = item.vegadresse
					return [
						{ numberHeader: `Oppholdsadresse ${idx + 1}: Vegadresse` },
						obj('Vegadresse', adresseVerdi(adresseData)),
						...vegadresse(adresseData),
						...datoer(item),
						obj('Opphold annet sted', showLabel('oppholdAnnetSted', item.oppholdAnnetSted)),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.matrikkeladresse) {
					const adresseData = item.matrikkeladresse
					return [
						{ numberHeader: `Oppholdsadresse ${idx + 1}: Matrikkeladresse` },
						obj('Matrikkeladresse', adresseVerdi(adresseData)),
						...matrikkeladresse(adresseData),
						...datoer(item),
						obj('Opphold annet sted', showLabel('oppholdAnnetSted', item.oppholdAnnetSted)),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.utenlandskAdresse) {
					const adresseData = item.utenlandskAdresse
					return [
						{ numberHeader: `Oppholdsadresse ${idx + 1}: Utenlandsk adresse` },
						obj('Utenlandsk adresse', adresseVerdi(adresseData)),
						...utenlandskAdresse(adresseData),
						...datoer(item),
						obj('Opphold annet sted', showLabel('oppholdAnnetSted', item.oppholdAnnetSted)),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				return [obj('Oppholdsadresse', ingenVerdierSatt), ...datoer(item)]
			}),
		}
		data.push(oppholdsadresseData)
	}
}

const mapKontaktadresse = (kontaktadresse, data) => {
	if (kontaktadresse) {
		const kontaktadresseData = {
			header: 'Kontaktadresse',
			itemRows: kontaktadresse.map((item, idx) => {
				if (item.vegadresse) {
					const adresseData = item.vegadresse
					return [
						{ numberHeader: `Kontaktadresse ${idx + 1}: Vegadresse` },
						obj('Vegadresse', adresseVerdi(adresseData)),
						...vegadresse(adresseData),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.utenlandskAdresse) {
					const adresseData = item.utenlandskAdresse
					return [
						{ numberHeader: `Kontaktadresse ${idx + 1}: Utenlandsk adresse` },
						obj('Utenlandsk adresse', adresseVerdi(adresseData)),
						...utenlandskAdresse(adresseData),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				if (item.postboksadresse) {
					const adresseData = item.postboksadresse
					return [
						{ numberHeader: `Kontaktadresse ${idx + 1}: Postboksadresse` },
						obj('Postboksadresse', adresseVerdi(adresseData)),
						obj('Postbokseier', adresseData.postbokseier),
						obj('Postboks', adresseData.postboks),
						obj('Postnummer', adresseData.postnummer),
						...datoer(item),
						...coAdresse(item.opprettCoAdresseNavn),
					]
				}
				return [obj('Kontaktadresse', ingenVerdierSatt), ...datoer(item)]
			}),
		}
		data.push(kontaktadresseData)
	}
}

const mapDeltBosted = (deltBosted, data) => {
	if (deltBosted?.length > 0) {
		const deltBostedData = deltBosted[0]
		data.push({
			header: 'Delt bosted',
			items: [
				obj('Har delt bosted', 'Ja'),
				obj('Startdato for kontrakt', formatDate(deltBostedData.startdatoForKontrakt)),
				obj('Sluttdato for kontrakt', formatDate(deltBostedData.sluttdatoForKontrakt)),
			],
		})
	}
}

const mapAdressebeskyttelse = (adressebeskyttelse, data) => {
	if (adressebeskyttelse) {
		const adressebeskyttelseData = {
			header: 'Adressebeskyttelse',
			itemRows: adressebeskyttelse.map((item, idx) => {
				return [
					{ numberHeader: `Adressebeskyttelse ${idx + 1}` },
					obj('Gradering', showLabel('gradering', item.gradering)),
				]
			}),
		}
		data.push(adressebeskyttelseData)
	}
}

const mapSikkerhetstiltak = (sikkerhetstiltak, data) => {
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
					obj('Gyldig fra og med', formatDate(item.gyldigFraOgMed)),
					obj('Gyldig til og med', formatDate(item.gyldigTilOgMed)),
				]
			}),
		}
		data.push(sikkerhetstiltakData)
	}
}

const mapSivilstand = (sivilstand, data) => {
	if (sivilstand) {
		const sivilstandData = {
			header: 'Sivilstand (partner)',
			itemRows: sivilstand.map((item, idx) => {
				return [
					{ numberHeader: `Sivilstand ${idx + 1}` },
					obj('Type sivilstand', showLabel('sivilstandType', item.type)),
					obj('Gyldig fra og med', formatDate(item.sivilstandsdato)),
					obj('Bekreftelsesdato', formatDate(item.bekreftelsesdato)),
					obj('Bor ikke sammen', oversettBoolean(item.borIkkeSammen)),
					obj('Person relatert til', item.relatertVedSivilstand),
					...personRelatertTil(item, 'nyRelatertPerson'),
				]
			}),
		}
		data.push(sivilstandData)
	}
}

const deltBosted = (personData, path) => {
	const deltBostedData = _.get(personData, path)
	const fellesVerdier = [
		obj('Har delt bosted', 'Ja'),
		obj('Startdato for kontrakt', formatDate(deltBostedData?.startdatoForKontrakt)),
		obj('Sluttdato for kontrakt', formatDate(deltBostedData?.sluttdatoForKontrakt)),
	]
	return [expandable('DELT BOSTED', !!deltBostedData, [...fellesVerdier])]
}

const mapForelderBarnRelasjon = (forelderBarnRelasjon, data) => {
	if (forelderBarnRelasjon) {
		const foreldreBarnData = {
			header: 'Barn/foreldre',
			itemRows: forelderBarnRelasjon.map((item, idx) => {
				return [
					{ numberHeader: `Relasjon ${idx + 1}` },
					obj('Relasjon', showLabel('pdlRelasjonTyper', item.relatertPersonsRolle)),
					obj(
						'Rolle for barn',
						item.relatertPersonsRolle === 'BARN' &&
							showLabel('pdlRelasjonTyper', item.minRolleForPerson),
					),
					obj('Bor ikke sammen', oversettBoolean(item.borIkkeSammen)),
					obj('Partner ikke forelder', oversettBoolean(item.partnerErIkkeForelder)),
					obj('Person relatert til', item.relatertPerson),
					...deltBosted(item, 'deltBosted'),
					...personRelatertTil(item, 'nyRelatertPerson'),
					...personRelatertTil(item, 'relatertPersonUtenFolkeregisteridentifikator'),
				]
			}),
		}
		data.push(foreldreBarnData)
	}
}

const mapForeldreansvar = (foreldreansvar, data) => {
	if (foreldreansvar) {
		const foreldreansvarData = {
			header: 'Foreldreansvar',
			itemRows: foreldreansvar.map((item, idx) => {
				return [
					{ numberHeader: `Foreldreansvar ${idx + 1}` },
					obj('Hvem har ansvaret', showLabel('foreldreansvar', item.ansvar)),
					obj('Gyldig fra og med', formatDate(item.gyldigFraOgMed)),
					obj('Gyldig til og med', formatDate(item.gyldigTilOgMed)),
					obj(
						'Type ansvarlig',
						(item.ansvarlig && 'Eksisterende person') ||
							(item.nyAnsvarlig && 'Ny person') ||
							(item.ansvarligUtenIdentifikator && 'Person uten identifikator'),
					),
					obj('Ansvarlig', showLabel('foreldreansvar', item.ansvarlig)),
					obj('Ansvarssubjekt', showLabel('foreldreansvar', item.ansvarssubjekt)),
					obj('Identtype', item.nyAnsvarlig?.identtype),
					obj('Kjønn', item.nyAnsvarlig?.kjoenn),
					obj('Født etter', formatDate(item.nyAnsvarlig?.foedtEtter)),
					obj('Født før', formatDate(item.nyAnsvarlig?.foedtFoer)),
					obj('Alder', item.nyAnsvarlig?.alder),
					obj(
						'Statsborgerskap',
						item.nyAnsvarlig?.statsborgerskapLandkode,
						AdresseKodeverk.StatsborgerskapLand,
					),
					obj('Gradering', showLabel('gradering', item.nyAnsvarlig?.gradering)),
					obj('Har mellomnavn', item.nyAnsvarlig?.nyttNavn?.hasMellomnavn && 'JA'),
					obj('Kjønn', item.ansvarligUtenIdentifikator?.kjoenn),
					obj('Fødselsdato', formatDate(item.ansvarligUtenIdentifikator?.foedselsdato)),
					obj(
						'Statsborgerskap',
						item.ansvarligUtenIdentifikator?.statsborgerskap,
						AdresseKodeverk.StatsborgerskapLand,
					),
					obj('Fornavn', item.ansvarligUtenIdentifikator?.navn?.fornavn),
					obj('Mellomnavn', item.ansvarligUtenIdentifikator?.navn?.mellomnavn),
					obj('Etternavn', item.ansvarligUtenIdentifikator?.navn?.etternavn),
				]
			}),
		}
		data.push(foreldreansvarData)
	}
}

const mapDoedfoedtBarn = (doedfoedtBarn, data) => {
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
				obj('Dødsdato', formatDate(item.dato)),
			])
		})

		data.push(doedfoedtBarnData)
	}
}

const mapFalskIdentitet = (falskIdentitet, data) => {
	const sjekkRettIdent = (item) => {
		if (_.has(item, 'rettIdentitetErUkjent')) {
			return 'Ukjent'
		} else if (_.has(item, 'rettIdentitetVedIdentifikasjonsnummer')) {
			return 'Ved identifikasjonsnummer'
		}
		return _.has(item, 'rettIdentitetVedOpplysninger') ? 'Ved personopplysninger' : 'Ingen'
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
					obj('Fødselsdato', formatDate(item.rettIdentitetVedOpplysninger?.foedselsdato)),
					obj('Kjønn', item.rettIdentitetVedOpplysninger?.kjoenn),
					obj('Statsborgerskap', item.rettIdentitetVedOpplysninger?.statsborgerskap?.join(', ')),
				]
			}),
		}
		data.push(falskIdentitetData)
	}
}

const mapUtenlandskIdentifikasjonsnummer = (utenlandskIdentifikasjonsnummer, data) => {
	if (utenlandskIdentifikasjonsnummer) {
		const utenlandskIdentData = {
			header: 'Utenlandsk identifikasjonsnummer',
			itemRows: utenlandskIdentifikasjonsnummer.map((item, idx) => {
				return [
					{
						numberHeader: `Utenlandsk ID ${idx + 1}`,
					},
					obj('Utenlandsk ID', item.identifikasjonsnummer),
					obj('Utstederland', item.utstederland, AdresseKodeverk.Utstederland),
					obj('Utenlandsk ID opphørt', oversettBoolean(item.opphoert)),
				]
			}),
		}
		data.push(utenlandskIdentData)
	}
}

const mapNyIdent = (nyident, data) => {
	if (nyident) {
		const nyidentData = {
			header: 'Ny identitet',
			itemRows: nyident.map((item, idx) => {
				return isEmpty(item, ['kilde', 'master', 'syntetisk'])
					? [obj('Ny identitet', ingenVerdierSatt)]
					: [
							{
								numberHeader: `Ny identitet ${idx + 1}`,
							},
							obj('Eksisterende ident', item.eksisterendeIdent),
							obj('Identtype', item.identtype),
							obj('Kjønn', item.kjoenn),
							obj('Født etter', formatDate(item.foedtEtter)),
							obj('Født før', formatDate(item.foedtFoer)),
							obj('Alder', item.alder),
							obj('Har mellomnavn', item.nyttNavn?.hasMellomnavn && 'JA'),
						]
			}),
		}
		data.push(nyidentData)
	}
}

const kontaktinfoFellesVerdier = (item) => {
	const {
		skifteform,
		attestutstedelsesdato,
		kontaktType,
		advokatSomKontakt,
		organisasjonSomKontakt,
		personSomKontakt,
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
		obj('Skifteform', skifteform),
		obj('Utstedelsesdato skifteattest', formatDate(attestutstedelsesdato)),
		obj('Kontakttype', kontaktType ? showLabel('kontaktType', kontaktType) : getKontakttype()),
	]
}

const mapKontaktinformasjonForDoedsbo = (kontaktinformasjonForDoedsbo, data) => {
	if (kontaktinformasjonForDoedsbo) {
		const doedsboData = {
			header: 'Kontaktinformasjon for dødsbo',
			itemRows: kontaktinformasjonForDoedsbo.map((item, idx) => {
				const { advokatSomKontakt, organisasjonSomKontakt, personSomKontakt, adresse } = item

				const kontaktperson = (person) => {
					return [
						obj('Kontaktperson fornavn', person?.fornavn),
						obj('Kontaktperson mellomnavn', person?.mellomnavn),
						obj('Kontaktperson etternavn', person?.etternavn),
					]
				}

				const kontaktinfoAdresse = [
					obj('Land', adresse?.landkode, AdresseKodeverk.PostadresseLand),
					obj('Adresselinje 1', adresse?.adresselinje1),
					obj('Adresselinje 2', adresse?.adresselinje2),
					obj(
						'Postnummer og -sted',
						(adresse?.postnummer || adresse?.poststedsnavn) &&
							`${adresse?.postnummer} ${adresse?.poststedsnavn}`,
					),
				]

				if (personSomKontakt) {
					return [
						{ numberHeader: `Kontaktinformasjon for dødsbo ${idx + 1}` },
						...kontaktinfoFellesVerdier(item),
						obj('Identifikasjonsnummer', personSomKontakt?.identifikasjonsnummer),
						obj('Fødselsdato', formatDate(personSomKontakt?.foedselsdato)),
						...kontaktperson(personSomKontakt?.navn),
						...kontaktinfoAdresse,
						...personRelatertTil(item, 'personSomKontakt.nyKontaktperson'),
					]
				}
				if (advokatSomKontakt) {
					return [
						{ numberHeader: `Kontaktinformasjon for dødsbo ${idx + 1}` },
						...kontaktinfoFellesVerdier(item),
						obj('Organisasjonsnummer', advokatSomKontakt.organisasjonsnummer),
						obj('Organisasjonsnavn', advokatSomKontakt.organisasjonsnavn),
						...kontaktperson(advokatSomKontakt.kontaktperson),
						...kontaktinfoAdresse,
					]
				}

				if (organisasjonSomKontakt) {
					return [
						{ numberHeader: `Kontaktinformasjon for dødsbo ${idx + 1}` },
						...kontaktinfoFellesVerdier(item),
						obj('Organisasjonsnummer', organisasjonSomKontakt.organisasjonsnummer),
						obj('Organisasjonsnavn', organisasjonSomKontakt.organisasjonsnavn),
						...kontaktperson(organisasjonSomKontakt.kontaktperson),
						...kontaktinfoAdresse,
					]
				}

				return [
					{ numberHeader: `Kontaktinformasjon for dødsbo ${idx + 1}` },
					...kontaktinfoFellesVerdier(item),
					...kontaktinfoAdresse,
					...personRelatertTil(item, 'personSomKontakt.nyKontaktperson'),
				]
			}),
		}
		data.push(doedsboData)
	}
}

const mapNomData = (bestillingData, data) => {
	if (bestillingData?.nomdata) {
		const { startDato, sluttDato } = bestillingData.nomdata
		const nomdata = {
			header: 'Nav-ansatt (NOM)',
			items:
				!startDato && !sluttDato
					? [obj('Nav-ansatt', 'Ingen verdier satt')]
					: [obj('Startdato', formatDate(startDato)), obj('Sluttdato', formatDate(sluttDato))],
		}
		data.push(nomdata)
	}
}

const mapSkjermingData = (bestillingData, data) => {
	const tpsMessaging = _.get(bestillingData, 'tpsMessaging')
	const skjerming = _.get(bestillingData, 'skjerming')

	if (
		skjerming?.egenAnsattDatoFom ||
		tpsMessaging?.egenAnsattDatoFom ||
		skjerming?.egenAnsattDatoTom ||
		tpsMessaging?.egenAnsattDatoTom
	) {
		const skjermingData = {
			header: 'Skjerming',
			items: [
				obj(
					'Skjerming fra',
					formatDate(skjerming?.egenAnsattDatoFom || tpsMessaging?.egenAnsattDatoFom),
				),
				obj(
					'Skjerming til',
					formatDate(skjerming?.egenAnsattDatoTom || tpsMessaging?.egenAnsattDatoTom),
				),
			],
		}
		data.push(skjermingData)
	}
}

const mapBankkonto = (bestillingData, data) => {
	const bankkonto = _.get(bestillingData, 'bankkonto')

	if (bankkonto?.norskBankkonto || bankkonto?.utenlandskBankkonto) {
		if (bankkonto.norskBankkonto) {
			const norskBankkontoData = {
				header: 'Norsk bankkonto',
				items: [
					obj('Kontonummer', bankkonto.norskBankkonto.kontonummer),
					obj('Tilfeldig kontonummer', bankkonto.norskBankkonto.tilfeldigKontonummer && 'Ja'),
				],
			}
			data.push(norskBankkontoData)
		}

		if (bankkonto.utenlandskBankkonto) {
			const utenlandskBankkontoData = {
				header: 'Utenlandsk bankkonto',
				items: [
					obj('Kontonummer', bankkonto.utenlandskBankkonto.kontonummer),
					obj('Tilfeldig kontonummer', bankkonto.utenlandskBankkonto.tilfeldigKontonummer && 'Ja'),
					obj('Swift kode', bankkonto.utenlandskBankkonto.swift),
					obj('Land', bankkonto.utenlandskBankkonto.landkode),
					obj('Banknavn', bankkonto.utenlandskBankkonto.banknavn),
					obj('Bankkode', bankkonto.utenlandskBankkonto.iban),
					obj('Valuta', bankkonto.utenlandskBankkonto.valuta),
					obj('Adresselinje 1', bankkonto.utenlandskBankkonto.bankAdresse1),
					obj('Adresselinje 2', bankkonto.utenlandskBankkonto.bankAdresse2),
					obj('Adresselinje 3', bankkonto.utenlandskBankkonto.bankAdresse3),
				],
			}
			data.push(utenlandskBankkontoData)
		}
	}
}

export const arbeidsforholdVisning = (arbeidsforhold, i, aaregKriterier, amelding) => [
	{
		numberHeader: `Arbeidsforhold ${i + 1}`,
	},
	obj('A-melding', amelding),
	{
		label: 'Type arbeidsforhold',
		value: arbeidsforhold.arbeidsforholdstype || aaregKriterier?.arbeidsforholdstype,
		apiKodeverkId: ArbeidKodeverk.Arbeidsforholdstyper,
	},
	obj('Orgnummer', arbeidsforhold.arbeidsgiver?.orgnummer),
	obj('Arbeidsgiver ident', arbeidsforhold.arbeidsgiver?.ident),
	obj('Arbeidsforhold-ID', arbeidsforhold.arbeidsforholdId),
	obj('Ansatt fra', formatDate(arbeidsforhold.ansettelsesPeriode?.fom)),
	obj('Ansatt til', formatDate(arbeidsforhold.ansettelsesPeriode?.tom)),
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
			: arbeidsforhold.arbeidsavtale?.stillingsprosent,
	),
	obj(
		'Endringsdato stillingprosent',
		formatDate(arbeidsforhold.arbeidsavtale?.endringsdatoStillingsprosent),
	),
	obj('Endringsdato lønn', formatDate(arbeidsforhold.arbeidsavtale?.sisteLoennsendringsdato)),
	{
		label: 'Arbeidstidsordning',
		value: arbeidsforhold.arbeidsavtale?.arbeidstidsordning,
		apiKodeverkId: ArbeidKodeverk.Arbeidstidsordninger,
	},
	obj('Avtalte arbeidstimer per uke', arbeidsforhold.arbeidsavtale?.avtaltArbeidstimerPerUke),
	{
		label: 'Skipsregister',
		value: arbeidsforhold.fartoy?.[0]?.skipsregister,
		apiKodeverkId: ArbeidKodeverk.Skipsregistre,
	},
	{
		label: 'Fartøystype',
		value: arbeidsforhold.fartoy?.[0]?.skipstype,
		apiKodeverkId: ArbeidKodeverk.Skipstyper,
	},
	{
		label: 'Fartsområde',
		value: arbeidsforhold.fartoy?.[0]?.fartsomraade,
		apiKodeverkId: ArbeidKodeverk.Fartsomraader,
	},
	obj('Perioder med antall timer for timelønnet', arbeidsforhold.antallTimerForTimeloennet?.length),
	obj('Perioder med utenlandsopphold', arbeidsforhold.utenlandsopphold?.length),
	obj('Perioder med permisjon', arbeidsforhold.permisjon?.length),
	obj('Perioder med permittering', arbeidsforhold.permittering?.length),
]

const mapAareg = (bestillingData, data) => {
	const aaregKriterier = bestillingData.aareg
	if (aaregKriterier) {
		const aareg = {
			header: 'Arbeidsforhold (Aareg)',
			items: [],
			itemRows: [],
			paginering: [],
			pagineringPages: [],
		}

		aaregKriterier?.forEach((arbeidsforhold, i) => {
			const amelding = arbeidsforhold.amelding?.length > 0 ? 'Ikke lenger støttet' : null
			aareg.itemRows.push(arbeidsforholdVisning(arbeidsforhold, i, aaregKriterier, amelding))
		})

		data.push(aareg)
	}
}

const mapSigrunstubPensjonsgivende = (bestillingData, data) => {
	const sigrunstubPensjonsgivendeKriterier = bestillingData.sigrunstubPensjonsgivende

	const sigrunstubPensjonsgivende = {
		header: 'Pensjonsgivende inntekt (Sigrun)',
		itemRows: [],
	}

	if (sigrunstubPensjonsgivendeKriterier) {
		sigrunstubPensjonsgivendeKriterier.forEach((inntekt, i) => {
			sigrunstubPensjonsgivende.itemRows.push([
				{ numberHeader: `Pensjonsgivende inntekt ${i + 1}` },
				obj('Inntektsår', inntekt.inntektsaar),
				obj('Testdataeier', inntekt.testdataEier),
				{ nestedItemRows: [] },
			])
			inntekt.pensjonsgivendeInntekt.forEach((test, y) => {
				const dynamicObjects = Object.entries(test)?.map(([key, value]) => {
					const erDato = !isNaN(Date.parse(value))
					if (erDato && (key.includes('Dato') || key.includes('dato'))) {
						return obj(kodeverkKeyToLabel(key), formatDate(value))
					}
					return obj(kodeverkKeyToLabel(key), value?.toString())
				})
				sigrunstubPensjonsgivende.itemRows[i][3]?.nestedItemRows?.push([
					{
						numberHeader: `Inntekt ${y + 1}`,
					},
					dynamicObjects,
				])
			})
		})
		data.push(sigrunstubPensjonsgivende)
	}
}

const mapSigrunstubSummertSkattegrunnlag = (bestillingData, data) => {
	function mapGrunnlag(grunnlag, header) {
		return [
			{ numberHeader: header },
			[
				obj('Teknisk navn', `${grunnlag.tekniskNavn}`, tekniskNavnKodeverk),
				obj('Beløp', grunnlag.beloep),
				obj('Kategori', grunnlag.kategori, kategoriKodeverk),
				obj('Andel fra barn', grunnlag.andelOverfoertFraBarn),
				obj(
					'Antall spesifiseringer',
					grunnlag.spesifisering?.length > 0 && grunnlag.spesifisering?.length,
				),
			],
		]
	}

	const kriterier = bestillingData.sigrunstubSummertSkattegrunnlag

	if (!kriterier) return

	const summertSkattegrunnlag = {
		header: 'Summert skattegrunnlag (Sigrun)',
		itemRows: [],
	}

	kriterier.forEach((skattegrunnlag, i) => {
		const baseRow = [
			{ numberHeader: `Summert skattegrunnlag ${i + 1}` },
			obj('Inntektsår', skattegrunnlag.inntektsaar),
			obj('Ajourholdstidspunkt', formatDateTime(skattegrunnlag.ajourholdstidspunkt)),
			obj('Skatteoppgjørsdato', formatDate(skattegrunnlag.skatteoppgjoersdato)),
			obj('Skjermet', oversettBoolean(skattegrunnlag.skjermet)),
			obj('Stadie', skattegrunnlag.stadie),
		]

		if (
			skattegrunnlag.grunnlag?.length > 0 ||
			skattegrunnlag.kildeskattPaaLoennGrunnlag?.length > 0 ||
			skattegrunnlag.svalbardGrunnlag?.length > 0
		) {
			baseRow.push({
				nestedItemRows: [
					...skattegrunnlag.grunnlag?.map((grunnlag, idx) => {
						return mapGrunnlag(grunnlag, `Grunnlag ${idx + 1}`)
					}),

					...skattegrunnlag.kildeskattPaaLoennGrunnlag?.map((grunnlag, idx) => {
						return mapGrunnlag(grunnlag, `Kildeskatt på lønnsgrunnlag ${idx + 1}`)
					}),

					...skattegrunnlag.svalbardGrunnlag?.map((grunnlag, idx) => {
						return mapGrunnlag(grunnlag, `Svalbard grunnlag ${idx + 1}`)
					}),
				],
			})
		}

		summertSkattegrunnlag.itemRows.push(baseRow)
	})

	data.push(summertSkattegrunnlag)
}
const mapInntektStub = (bestillingData, data) => {
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
					obj('Rapporteringstidspunkt', formatDateTime(inntektsinfo.rapporteringsdato)),
					obj('Generer antall måneder', inntektsinfo.antallMaaneder),
					obj('Virksomhet (orgnr/id)', inntektsinfo.virksomhet),
					obj('Opplysningspliktig (orgnr/id)', inntektsinfo.opplysningspliktig),
					obj(
						'Antall registrerte inntekter',
						inntektsinfo.inntektsliste && inntektsinfo.inntektsliste?.length,
					),
					obj(
						'Antall registrerte fradrag',
						inntektsinfo.fradragsliste && inntektsinfo.fradragsliste?.length,
					),
					obj(
						'Antall registrerte forskuddstrekk',
						inntektsinfo.forskuddstrekksliste && inntektsinfo.forskuddstrekksliste?.length,
					),
					obj(
						'Antall registrerte arbeidsforhold',
						inntektsinfo.arbeidsforholdsliste && inntektsinfo.arbeidsforholdsliste?.length,
					),
					obj(
						'Antall registrerte inntektsendringer (historikk)',
						inntektsinfo.historikk && inntektsinfo.historikk?.length,
					),
				])
			})

		data.push(inntektStub)
	}
}

const mapArbeidsplassenCV = (bestillingData, data) => {
	const CVKriterier = _.get(bestillingData, 'arbeidsplassenCV')

	if (CVKriterier) {
		const arbeidsplassenCV = {
			header: 'Nav CV',
			items: [],
			itemRows: [],
		}

		if (CVKriterier.jobboensker) {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: 'Jobbønsker' },
				obj(
					'Jobber og yrker',
					arrayToString(CVKriterier.jobboensker?.occupations?.map((jobb) => jobb?.title)),
				),
				obj(
					'Områder',
					arrayToString(CVKriterier.jobboensker?.locations?.map((omraade) => omraade?.location)),
				),
				obj(
					'Arbeidsmengde',
					arrayToString(
						CVKriterier.jobboensker?.workLoadTypes?.map((type) => showLabel('arbeidsmengde', type)),
					),
				),
				obj(
					'Arbeidstider',
					arrayToString(
						CVKriterier.jobboensker?.workScheduleTypes?.map((type) =>
							showLabel('arbeidstid', type),
						),
					),
				),
				obj(
					'Ansettelsestyper',
					arrayToString(
						CVKriterier.jobboensker?.occupationTypes?.map((type) =>
							showLabel('ansettelsestype', type),
						),
					),
				),
				obj('Oppstart', showLabel('oppstart', CVKriterier.jobboensker?.startOption)),
			])
		}

		CVKriterier.utdanning?.forEach((utdanning, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Utdanning ${i + 1}` },
				obj('Utdanningsnivå', showLabel('nusKoder', utdanning.nuskode)),
				obj('Grad og utdanningsretning', utdanning.field),
				obj('Skole/studiested', utdanning.institution),
				obj('Beskrivelse', utdanning.description),
				obj('Startdato', formatDate(utdanning.startDate)),
				obj('Sluttdato', formatDate(utdanning.endDate)),
				obj('Pågående utdanning', oversettBoolean(utdanning.ongoing)),
			])
		})

		CVKriterier.fagbrev?.forEach((fagbrev, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Fagbrev ${i + 1}` },
				obj('Fagdokumentasjon', fagbrev.title),
			])
		})

		CVKriterier.arbeidserfaring?.forEach((arbeidsforhold, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Arbeidserfaring ${i + 1}` },
				obj('Stilling/yrke', arbeidsforhold.jobTitle),
				obj('Alternativ tittel', arbeidsforhold.alternativeJobTitle),
				obj('Bedrift', arbeidsforhold.employer),
				obj('Sted', arbeidsforhold.location),
				obj('Arbeidsoppgaver', arbeidsforhold.description),
				obj('Ansatt fra', formatDate(arbeidsforhold.fromDate)),
				obj('Ansatt til', formatDate(arbeidsforhold.toDate)),
				obj('Nåværende jobb', oversettBoolean(arbeidsforhold.ongoing)),
			])
		})

		CVKriterier.annenErfaring?.forEach((annenErfaring, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Annen erfaring ${i + 1}` },
				obj('Rolle', annenErfaring.role),
				obj('Beskrivelse', annenErfaring.description),
				obj('Startdato', formatDate(annenErfaring.fromDate)),
				obj('Sluttdato', formatDate(annenErfaring.toDate)),
				obj('Pågående', oversettBoolean(annenErfaring.ongoing)),
			])
		})

		CVKriterier.kompetanser?.forEach((kompetanse, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Kompetanser ${i + 1}` },
				obj('Kompetanse', kompetanse.title),
			])
		})

		CVKriterier.offentligeGodkjenninger?.forEach((offentligGodkjenning, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Offentlig godkjenning ${i + 1}` },
				obj('Offentlig godkjenning', offentligGodkjenning.title),
				obj('Utsteder', offentligGodkjenning.issuer),
				obj('Fullført', formatDate(offentligGodkjenning.fromDate)),
				obj('Utløper', formatDate(offentligGodkjenning.toDate)),
			])
		})

		CVKriterier.andreGodkjenninger?.forEach((annenGodkjenning, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Andre godkjenninger ${i + 1}` },
				obj('Annen godkjenning', annenGodkjenning.certificateName),
				obj('Utsteder', annenGodkjenning.issuer),
				obj('Fullført', formatDate(annenGodkjenning.fromDate)),
				obj('Utløper', formatDate(annenGodkjenning.toDate)),
			])
		})

		CVKriterier.spraak?.forEach((spraak, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Språk ${i + 1}` },
				obj('Språk', spraak.language),
				obj('Muntlig', showLabel('spraakNivaa', spraak.oralProficiency)),
				obj('Skriftlig', showLabel('spraakNivaa', spraak.writtenProficiency)),
			])
		})

		CVKriterier.foererkort?.forEach((foererkort, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Førerkort ${i + 1}` },
				obj('Type førerkort', foererkort.type),
				obj('Gyldig fra', formatDate(foererkort.acquiredDate)),
				obj('Gyldig til', formatDate(foererkort.expiryDate)),
			])
		})

		CVKriterier.kurs?.forEach((kurs, i) => {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: `Kurs ${i + 1}` },
				obj('Kursnavn', kurs.title),
				obj('Kursholder', kurs.issuer),
				obj('Fullført', formatDate(kurs.date)),
				obj('Kurslengde', showLabel('kursLengde', kurs.durationUnit)),
				obj(
					`Antall ${
						kurs.durationUnit && kurs.durationUnit !== 'UKJENT'
							? showLabel('kursLengde', kurs.durationUnit)
							: ''
					}`,
					kurs.duration,
				),
			])
		})

		if (CVKriterier.sammendrag) {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: 'Sammendrag' },
				{
					label: 'Oppsummering',
					value: CVKriterier.sammendrag,
					width: 'xlarge',
				},
			])
		}

		if (_.has(CVKriterier, 'harHjemmel')) {
			arbeidsplassenCV.itemRows.push([
				{ numberHeader: 'Hjemmel' },
				{
					label: 'Godta hjemmel',
					value: oversettBoolean(CVKriterier.harHjemmel),
				},
			])
		}

		data.push(arbeidsplassenCV)
	}
}

const mapArbeidssoekerregisteret = (bestillingData, data) => {
	const arbeidssoekerregisteretKriterier = _.get(bestillingData, 'arbeidssoekerregisteret')

	if (arbeidssoekerregisteretKriterier) {
		const arbeidssoekerregisteret = {
			header: 'Arbeidssøkerregisteret',
			items: [
				obj('Utført av', showTyperLabel('BRUKERTYPE', arbeidssoekerregisteretKriterier.utfoertAv)),
				obj('Kilde', arbeidssoekerregisteretKriterier.kilde),
				obj('Årsak', arbeidssoekerregisteretKriterier.aarsak),
				obj('Utdanningsnivå', showTyperLabel('NUSKODE', arbeidssoekerregisteretKriterier.nuskode)),
				obj(
					'Beskrivelse av jobbsituasjonen',
					showTyperLabel(
						'JOBBSITUASJONSBESKRIVELSE',
						arbeidssoekerregisteretKriterier.jobbsituasjonsbeskrivelse,
					),
				),
				obj(
					'Utdanning bestått',
					oversettBoolean(arbeidssoekerregisteretKriterier.utdanningBestaatt),
				),
				obj(
					'Utdanning godkjent',
					oversettBoolean(arbeidssoekerregisteretKriterier.utdanningGodkjent),
				),
				obj(
					'Helsetilstand hindrer arbeid',
					oversettBoolean(arbeidssoekerregisteretKriterier.helsetilstandHindrerArbeid),
				),
				obj(
					'Andre forhold hindrer arbeid',
					oversettBoolean(arbeidssoekerregisteretKriterier.andreForholdHindrerArbeid),
				),
				obj(
					'Gjelder fra dato',
					formatDate(arbeidssoekerregisteretKriterier.jobbsituasjonsdetaljer?.gjelderFraDato),
				),
				obj(
					'Gjelder til dato',
					formatDate(arbeidssoekerregisteretKriterier.jobbsituasjonsdetaljer?.gjelderTilDato),
				),
				obj('Stilling', arbeidssoekerregisteretKriterier.jobbsituasjonsdetaljer?.stillingstittel),
				obj(
					'Stillingsprosent',
					arbeidssoekerregisteretKriterier.jobbsituasjonsdetaljer?.stillingsprosent,
				),
				obj(
					'Siste dag med lønn',
					formatDate(arbeidssoekerregisteretKriterier.jobbsituasjonsdetaljer?.sisteDagMedLoenn),
				),
				obj(
					'Siste arbeidsdag',
					formatDate(arbeidssoekerregisteretKriterier.jobbsituasjonsdetaljer?.sisteArbeidsdag),
				),
			],
		}
		data.push(arbeidssoekerregisteret)
	}
}

const mapSykemelding = (bestillingData, data) => {
	const sykemeldingKriterier = _.get(bestillingData, 'sykemelding')

	if (sykemeldingKriterier) {
		const sykemelding = {
			header: 'Sykemelding',
			items: [] as any,
		}
		if (sykemeldingKriterier.syntSykemelding) {
			sykemelding.items = [
				obj('Startdato', formatDate(sykemeldingKriterier.syntSykemelding.startDato)),
				obj('Organisasjonsnummer', sykemeldingKriterier.syntSykemelding.orgnummer),
				obj('Arbeidsforhold-ID', sykemeldingKriterier.syntSykemelding.arbeidsforholdId),
			]
		} else if (sykemeldingKriterier.nySykemelding) {
			sykemeldingKriterier.nySykemelding.aktivitet?.forEach((aktivitet: any) => {
				sykemelding.items.push(
					obj('Startdato', formatDate(aktivitet.fom)),
					obj('Sluttdato', formatDate(aktivitet.tom)),
				)
			})
		} else if (sykemeldingKriterier.detaljertSykemelding) {
			sykemelding.items = [
				obj('Startdato', formatDate(sykemeldingKriterier.detaljertSykemelding.startDato)),
				obj(
					'Trenger umiddelbar bistand',
					oversettBoolean(sykemeldingKriterier.detaljertSykemelding.umiddelbarBistand),
				),
				obj(
					'Manglende tilrettelegging på arbeidsplassen',
					oversettBoolean(
						sykemeldingKriterier.detaljertSykemelding.manglendeTilretteleggingPaaArbeidsplassen,
					),
				),
				obj('Diagnose', _.get(sykemeldingKriterier.detaljertSykemelding, 'hovedDiagnose.diagnose')),
				obj(
					'Diagnosekode',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'hovedDiagnose.diagnosekode'),
				),
				obj(
					'Antall registrerte bidiagnoser',
					sykemeldingKriterier.detaljertSykemelding.biDiagnoser &&
						sykemeldingKriterier.detaljertSykemelding.biDiagnoser?.length,
				),
				obj(
					'Helsepersonell navn',
					sykemeldingKriterier.detaljertSykemelding.helsepersonell &&
						`${sykemeldingKriterier.detaljertSykemelding.helsepersonell.fornavn} ${getNavn(
							sykemeldingKriterier.detaljertSykemelding.helsepersonell.mellomnavn,
						)} ${sykemeldingKriterier.detaljertSykemelding.helsepersonell.etternavn}`,
				),
				obj(
					'Helsepersonell ident',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'helsepersonell.ident'),
				),
				obj('HPR-nummer', _.get(sykemeldingKriterier.detaljertSykemelding, 'helsepersonell.hprId')),
				obj(
					'SamhandlerType',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'helsepersonell.samhandlerType'),
				),
				obj('Arbeidsgiver', _.get(sykemeldingKriterier.detaljertSykemelding, 'arbeidsgiver.navn')),
				obj(
					'Yrkesbetegnelse',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'arbeidsgiver.yrkesbetegnelse'),
					ArbeidKodeverk.Yrker,
				),
				obj(
					'Stillingsprosent',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'arbeidsgiver.stillingsprosent'),
				),
				obj(
					'Antall registrerte perioder',
					sykemeldingKriterier.detaljertSykemelding.perioder?.length,
				),
				obj(
					'Tiltak fra NAV',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'detaljer.tiltakNav'),
				),
				obj(
					'Tiltak på arbeidsplass',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'detaljer.tiltakArbeidsplass'),
				),
				obj(
					'Hensyn på arbeidsplass',
					_.get(sykemeldingKriterier.detaljertSykemelding, 'detaljer.beskrivHensynArbeidsplassen'),
				),
				obj(
					'Begrunnelse ikke kontakt',
					_.get(
						sykemeldingKriterier.detaljertSykemelding,
						'kontaktMedPasient.begrunnelseIkkeKontakt',
					),
				),
				obj(
					'Kontaktdato',
					formatDate(sykemeldingKriterier.detaljertSykemelding?.kontaktMedPasient?.kontaktDato),
				),
				obj(
					'Arbeidsfør etter endt periode',
					sykemeldingKriterier.detaljertSykemelding.detaljer &&
						oversettBoolean(
							sykemeldingKriterier.detaljertSykemelding.detaljer.arbeidsforEtterEndtPeriode,
						),
				),
			]
		}
		data.push(sykemelding)
	}
}

const mapYrkesskader = (bestillingData, data) => {
	const yrkesskadeKriterier = bestillingData.yrkesskader

	if (yrkesskadeKriterier) {
		const mapYrkesskadeKriterier = () => ({
			header: 'Yrkesskader',
			itemRows: yrkesskadeKriterier.map((yrkesskade, i) => [
				{
					numberHeader: `Yrkesskade ${i + 1}`,
				},
				obj('Rolletype', codeToNorskLabel(yrkesskade.rolletype)),
				obj('Innmelderrolle', codeToNorskLabel(yrkesskade.innmelderrolle)),
				obj('Klassifisering', showLabel('klassifisering', yrkesskade.klassifisering)),
				obj('Referanse', yrkesskade.referanse),
				obj('Ferdigstill sak', showLabel('ferdigstillSak', yrkesskade.ferdigstillSak)),
				obj('Tidstype', showLabel('tidstype', yrkesskade.tidstype)),
				obj('Skadetidspunkt', formatDateTime(yrkesskade.skadetidspunkt)),
				obj('Antall perioder', yrkesskade.perioder?.length),
			]),
		})
		data.push(mapYrkesskadeKriterier())
	}
}

const mapBrregstub = (bestillingData, data) => {
	const brregstubKriterier = bestillingData.brregstub

	if (brregstubKriterier) {
		const brregstub = {
			header: 'Brønnøysundregistrene',
			items: [obj('Understatuser', arrayToString(brregstubKriterier.understatuser))],
			itemRows: [],
		}
		brregstubKriterier.enheter.forEach((enhet, i) => {
			brregstub.itemRows.push([
				{ numberHeader: `Enhet ${i + 1}` },
				obj('Rolle', enhet.rolle),
				obj('Registreringsdato', formatDate(enhet.registreringsdato)),
				obj('Organisasjonsnummer', enhet.orgNr),
				obj('Foretaksnavn', enhet.foretaksNavn?.navn1),
				obj('Antall registrerte personroller', enhet.personroller && enhet.personroller?.length),
			])
		})

		data.push(brregstub)
	}
}

const mapMedlemskapsperiode = (bestillingData, data) => {
	const medlKriterier = bestillingData.medl

	if (medlKriterier) {
		const medl = {
			header: 'Medlemskap',
			items: [
				obj('Kilde', medlKriterier.kilde, MedlKodeverk.KILDE),
				obj('Fra dato', formatDate(medlKriterier.fraOgMed)),
				obj('Til dato', formatDate(medlKriterier.tilOgMed)),
				obj('Status', medlKriterier.status, MedlKodeverk.PERIODE_STATUS),
				obj('Grunnlag', medlKriterier.grunnlag, MedlKodeverk.GRUNNLAG),
				obj('Dekning', medlKriterier.dekning, MedlKodeverk.PERIODE_DEKNING),
				obj('Lovvalgsland', medlKriterier.lovvalgsland, MedlKodeverk.LANDKODER),
				obj('Lovvalg', medlKriterier.lovvalg, MedlKodeverk.LOVVALG_PERIODE),
				obj('Kildedokument', medlKriterier.kildedokument, MedlKodeverk.KILDE_DOK),
				obj('Delstudie', oversettBoolean(medlKriterier.studieinformasjon?.delstudie)),
				obj(
					'Er søknad innvilget',
					oversettBoolean(medlKriterier.studieinformasjon?.soeknadInnvilget),
				),
				obj('Studieland', medlKriterier.studieinformasjon?.studieland, MedlKodeverk.LANDKODER),
				obj(
					'Statsborgerland',
					medlKriterier.studieinformasjon?.statsborgerland,
					MedlKodeverk.LANDKODER,
				),
			],
		}
		data.push(medl)
	}
}

const jaNeiNull = (verdi) => {
	if (verdi === null || verdi === undefined) {
		return null
	}
	return verdi ? 'JA' : 'NEI'
}

const mapKrr = (bestillingData, data) => {
	const krrKriterier = bestillingData.krrstub

	if (krrKriterier) {
		const krrStub = {
			header: 'Kontaktinformasjon og reservasjon',
			items: [
				obj('Registrert i KRR', krrKriterier.registrert ? 'JA' : 'NEI'),
				{
					label: 'RESERVERT MOT DIGITALKOMMUNIKASJON',
					value: jaNeiNull(krrKriterier.reservert),
					width: 'medium',
				},
				obj('Epost', krrKriterier.epost),
				obj(
					'Mobilnummer',
					krrKriterier.registrert &&
						krrKriterier.mobil &&
						`${krrKriterier.landkode} ${krrKriterier.mobil}`,
				),
				obj('Språk', showLabel('spraaktype', krrKriterier.spraak)),
				obj('Gyldig fra', formatDate(krrKriterier.gyldigFra)),
				obj('Adresse', krrKriterier.sdpAdresse),
				obj('Leverandør', krrKriterier.sdpLeverandoer),
			],
		}

		data.push(krrStub)
	}
}

const mapArena = (bestillingData, data) => {
	const arenaKriterier = bestillingData.arenaforvalter

	if (arenaKriterier) {
		const arenaforvalter = {
			header: 'Arbeidsytelser',
			items: [
				obj('Brukertype', uppercaseAndUnderscoreToCapitalized(arenaKriterier.arenaBrukertype)),
				obj('Servicebehov', arenaKriterier.kvalifiseringsgruppe),
				obj('Aktiveringsdato', formatDate(arenaKriterier.aktiveringDato)),
				obj(
					'Automatisk innsending av meldekort',
					oversettBoolean(arenaKriterier.automatiskInnsendingAvMeldekort),
				),
				obj('Inaktiv fra dato', formatDate(arenaKriterier.inaktiveringDato)),
				obj('Har 11-5 vedtak', oversettBoolean(!!arenaKriterier.aap115?.[0])),
				obj(
					'AAP 11-5 fra dato',
					arenaKriterier.aap115?.[0] && formatDate(arenaKriterier.aap115[0].fraDato),
				),
				obj('Har AAP vedtak UA - positivt utfall', oversettBoolean(!!arenaKriterier.aap?.[0])),
				obj('AAP fra dato', arenaKriterier.aap?.[0] && formatDate(arenaKriterier.aap[0].fraDato)),
				obj('AAP til dato', arenaKriterier.aap?.[0] && formatDate(arenaKriterier.aap[0].tilDato)),
				obj('Har dagpengevedtak', oversettBoolean(!!arenaKriterier.dagpenger?.[0])),
				obj(
					'RettighetKode',
					arenaKriterier.dagpenger?.[0] &&
						showLabel('rettighetKode', arenaKriterier.dagpenger[0].rettighetKode),
				),
				obj(
					'Dagpenger fra dato',
					arenaKriterier.dagpenger?.[0] && formatDate(arenaKriterier.dagpenger[0].fraDato),
				),
				obj(
					'Dagpenger til dato',
					arenaKriterier.dagpenger?.[0] && formatDate(arenaKriterier.dagpenger[0].tilDato),
				),
			],
		}
		data.push(arenaforvalter)
	}
}

const mapInst = (bestillingData, data) => {
	const instKriterier = bestillingData.instdata

	if (instKriterier) {
		// Flater ut instKriterier for å gjøre det lettere å mappe

		const flatInstKriterier = []
		instKriterier.forEach((i) => {
			flatInstKriterier.push({
				institusjonstype: i.institusjonstype,
				varighet: i.varighet,
				startdato: i.startdato,
				forventetSluttdato: i.forventetSluttdato,
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
				obj('Institusjonstype', showLabel('institusjonstype', inst.institusjonstype)),
				obj('Varighet', inst.varighet && showLabel('varighet', inst.varighet)),
				obj('Startdato', formatDate(inst.startdato)),
				obj('Forventet sluttdato', formatDate(inst.forventetSluttdato)),
				obj('Sluttdato', formatDate(inst.sluttdato)),
			])
		})
		data.push(instObj)
	}
}

const getTredjelandsborgerStatus = (oppholdKriterier, udiStubKriterier) => {
	if (oppholdKriterier && oppholdKriterier.oppholdSammeVilkaar) {
		return 'Oppholdstillatelse eller opphold på samme vilkår'
	} else if (oppholdKriterier && oppholdKriterier.uavklart) {
		return 'Uavklart'
	} else if (udiStubKriterier.harOppholdsTillatelse === false) {
		return 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår'
	}
	return null
}

const getAliasListe = (udiStubKriterier) => {
	const aliaserListe = []
	if (udiStubKriterier.aliaser) {
		udiStubKriterier.aliaser.forEach((alias, i) => {
			if (alias.nyIdent === false) {
				aliaserListe.push(`#${i + 1} Navn\n`)
			} else {
				aliaserListe.push(`#${i + 1} ID-nummer - ${alias.identtype}\n`)
			}
		})
	}
	return aliaserListe
}

const mapUdiStub = (bestillingData, data) => {
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

		let currentTredjelandsborgereStatus = getTredjelandsborgerStatus(
			oppholdKriterier,
			udiStubKriterier,
		)

		const oppholdsrett = Boolean(currentOppholdsrettType)
		const tredjelandsborger = Boolean(currentTredjelandsborgereStatus) ? 'Tredjelandsborger' : null

		const aliaserListe = getAliasListe(udiStubKriterier)

		const udistub = {
			header: 'UDI',
			items: [
				obj('Oppholdsstatus', oppholdsrett ? 'EØS-eller EFTA-opphold' : tredjelandsborger),
				obj(
					'Type opphold',
					oppholdsrett && showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType),
				),
				obj('Status', currentTredjelandsborgereStatus),
				obj(
					'Oppholdstillatelse fra dato',
					formatDate(
						_.get(oppholdKriterier, `${currentOppholdsrettType}Periode.fra`) ||
							_.get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra'),
					),
				),
				obj(
					'Oppholdstillatelse til dato',
					formatDate(
						_.get(oppholdKriterier, `${currentOppholdsrettType}Periode.til`) ||
							_.get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til'),
					),
				),
				obj(
					'Effektueringsdato',
					formatDate(
						_.get(oppholdKriterier, `${currentOppholdsrettType}Effektuering`) ||
							_.get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering'),
					),
				),
				obj(
					'Grunnlag for opphold',
					oppholdsrett &&
						showLabel(currentOppholdsrettType, oppholdKriterier[currentOppholdsrettType]),
				),
				obj(
					'Type oppholdstillatelse',
					showLabel(
						'oppholdstillatelseType',
						_.get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdstillatelseType'),
					),
				),
				obj(
					'Vedtaksdato',
					formatDate(_.get(oppholdKriterier, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato')),
				),
				obj(
					'Avgjørelsesdato',
					formatDate(
						_.get(
							oppholdKriterier,
							'ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avgjorelsesDato',
						),
					),
				),
				obj(
					'Har arbeidsadgang',
					allCapsToCapitalized(arbeidsadgangKriterier && arbeidsadgangKriterier.harArbeidsAdgang),
				),
				obj(
					'Type arbeidsadgang',
					showLabel(
						'typeArbeidsadgang',
						arbeidsadgangKriterier && arbeidsadgangKriterier.typeArbeidsadgang,
					),
				),
				obj(
					'Arbeidsomfang',
					showLabel(
						'arbeidsOmfang',
						arbeidsadgangKriterier && arbeidsadgangKriterier.arbeidsOmfang,
					),
				),
				obj('Arbeidsadgang fra dato', formatDate(_.get(arbeidsadgangKriterier, 'periode.fra'))),
				obj('Arbeidsadgang til dato', formatDate(_.get(arbeidsadgangKriterier, 'periode.til'))),
				obj('Hjemmel', _.get(arbeidsadgangKriterier, 'hjemmel')),
				obj('Forklaring', _.get(arbeidsadgangKriterier, 'forklaring')),
				obj('Alias', aliaserListe?.length > 0 && aliaserListe),
				obj('Flyktningstatus', oversettBoolean(udiStubKriterier.flyktning)),
				obj(
					'Asylsøker',
					showLabel('jaNeiUavklart', udiStubKriterier.soeknadOmBeskyttelseUnderBehandling),
				),
			],
		}
		data.push(udistub)
	}
}

const mapPensjon = (bestillingData, data, navEnheter) => {
	const { tpOrdningData } = useTpOrdningKodeverk()
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
						oversettBoolean(pensjonKriterier.inntekt.redusertMedGrunnbelop),
					),
				],
			}

			data.push(pensjonforvalterPopp)
		}

		if (pensjonKriterier.pensjonsavtale && pensjonKriterier.pensjonsavtale?.length > 0) {
			const penPensjonsavtale = {
				header: 'Pensjonsavtale (PEN)',
				itemRows: [],
			}

			pensjonKriterier.pensjonsavtale?.forEach((pensjonsavtale, i) => {
				penPensjonsavtale.itemRows.push([
					{ numberHeader: `Pensjonsavtale ${i + 1}` },
					obj('Produktbetegnelse', pensjonsavtale.produktBetegnelse),
					obj('Avtalekategori', showLabel('avtaleKategori', pensjonsavtale.avtaleKategori)),
				])

				pensjonsavtale.utbetalingsperioder?.forEach((periode, j) => {
					penPensjonsavtale.itemRows.push([
						{ numberHeader: `Utbetalingsperiode ${j + 1}` },
						obj('Startalder År', periode.startAlderAar),
						obj('Startalder Måned', showLabel('maanedsvelger', periode.startAlderMaaned)),
						obj('Sluttalder År', periode.sluttAlderAar),
						obj('Sluttalder Måned', showLabel('maanedsvelger', periode.sluttAlderMaaned)),
						obj('Årlig Utbetaling', periode.aarligUtbetaling),
					])
				})
			})

			data.push(penPensjonsavtale)
		}

		if (pensjonKriterier.generertInntekt) {
			const generertPopp = {
				header: 'Generert pensjonsgivende inntekt (POPP)',
				items: [
					obj('Fra og med år', pensjonKriterier.generertInntekt.generer?.fomAar),
					obj('Til og med år', pensjonKriterier.generertInntekt.generer?.tomAar),
					obj('Gjennomsnitt G-verdi', pensjonKriterier.generertInntekt.generer.averageG),
					obj(
						'Tillat inntekt under 1G',
						oversettBoolean(pensjonKriterier.generertInntekt.generer.tillatInntektUnder1G),
					),
				],
			}

			data.push(generertPopp)
		}

		if (pensjonKriterier.tp && pensjonKriterier.tp?.length > 0) {
			const hentTpOrdningNavn = (tpnr) => {
				if (Options('tpOrdninger')?.length) {
					return Options('tpOrdninger').find((ordning) => ordning.value === tpnr)?.label
				}
				return tpnr
			}

			const pensjonforvalterTp = {
				header: 'Tjenestepensjon (TP)',
				itemRows: [],
			}

			pensjonKriterier.tp.forEach((ordning) => {
				const ordningNavn = hentTpOrdningNavn(ordning.ordning)

				if (ordning.ytelser?.length) {
					ordning.ytelser.forEach((ytelse) => {
						pensjonforvalterTp.itemRows.push([
							{ numberHeader: `${ordningNavn}` },
							obj('Ytelse', ytelse.type),
							obj('datoInnmeldtYtelseFom', formatDate(ytelse.datoInnmeldtYtelseFom)),
							obj('datoYtelseIverksattFom', formatDate(ytelse.datoYtelseIverksattFom)),
							obj('datoYtelseIverksattTom', formatDate(ytelse.datoYtelseIverksattTom)),
						])
					})
				} else {
					pensjonforvalterTp.itemRows.push([
						{ numberHeader: `${ordningNavn} bare forhold (uten ytelser)` },
					])
				}
			})

			data.push(pensjonforvalterTp)
		}

		if (pensjonKriterier.alderspensjon) {
			const ap = pensjonKriterier.alderspensjon

			const navEnhetLabel = navEnheter?.find(
				(enhet) => enhet.value === ap.navEnhetId?.toString(),
			)?.label

			const pensjonforvalterAlderspensjon = {
				header: 'Alderspensjon: ' + (ap.soknad ? 'Søknad' : 'Vedtak'),
				items: [
					obj('Iverksettelsesdato', formatDate(ap.iverksettelsesdato)),
					obj('Saksbehandler', ap.saksbehandler),
					obj('Attesterer', ap.attesterer),
					obj('Uttaksgrad', `${ap.uttaksgrad}%`),
					obj('NAV-kontor', navEnhetLabel || ap.navEnhetId),
					obj('Ektefelle/partners inntekt', ap.relasjoner?.[0]?.sumAvForvArbKapPenInntekt),
					obj('Inkluder AFP privat', oversettBoolean(ap.inkluderAfpPrivat)),
					obj('AFP privat resultat', showLabel('afpPrivatResultat', ap.afpPrivatResultat)),
				],
			}
			data.push(pensjonforvalterAlderspensjon)
		}

		if (pensjonKriterier.uforetrygd) {
			const uforetrygd = pensjonKriterier.uforetrygd

			const navEnhetLabel = navEnheter?.find(
				(enhet) => enhet.value === uforetrygd.navEnhetId?.toString(),
			)?.label

			const pensjonforvalterUforetrygd = {
				header: 'Uføretrygd',
				items: [
					obj('Ønsket virkningsdato', formatDate(uforetrygd.onsketVirkningsDato)),
					obj('Uføretidspunkt', formatDate(uforetrygd.uforetidspunkt)),
					obj('Inntekt før uførhet', uforetrygd.inntektForUforhet),
					obj('Inntekt etter uførhet', uforetrygd.inntektEtterUforhet),
					obj('Har barnetillegg', oversettBoolean(uforetrygd.barnetilleggDetaljer !== null)),
					obj(
						'Type barnetillegg',
						showLabel('barnetilleggType', uforetrygd.barnetilleggDetaljer?.barnetilleggType),
					),
					obj(
						'Antall forventede inntekter for søker',
						uforetrygd.barnetilleggDetaljer?.forventedeInntekterSoker?.length,
					),
					obj(
						'Antall forventede inntekter for partner',
						uforetrygd.barnetilleggDetaljer?.forventedeInntekterEP?.length,
					),
					obj(
						'Sats for minimum IFU',
						showLabel('minimumInntektForUforhetType', uforetrygd.minimumInntektForUforhetType),
					),
					obj('Uføregrad', uforetrygd.uforegrad ? `${uforetrygd.uforegrad}%` : null),
					obj('Saksbehandler', uforetrygd.saksbehandler),
					obj('Attesterer', uforetrygd.attesterer),
					obj('NAV-kontor', navEnhetLabel || uforetrygd.navEnhetId),
				],
			}
			data.push(pensjonforvalterUforetrygd)
		}

		if (pensjonKriterier?.afpOffentlig) {
			const afpOffentlig = pensjonKriterier.afpOffentlig

			const pensjonforvalterAfpOffentlig = {
				header: 'AFP Offentlig',
				items: [
					obj(
						'Direktekall',
						afpOffentlig.direktekall?.map((tpId) => showTpNavn(tpId, tpOrdningData))?.join(', '),
					),
				],
				itemRows: [],
			}

			afpOffentlig?.mocksvar?.forEach((mocksvar, i) => {
				pensjonforvalterAfpOffentlig.itemRows.push([
					{ numberHeader: `AFP offentlig ${i + 1}` },
					obj('TP-ordning', showTpNavn(mocksvar.tpId, tpOrdningData)),
					obj('Status AFP', showLabel('statusAfp', mocksvar.statusAfp)),
					obj('Virkningsdato', formatDate(mocksvar.virkningsDato)),
					obj('Sist benyttet G', mocksvar.sistBenyttetG),
					obj('Antall beløp', mocksvar.belopsListe?.length),
				])
			})

			data.push(pensjonforvalterAfpOffentlig)
		}
	}
}

const mapInntektsmelding = (bestillingData, data) => {
	const inntektsmeldingKriterier = bestillingData.inntektsmelding

	const mapInntektsmeldingKriterier = (meldinger) => ({
		header: 'Inntektsmelding (fra Altinn)',
		itemRows: meldinger.map((inntekt, i) => [
			{
				numberHeader: `Inntekt ${i + 1}`,
			},
			obj('Årsak til innsending', codeToNorskLabel(inntekt.aarsakTilInnsending)),
			obj('Ytelse', codeToNorskLabel(inntekt.ytelse)),
			obj('Nær relasjon', oversettBoolean(inntekt.naerRelasjon)),
			obj('Innsendingstidspunkt', formatDate(inntekt.avsendersystem.innsendingstidspunkt)),

			obj('Arbeidsgiver (orgnr)', inntekt.arbeidsgiver && inntekt.arbeidsgiver.virksomhetsnummer),
			obj(
				'Arbeidsgiver (fnr/dnr/npid)',
				inntekt.arbeidsgiverPrivat && inntekt.arbeidsgiverPrivat.arbeidsgiverFnr,
			),
			obj('Arbeidsforhold-ID', inntekt.arbeidsforhold.arbeidsforholdId),
			obj('Beløp', inntekt.arbeidsforhold.beregnetInntekt.beloep),
			obj('Årsak ved endring', codeToNorskLabel(inntekt.arbeidsforhold.aarsakVedEndring)),
			obj('Første fraværsdag', formatDate(inntekt.arbeidsforhold.foersteFravaersdag)),
			obj(
				'Avtalte ferier',
				inntekt.arbeidsforhold.avtaltFerieListe && inntekt.arbeidsforhold.avtaltFerieListe?.length,
			),
			//Refusjon
			obj('Refusjonsbeløp per måned', inntekt.refusjon.refusjonsbeloepPrMnd),
			obj('Opphørsdato refusjon', formatDate(inntekt.refusjon.refusjonsopphoersdato)),
			obj(
				'Endringer i refusjon',
				_.has(inntekt, 'refusjon.endringIRefusjonListe') &&
					inntekt.refusjon.endringIRefusjonListe?.length,
			),
			//Omsorg
			obj('Har utbetalt pliktige dager', _.get(inntekt, 'omsorgspenger.harUtbetaltPliktigeDager')),
			obj(
				'Fraværsperioder',
				_.has(inntekt, 'omsorgspenger.fravaersPerioder') &&
					inntekt.omsorgspenger.fravaersPerioder?.length,
			),
			obj(
				'Delvis fravær',
				_.has(inntekt, 'omsorgspenger.delvisFravaersListe') &&
					inntekt.omsorgspenger.delvisFravaersListe?.length,
			),
			//Sykepenger
			obj('Brutto utbetalt', _.get(inntekt, 'sykepengerIArbeidsgiverperioden.bruttoUtbetalt')),
			obj(
				'Begrunnelse for reduksjon eller ikke utbetalt',
				codeToNorskLabel(
					_.get(
						inntekt,
						'sykepengerIArbeidsgiverperioden.begrunnelseForReduksjonEllerIkkeUtbetalt',
					),
				),
			),
			obj(
				'Arbeidsgiverperioder',
				_.has(inntekt, 'sykepengerIArbeidsgiverperioden.arbeidsgiverperiodeListe') &&
					inntekt.sykepengerIArbeidsgiverperioden.arbeidsgiverperiodeListe?.length,
			),
			//Foreldrepenger
			obj('Startdato foreldrepenger', formatDate(inntekt.startdatoForeldrepengeperiode)),
			//Pleiepenger
			obj(
				'Pleiepengerperioder',
				inntekt.pleiepengerPerioder && inntekt.pleiepengerPerioder?.length,
			),
			//Naturalytelse
			obj(
				'Gjenopptagelse Naturalytelse',
				inntekt.gjenopptakelseNaturalytelseListe &&
					inntekt.gjenopptakelseNaturalytelseListe?.length,
			),
			obj(
				'Opphør av Naturalytelse',
				inntekt.opphoerAvNaturalytelseListe && inntekt.opphoerAvNaturalytelseListe?.length,
			),
		]),
	})

	const tomInntektsmelding = {
		header: 'Inntektsmelding (fra Altinn)',
		items: [obj('Inntektsmelding', 'Tom bestilling')],
	}

	if (inntektsmeldingKriterier) {
		if (_.isEmpty(inntektsmeldingKriterier.inntekter)) {
			data.push(tomInntektsmelding)
		} else data.push(mapInntektsmeldingKriterier(inntektsmeldingKriterier.inntekter))
	}
}

const mapSkattekort = (bestillingData, data) => {
	const skattekortKriterier = bestillingData.skattekort

	if (skattekortKriterier) {
		const skattekort = {
			header: 'Skattekort (SOKOS)',
			itemRows: [],
		}

		skattekortKriterier?.arbeidsgiverSkatt?.forEach((arbeidsgiver, idx) => {
			const arbeidstaker = arbeidsgiver?.arbeidstaker?.[0]
			const trekkListe = arbeidstaker?.skattekort?.forskuddstrekk

			const tilleggsopplysningFormatted = arbeidstaker?.tilleggsopplysning?.map(
				(tilleggsopplysning) => {
					return showKodeverkLabel('TILLEGGSOPPLYSNING', tilleggsopplysning)
				},
			)

			skattekort.itemRows.push([
				{ numberHeader: `Skattekort ${idx + 1}` },
				obj(
					'Resultat på forespørsel',
					showKodeverkLabel('RESULTATSTATUS', arbeidstaker?.resultatPaaForespoersel),
				),
				obj('Inntektsår', arbeidstaker?.inntektsaar),
				obj('Utstedt dato', formatDate(arbeidstaker?.skattekort?.utstedtDato)),
				obj('Skattekortidentifikator', arbeidstaker?.skattekort?.skattekortidentifikator),
				obj('Tilleggsopplysning', arrayToString(tilleggsopplysningFormatted)),
				obj('Arbeidsgiver (org.nr.)', arbeidsgiver?.arbeidsgiveridentifikator?.organisasjonsnummer),
				obj('Arbeidsgiver (ident)', arbeidsgiver?.arbeidsgiveridentifikator?.personidentifikator),
			])

			trekkListe?.forEach((item, idx) => {
				const forskuddstrekkType = Object.keys(item)?.filter((key) => item[key])?.[0]
				const forskuddstrekk = item[forskuddstrekkType]

				skattekort.itemRows.push([
					{ numberHeader: `Forskuddstrekk ${idx + 1}: ${toTitleCase(forskuddstrekkType)}` },
					obj('Trekkode', showKodeverkLabel('TREKKODE', forskuddstrekk?.trekkode)),
					obj('Frikortbeløp', forskuddstrekk?.frikortbeloep),
					obj('Tabelltype', showKodeverkLabel('TABELLTYPE', forskuddstrekk?.tabelltype)),
					obj('Tabellnummer', forskuddstrekk?.tabellnummer),
					obj('Prosentsats', forskuddstrekk?.prosentsats),
					obj('Antall måneder for trekk', forskuddstrekk?.antallMaanederForTrekk),
				])
			})
		})

		data.push(skattekort)
	}
}

const mapDokarkiv = (bestillingData, data) => {
	const dokarkivKriterier = bestillingData.dokarkiv

	if (dokarkivKriterier) {
		const dokarkiv = {
			header: 'Dokumenter (Joark)',
			itemRows: dokarkivKriterier?.map((dokument, i) => [
				{ numberHeader: `Dokument ${i + 1}` },
				obj('Brevkode', dokument.dokumenter[0].brevkode),
				obj('Tittel', dokument.tittel),
				obj('Avsender type', dokument.avsenderMottaker?.idType),
				obj('Avsender ID', dokument.avsenderMottaker?.id),
				obj('Avsender navn', dokument.avsenderMottaker?.navn),
				obj('Tema', dokument.tema),
				obj('Behandlingstema', dokument.behandlingstema),
				obj('Journalførende enhet', dokument.journalfoerendeEnhet),
				obj('Ferdigstill journalpost', oversettBoolean(dokument.ferdigstill)),
				obj('Sakstype', showLabel('sakstype', dokument.sak?.sakstype)),
				obj('Fagsaksystem', showLabel('fagsaksystem', dokument.sak?.fagsaksystem)),
				obj('Fagsak-ID', dokument.sak?.fagsakId),
				obj('Antall vedlegg', dokument.dokumenter?.length),
			]),
		}

		data.push(dokarkiv)
	}
}

const mapHistark = (bestillingData, data) => {
	const histarkKriterier = bestillingData.histark?.dokumenter

	if (histarkKriterier) {
		const histark = {
			header: 'Dokumenter (Histark)',
			itemRows: histarkKriterier?.map((dokument, i) => [
				{ numberHeader: `Dokument ${i + 1}` },
				obj('Temakoder', dokument?.temakoder && arrayToString(dokument?.temakoder)),
				obj('Enhetsnavn', dokument?.enhetsnavn),
				obj('Enhetsnummer', dokument?.enhetsnummer),
				obj('Startår', dokument?.startYear),
				obj('Sluttår', dokument?.endYear),
				obj(
					'Skanningstidspunkt',
					dokument?.skanningstidspunkt && formatDate(dokument?.skanningstidspunkt),
				),
				obj('Skanner', dokument?.skanner),
				obj('Skannested', dokument?.skannested),
				obj('Filnavn', dokument?.tittel),
			]),
		}

		data.push(histark)
	}
}

const mapOrganisasjon = (bestillingData, data) => {
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
				obj('Stiftelsesdato', formatDate(organisasjonKriterier[0].stiftelsesdato)),
				obj('Målform', showLabel('maalform', organisasjonKriterier[0].maalform)),
				obj('Telefon', organisasjonKriterier[0].telefon),
				obj('E-postadresse', organisasjonKriterier[0].epost),
				obj('Internettadresse', organisasjonKriterier[0].nettside),
			],
		}

		data.push(organisasjon)

		if (forretningsadresse) {
			const forretningsadresseKriterier = {
				header: 'Forretningsadresse',
				items: [
					obj('Land', forretningsadresse.landkode),
					obj('Postnummer', forretningsadresse.postnr),
					obj('Poststed', forretningsadresse.poststed),
					obj('Kommunenummer', forretningsadresse.kommunenr),
					obj('Adresselinje 1', forretningsadresse.adresselinjer[0]),
					obj('Adresselinje 2', forretningsadresse.adresselinjer[1]),
					obj('Adresselinje 3', forretningsadresse.adresselinjer[2]),
				],
			}
			data.push(forretningsadresseKriterier)
		}
		if (postadresse) {
			const postadresseKriterier = {
				header: 'Postadresse',
				items: [
					obj('Land', postadresse.landkode),
					obj('Postnummer', postadresse.postnr),
					obj('Poststed', postadresse.poststed),
					obj('Kommunenummer', postadresse.kommunenr),
					obj('Adresselinje 1', postadresse.adresselinjer[0]),
					obj('Adresselinje 2', postadresse.adresselinjer[1]),
					obj('Adresselinje 3', postadresse.adresselinjer[2]),
				],
			}
			data.push(postadresseKriterier)
		}
	}
}

export function mapBestillingData(bestillingData, bestillingsinformasjon, firstIdent) {
	const bestilling = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { navEnheter } = useNavEnheter()

	if (!bestillingData) {
		return null
	}

	const data: any[] = []
	const identtype = bestillingData.pdldata?.opprettNyPerson?.identtype

	mapBestillingsinformasjon(bestillingsinformasjon, data, identtype, firstIdent)
	mapPdlNyPerson(bestillingData, data, bestilling)

	const pdldataKriterier = bestillingData.pdldata?.person
	if (pdldataKriterier) {
		const {
			foedsel,
			foedested,
			foedselsdato,
			kjoenn,
			navn,
			telefonnummer,
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
			deltBosted,
		} = pdldataKriterier

		mapFoedsel(foedsel, data)
		mapFoedested(foedested, data)
		mapFoedselsdato(foedselsdato, data)
		mapInnflytting(innflytting, data)
		mapUtflytting(utflytting, data)
		mapKjoenn(kjoenn, data)
		mapNavn(navn, data)
		mapTelefonnummer(telefonnummer, data)
		mapVergemaal(vergemaal, data)
		mapTilrettelagtKommunikasjon(tilrettelagtKommunikasjon, data)
		mapStatsborgerskap(statsborgerskap, data)
		mapDoedsfall(doedsfall, data)
		mapBostedsadresse(bostedsadresse, data)
		mapOppholdsadresse(oppholdsadresse, data)
		mapKontaktadresse(kontaktadresse, data)
		mapDeltBosted(deltBosted, data)
		mapAdressebeskyttelse(adressebeskyttelse, data)
		mapSikkerhetstiltak(sikkerhetstiltak, data)
		mapSivilstand(sivilstand, data)
		mapForelderBarnRelasjon(forelderBarnRelasjon, data)
		mapForeldreansvar(foreldreansvar, data)
		mapDoedfoedtBarn(doedfoedtBarn, data)
		mapFalskIdentitet(falskIdentitet, data)
		mapUtenlandskIdentifikasjonsnummer(utenlandskIdentifikasjonsnummer, data)
		mapNyIdent(nyident, data)
		mapKontaktinformasjonForDoedsbo(kontaktinformasjonForDoedsbo, data)
	}

	mapFullmakt(bestillingData, data)
	mapNomData(bestillingData, data)
	mapSkjermingData(bestillingData, data)
	mapBankkonto(bestillingData, data)
	mapAareg(bestillingData, data)
	mapSigrunstubPensjonsgivende(bestillingData, data)
	mapSigrunstubSummertSkattegrunnlag(bestillingData, data)
	mapInntektStub(bestillingData, data)
	mapInntektsmelding(bestillingData, data)
	mapSkattekort(bestillingData, data)
	mapArbeidssoekerregisteret(bestillingData, data)
	mapArbeidsplassenCV(bestillingData, data)
	mapPensjon(bestillingData, data, navEnheter)
	mapArena(bestillingData, data)
	mapSykemelding(bestillingData, data)
	mapYrkesskader(bestillingData, data)
	mapBrregstub(bestillingData, data)
	mapInst(bestillingData, data)
	mapKrr(bestillingData, data)
	mapMedlemskapsperiode(bestillingData, data)
	mapUdiStub(bestillingData, data)
	mapDokarkiv(bestillingData, data)
	mapHistark(bestillingData, data)
	mapOrganisasjon(bestillingData, data)

	return data
}
