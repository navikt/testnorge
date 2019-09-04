import { relasjonTranslator } from './Utils'
import Formatters from '~/utils/DataFormatter'

export function mapTpsfData(tpsfData, testIdent, pdlfData) {
	console.log('tpsfData :', tpsfData)
	if (!tpsfData) return null
	let data
	data = [
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
					value: tpsfData.personStatus
				},
				{
					id: 'erForsvunnet',
					label: 'erForsvunnet',
					value: tpsfData.erForsvunnet
				},
				{
					id: 'forsvunnetDato',
					label: 'Savnet Siden',
					value: Formatters.formatDate(tpsfData.forsvunnetDato)
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
		}
	]

	if (tpsfData.statsborgerskap) {
		data.push({
			header: 'Nasjonalitet',
			data: [
				{
					id: 'innvandretFra',
					label: 'Innvandret fra',
					value: tpsfData.innvandretFra
				},
				{
					id: 'statsborgerskap',
					label: 'Statsborgerskap',
					value: tpsfData.statsborgerskap
				},
				{
					id: 'sprakKode',
					label: 'Språk',
					value: tpsfData.sprakKode
				}
			]
		})
	}

	if (pdlfData && pdlfData.utenlandskeIdentifikasjonsnummere) {
		var opphoert = false
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
		data.push({
			header: 'Familierelasjoner',
			multiple: true,
			data: tpsfData.relasjoner.map(relasjon => {
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
							value: relasjon.personRelasjonMed.personStatus
						},
						{
							id: 'statsborgerskap',
							label: 'Statsborgerskap',
							value: relasjon.personRelasjonMed.statsborgerskap
						},
						{
							id: 'sprakKode',
							label: 'Språk',
							value: relasjon.personRelasjonMed.sprakKode
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
