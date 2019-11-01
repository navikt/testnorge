import { relasjonTranslator } from './Utils'
import Formatters from '~/utils/DataFormatter'
import _get from 'lodash/get'

export function mapTpsfData(tpsfData, tpsfKriterier) {
	if (!tpsfData) return null

	const data = [
		{
			header: 'Persondetaljer',
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
					id: 'ForsvunnetDato',
					label: 'Savnet siden',
					value: Formatters.formatDate(tpsfData.forsvunnetDato)
				},
				{
					id: 'sivilstand',
					label: 'Sivilstand',
					value: tpsfData.sivilstand
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
					tknr: tpsfData.tknavn ? `${tpsfData.tknr} - ${tpsfData.tknavn}` : tpsfData.tknr
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
					value: tpsfData.statsborgerskap,
					apiKodeverkId: 'Landkoder'
				},
				{
					id: 'statsborgerskapRegdato',
					label: 'Statsborgerskap fra',
					value: Formatters.formatDate(tpsfData.statsborgerskapRegdato)
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
					apiKodeverkId: tpsfData.innvandretFraLand && 'Landkoder'
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
					apiKodeverkId: tpsfData.utvandretTilLand && 'Landkoder'
				},
				{
					id: 'utvandretTilLandFlyttedato',
					label: 'Utvandret dato',
					value: Formatters.formatDate(tpsfData.utvandretTilLandFlyttedato)
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
	if (tpsfData.identHistorikk) {
		data.push({
			header: 'Identhistorikk',
			multiple: true,
			data: tpsfData.identHistorikk.map((data, i) => {
				return {
					parent: 'identhistorikk',
					id: data.id,
					value: [
						{
							id: 'id',
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						{
							id: 'identtype',
							label: 'Identtype',
							value: data.aliasPerson.identtype
						},
						{
							id: 'fnrdnr',
							label: data.aliasPerson.identtype,
							value: data.aliasPerson.ident
						},
						{
							id: 'kjonn',
							label: 'Kjønn',
							value: data.aliasPerson.kjonn
						},
						{
							id: 'regdato',
							label: 'Utgått dato',
							value: Formatters.formatDate(data.regdato)
						}
					]
				}
			})
		})
	}
	if (tpsfData.relasjoner && tpsfData.relasjoner.length) {
		let numberOfChildren = 0
		data.push({
			header: 'Familierelasjoner',
			multiple: true,
			data: tpsfData.relasjoner.map(relasjon => {
				const relasjonstype = relasjonTranslator(relasjon.relasjonTypeNavn)
				if (relasjonstype === 'Barn') numberOfChildren += 1
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
							id: 'ForsvunnetDato',
							label: 'Savnet siden',
							value: Formatters.formatDate(relasjon.personRelasjonMed.forsvunnetDato)
						},
						{
							id: 'statsborgerskap',
							label: 'statsborgerskap',
							value:
								relasjonstype === 'Barn' &&
								tpsfKriterier.relasjoner.barn[numberOfChildren - 1].statsborgerskap
									? relasjon.personRelasjonMed.statsborgerskap
									: relasjonstype === 'Partner' && tpsfKriterier.relasjoner.partner.statsborgerskap
									? relasjon.personRelasjonMed.statsborgerskap
									: null,
							apiKodeverkId: 'Landkoder'
						},
						{
							id: 'ssatsborgerskapRegdato',
							label: 'Statsborgerskap fra',
							value:
								relasjonstype === 'Barn' &&
								tpsfKriterier.relasjoner.barn[numberOfChildren - 1].statsborgerskap
									? Formatters.formatDate(relasjon.personRelasjonMed.statsborgerskapRegdato)
									: relasjonstype === 'Partner' && tpsfKriterier.relasjoner.partner.statsborgerskap
									? Formatters.formatDate(relasjon.personRelasjonMed.statsborgerskapRegdato)
									: null
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
							apiKodeverkId: 'Landkoder'
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
							apiKodeverkId: 'Landkoder'
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
					].concat(mapIdenthistorikkData(relasjon.personRelasjonMed.identHistorikk))
				}
			})
		})
	}

	return data
}

export function mapIdenthistorikkData(data) {
	if (!data || data.length < 1) return []
	return {
		id: 'identhistorikk',
		label: 'Identhistorikk',
		subItem: true,
		value: data.map((subdata, i) => {
			return [
				{
					id: 'id',
					label: '',
					value: `#${i + 1}`,
					width: 'x-small'
				},
				{
					id: 'identtype',
					label: 'Identtype',
					value: subdata.aliasPerson.identtype
				},
				{
					id: 'fnrdnr',
					label: subdata.aliasPerson.identtype,
					value: subdata.aliasPerson.ident
				},
				{
					id: 'kjonn',
					label: 'Kjønn',
					value: Formatters.kjonnToString(subdata.aliasPerson.kjonn)
				},
				{
					id: 'regdato',
					label: 'Utgått dato',
					value: Formatters.formatDate(subdata.regdato)
				}
			]
		})
	}
}
