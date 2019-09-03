import { relasjonTranslator } from './Utils'
import Formatters from '~/utils/DataFormatter'
import _get from 'lodash/get'

export function mapTpsfData(tpsfData, testIdent, tpsfKriterier, pdlfData) {
	if (!tpsfData) return null

	const data = [
		{
			header: 'Personlig informasjon',
			data: [
				{
					id: 'ident',
					label: tpsfData.identtype,
					value: tpsfData.ident
				},
				{
					id: 'fornavn',
					label: 'Fornavn',
					value: tpsfData.fornavn
				},
				{
					id: 'mellomnavn',
					label: 'Mellomnavn',
					value: tpsfData.mellomnavn
				},
				{
					id: 'etternavn',
					label: 'Etternavn',
					value: tpsfData.etternavn
				},
				{
					id: 'kjonn',
					label: 'Kjønn',
					value: tpsfData.kjonn
				},
				{
					id: 'alder',
					label: 'Alder',
					value: Formatters.formatAlder(tpsfData.alder, tpsfData.doedsdato)
				},
				{
					id: 'personStatus',
					label: 'Personstatus',
					value: tpsfData.personStatus,
					apiKodeverkId: tpsfData.personStatus && 'Personstatuser'
				},
				{
					id: 'sivilstand',
					label: 'Sivilstand',
					value: tpsfData.sivilstand
				},
				{
					id: 'miljoer',
					label: 'Miljøer',
					value: Formatters.commaToSpace(testIdent.tpsfSuccessEnv)
				},
				{
					id: 'spesreg',
					label: 'Diskresjonskoder',
					value: tpsfData.spesreg
				},
				{
					id: 'utenFastBopel',
					label: 'Uten fast bopel',
					value: tpsfData.utenFastBopel && Formatters.oversettBoolean(tpsfData.utenFastBopel)
				},
				{
					id: 'gtVerdi',
					label: 'Geo. Tilhør',
					value: tpsfData.gtVerdi,
					extraLabel: Formatters.gtTypeLabel(tpsfData.gtType),
					apiKodeverkId: Formatters.gtApiKodeverkId(tpsfData.gtType)
				},
				{
					id: 'tknr',
					label: 'TK nummer',
					tknr: tpsfData.tknr
				},
				{
					id: 'egenAnsattDatoFom',
					label: 'Egenansatt',
					value: tpsfData.egenAnsattDatoFom && 'JA'
				}
			]
		}
	]

	if (tpsfData.statsborgerskap) {
		data.push({
			header: 'Nasjonalitet',
			data: [
				{
					id: 'statsborgerskap',
					label: 'Statsborgerskap',
					value: tpsfData.statsborgerskap
				},
				{
					id: 'sprakKode',
					label: 'Språk',
					value: tpsfData.sprakKode
				},
				{
					id: 'innvandretFraLand',
					label: 'Innvandret fra land',
					value: tpsfKriterier.innvandretFraLand && tpsfData.innvandretFraLand,
					apiKodeverkId: tpsfData.innvandretFraLand && 'StatsborgerskapFreg'
				},
				{
					id: 'innvandretFraLandFlyttedato',
					label: 'Innvandret dato',
					value:
						tpsfKriterier.innvandretFraLand &&
						Formatters.formatDate(tpsfData.innvandretFraLandFlyttedato)
				},
				{
					id: 'utvandretTilLand',
					label: 'Utvandret til land',
					value: tpsfData.utvandretTilLand,
					apiKodeverkId: tpsfData.utvandretTilLand && 'StatsborgerskapFreg'
				},
				{
					id: 'utvandretTilLandFlyttedato',
					label: 'Utvandret dato',
					value: Formatters.formatDate(tpsfData.utvandretTilLandFlyttedato)
				}
			]
		})
	}

	if (pdlfData && pdlfData.utenlandskeIdentifikasjonsnummere) {
		let opphoert = false
		if (pdlfData.utenlandskeIdentifikasjonsnummere[0].registrertOpphoertINAV) {
			opphoert = true
		}
		data.push({
			header: 'Utenlands-ID',
			data: [
				{
					id: 'idNummer',
					label: 'Identifikasjonsnummer',
					value: pdlfData.utenlandskeIdentifikasjonsnummere[0].idNummer
				},
				{
					id: 'kilde',
					label: 'Kilde',
					value: pdlfData.utenlandskeIdentifikasjonsnummere[0].kilde
				},
				{
					id: 'opphoert',
					label: 'Opphørt',
					value: Formatters.oversettBoolean(opphoert)
				},
				{
					id: 'utstederland',
					label: 'Utstederland',
					value: pdlfData.utenlandskeIdentifikasjonsnummere[0].utstederland,
					apiKodeverkId: 'StatsborgerskapFreg'
				}
			]
		})
	}

	if (tpsfData.boadresse) {
		data.push({
			header: 'Bostedadresse',
			data: [
				{
					parent: 'boadresse',
					id: 'adressetype',
					label: 'Adressetype',
					value: Formatters.adressetypeToString(tpsfData.boadresse.adressetype)
				},
				{
					parent: 'boadresse',
					id: 'gateadresse',
					label: 'Gatenavn',
					value: tpsfData.boadresse.gateadresse
				},
				{
					parent: 'boadresse',
					id: 'husnummer',
					label: 'Husnummer',
					value: tpsfData.boadresse.husnummer
				},
				{
					parent: 'boadresse',
					id: 'mellomnavn',
					label: 'Stedsnavn',
					value: tpsfData.boadresse.mellomnavn
				},
				{
					parent: 'boadresse',
					id: 'gardsnr',
					label: 'Gårdsnummer',
					value: tpsfData.boadresse.gardsnr
				},
				{
					parent: 'boadresse',
					id: 'bruksnr',
					label: 'Bruksnummer',
					value: tpsfData.boadresse.bruksnr
				},
				{
					parent: 'boadresse',
					id: 'festenr',
					label: 'Festenummer',
					value: tpsfData.boadresse.festenr
				},
				{
					parent: 'boadresse',
					id: 'undernr',
					label: 'Undernummer',
					value: tpsfData.boadresse.undernr
				},
				{
					parent: 'boadresse',
					id: 'postnr',
					label: 'Postnummer',
					extraLabel: tpsfData.boadresse.postnr,
					apiKodeverkId: 'Postnummer',
					value: tpsfData.boadresse.postnr
				},
				{
					parent: 'boadresse',
					id: 'flyttedato',
					label: 'Flyttedato',
					value: Formatters.formatDate(tpsfData.boadresse.flyttedato)
				}
			]
		})
	}

	if (tpsfData.postadresse) {
		data.push({
			header: 'Postadresse',
			data: [
				{
					parent: 'postadresse',
					id: 'postLinje1',
					label: 'Adresselinje 1',
					value: tpsfData.postadresse[0].postLinje1
				},
				{
					parent: 'postadresse',
					id: 'postLinje2',
					label: 'Adresselinje 2',
					value: tpsfData.postadresse[0].postLinje2
				},
				{
					parent: 'postadresse',
					id: 'postLinje3',
					label: 'Adresselinje 3',
					value: tpsfData.postadresse[0].postLinje3
				},
				{
					parent: 'postadresse',
					id: 'postLand',
					label: 'Land',
					value: tpsfData.postadresse[0].postLand
				}
			]
		})
	}

	if (tpsfData.relasjoner && tpsfData.relasjoner.length) {
		let numberOfChildren = 0
		data.push({
			header: 'Familierelasjoner',
			multiple: true,
			data: tpsfData.relasjoner.map(relasjon => {
				const relasjonstype = relasjonTranslator(relasjon.relasjonTypeNavn)
				relasjonstype === 'Barn' && (numberOfChildren += 1)
				return {
					parent: 'relasjoner',
					id: relasjon.id,
					label: relasjonstype,
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
							value: relasjon.personRelasjonMed.kjonn
						},
						{
							id: 'alder',
							label: 'Alder',
							value: Formatters.formatAlder(
								relasjon.personRelasjonMed.alder,
								relasjon.personRelasjonMed.doedsdato
							)
						},
						{
							id: 'personStatus',
							label: 'Personstatus',
							value: relasjon.personRelasjonMed.personStatus,
							apiKodeverkId: relasjon.personRelasjonMed.personStatus && 'Personstatuser'
						},
						{
							id: 'statsborgerskap',
							label: 'Statsborgerskap',
							value: relasjon.personRelasjonMed.statsborgerskap
						},
						{
							id: 'innvandretFraLand',
							label: 'Innvandret fra land',
							value:
								relasjonstype === 'Barn' &&
								tpsfKriterier.relasjoner.barn[numberOfChildren - 1].innvandretFraLand
									? relasjon.personRelasjonMed.innvandretFraLand
									: relasjonstype === 'Partner' &&
									  tpsfKriterier.relasjoner.partner.innvandretFraLand
										? relasjon.personRelasjonMed.innvandretFraLand
										: null,
							apiKodeverkId: 'StatsborgerskapFreg'
						},
						{
							id: 'innvandretFraLandFlyttedato',
							label: 'Innvandret dato',
							value:
								relasjonstype === 'Barn' &&
								tpsfKriterier.relasjoner.barn[numberOfChildren - 1].innvandretFraLand
									? Formatters.formatDate(relasjon.personRelasjonMed.innvandretFraLandFlyttedato)
									: relasjonstype === 'Partner' &&
									  tpsfKriterier.relasjoner.partner.innvandretFraLand
										? Formatters.formatDate(relasjon.personRelasjonMed.innvandretFraLandFlyttedato)
										: null
						},
						{
							id: 'utvandretTilLand',
							label: 'Utvandret til land',
							value: relasjon.personRelasjonMed.utvandretTilLand,
							apiKodeverkId: 'StatsborgerskapFreg'
						},
						{
							id: 'utvandretTilLandFlyttedato',
							label: 'Utvandret dato',
							value: Formatters.formatDate(relasjon.personRelasjonMed.utvandretTilLandFlyttedato)
						},
						{
							id: 'sprakKode',
							label: 'Språk',
							value: relasjon.personRelasjonMed.sprakKode
						},
						{
							id: 'sivilstand',
							label: 'Sivilstand',
							value: relasjon.personRelasjonMed.sivilstand
						},
						{
							id: 'spesreg',
							label: 'Diskresjonskoder',
							value: relasjon.personRelasjonMed.spesreg
						},
						{
							id: 'egenAnsattDatoFom',
							label: 'Egenansatt',
							value: relasjon.personRelasjonMed.egenAnsattDatoFom && 'JA'
						}
					]
				}
			})
		})
	}
	return data
}
