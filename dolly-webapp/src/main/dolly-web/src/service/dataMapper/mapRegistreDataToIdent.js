import Formatters from '~/utils/DataFormatter'
import _get from 'lodash/get'

export function mapSigrunData(sigrunData) {
	if (!sigrunData || sigrunData.length === 0) return null
	return {
		header: 'Inntekter',
		multiple: true,
		data: sigrunData.map((data, i) => {
			return {
				parent: 'inntekter',
				id: data.personidentifikator,
				value: [
					{
						id: 'id',
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						id: 'aar',
						label: 'År',
						value: data.inntektsaar
					},
					{
						id: 'verdi',
						label: 'Beløp',
						value: data.verdi
					},
					,
					{
						id: 'tjeneste',
						label: 'Tjeneste',
						width: 'medium',
						value: data.tjeneste
					},

					{
						id: 'grunnlag',
						label: 'Grunnlag',
						width: 'xlarge',
						value: Formatters.camelCaseToLabel(data.grunnlag)
					}
				]
			}
		})
	}
}

export function mapKrrData(krrData) {
	if (!krrData) return null
	return {
		header: 'Kontaktinformasjon og reservasjon',
		data: [
			{
				id: 'mobil',
				label: 'Mobilnummer',
				value: krrData.mobil
			},
			{
				id: 'epost',
				label: 'Epost',
				value: krrData.epost
			},
			{
				id: 'reservert',
				label: 'Reservert mot digitalkommunikasjon',
				value: krrData.reservert ? 'JA' : 'NEI'
			}
		]
	}
}

export function mapArenaData(arenaData, kvalifiseringsgruppe, inaktiveringDato, aap115, aap) {
	if (!arenaData) return null
	if (arenaData['data']['arbeidsokerList'].length === 0) return null
	const brukertype = arenaData['data']['arbeidsokerList'][0].servicebehov
	return {
		header: 'Arena',
		data: [
			{
				id: 'brukertype',
				label: 'Brukertype',
				value: Formatters.booleanToServicebehov(brukertype)
			},
			{
				id: 'servicebehov',
				label: 'Servicebehov',
				value:
					kvalifiseringsgruppe && Formatters.servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe)
			},
			{
				id: 'inaktiveringDato',
				label: 'Inaktiv fra dato',
				value: inaktiveringDato && Formatters.formatDate(inaktiveringDato)
			},
			{
				id: 'aap115',
				label: 'Har 11-5 vedtak',
				value: aap115 && Formatters.oversettBoolean(true)
			},
			{
				id: 'aap115_fraDato',
				label: 'Fra dato',
				value: aap115 && aap115[0].fraDato && Formatters.formatDate(aap115[0].fraDato)
			},
			{
				id: 'aap',
				label: 'Har AAP vedtak UA - positivt utfall',
				value: aap && Formatters.oversettBoolean(true)
			},
			{
				id: 'aap_fraDato',
				label: 'Fra dato',
				value: aap && aap[0].fraDato && Formatters.formatDate(aap[0].fraDato)
			},
			{
				id: 'aap_tilDato',
				label: 'Til dato',
				value: aap && aap[0].tilDato && Formatters.formatDate(aap[0].tilDato)
			}
		]
	}
}

export function mapSubItemAaregData(data) {
	let subItemArray = []
	data.utenlandsopphold &&
		subItemArray.push({
			id: 'utenlandsopphold',
			label: 'Utenlandsopphold',
			subItem: true,
			value: data.utenlandsopphold.map((subdata, k) => {
				return [
					{
						id: 'id',
						label: '',
						value: `#${k + 1}`,
						width: 'x-small'
					},
					{
						id: 'land',
						label: 'Land',
						value: subdata.landkode
					},
					{
						id: 'fom',
						label: 'Startdato',
						value: subdata.periode.fom
					},
					{
						id: 'tom',
						label: 'Sluttdato',
						value: subdata.periode.tom
					}
				]
			})
		})

	data.permisjonPermitteringer &&
		subItemArray.push({
			id: 'permisjon',
			label: 'Permisjon',
			subItem: true,
			value: data.permisjonPermitteringer.map((subdata, k) => {
				return [
					{
						id: 'id',
						label: '',
						value: `#${k + 1}`,
						width: 'x-small'
					},
					{
						id: 'permisjonOgPermittering',
						label: 'Permisjonstype',
						value: subdata.type,
						width: 'medium'
					},
					{
						id: 'fom',
						label: 'Startdato',
						value: subdata.periode.fom
					},
					{
						id: 'tom',
						label: 'Sluttdato',
						value: subdata.periode.tom
					}
				]
			})
		})
	return subItemArray
}

export function mapAaregData(aaregData) {
	if (!aaregData) return null

	return {
		header: 'Arbeidsforhold',
		multiple: true,
		data: aaregData.map((data, i) => {
			return {
				parent: 'arbeidsforhold',
				id: data.arbeidsforholdId,
				label: 'Arbeidsforhold',
				value: [
					{
						id: 'id',
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						id: 'yrke',
						label: 'Yrke',
						value: data.arbeidsavtaler[0].yrke,
						apiKodeverkId: 'Yrker'
					},
					{
						id: 'startdato',
						label: 'Startdato',
						value: data.ansettelsesperiode.periode.fom
					},
					{
						id: 'sluttdato',
						label: 'Sluttdato',
						value: data.ansettelsesperiode.periode.tom
					},
					{
						id: 'stillingprosent',
						label: 'Stillingprosent',
						value: data.arbeidsavtaler[0].stillingsprosent
					},
					{
						id: 'typearbeidsgiver',
						label: 'Type av arbeidsgiver',
						value: data.arbeidsgiver.type
					},

					{
						id: 'orgnr',
						label: 'Orgnummer',
						value: data.arbeidsgiver.organisasjonsnummer
					},
					{
						id: 'orgnr',
						label: 'Arbeidsgiver Ident',
						value: data.arbeidsgiver.offentligIdent
					}
				].concat(mapSubItemAaregData(data))
			}
		})
	}
}

export function mapInstData(instData) {
	if (!instData || instData.length === 0) return null
	return {
		header: 'Institusjonsopphold',
		multiple: true,
		data: instData.map((data, i) => {
			return {
				parent: 'institusjonsopphold',
				id: data.personidentifikator,
				value: [
					{
						id: 'id',
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						id: 'institusjonstype',
						label: 'Institusjonstype',
						value: Formatters.showLabel('institusjonstype', data.institusjonstype)
					},
					{
						id: 'varighet',
						label: 'Varighet',
						value: data.varighet && Formatters.showLabel('varighet', data.varighet)
					},
					{
						id: 'startdato',
						label: 'Startdato',
						value: Formatters.formateStringDates(data.startdato)
					},
					{
						id: 'faktiskSluttdato',
						label: 'Sluttdato',
						value: Formatters.formateStringDates(data.faktiskSluttdato)
					}
				]
			}
		})
	}
}

export function mapUdiData(udiData) {
	if (!udiData) return null
	//! Begynte på denne for lenge siden, men tror ikke det er så mye som fortsatt er brukbart.
	console.log('udiData :', udiData)
	const oppholdsrettTyper = [
		'eosEllerEFTABeslutningOmOppholdsrett',
		'eosEllerEFTAVedtakOmVarigOppholdsrett',
		'eosEllerEFTAOppholdstillatelse',
		'oppholdSammeVilkaar'
	]
	const currentOppholdsrettType = oppholdsrettTyper.find(type => udiData.oppholdStatus[type])

	const oppholdsrett = Boolean(currentOppholdsrettType)

	return {
		header: 'UDI',
		data: [
			// {
			// 	id: 'oppholdStatus',
			// 	label: 'Gjeldende oppholdsstatus',
			// 	value: [
			{
				id: 'oppholdsstatus',
				label: 'Oppholdsstatus',
				value: oppholdsrett
					? 'EØS- eller EFTA-opphold'
					: udiData.oppholdSammeVilkaar
						? 'Tredjelandsborger'
						: null // EØS/EFTA eller tredjelandsborger
				// Sjekk hvilke felter som er utfylt? Kommer an på hva som fylles ut default.
			},
			{
				id: 'status',
				label: 'Status', //Status for tredjelandsborgere - Oppholdstillatelse, ikke opphold og uavklart
				value: false //udiData.oppholdStatus
			},
			{
				id: 'typeOpphold',
				label: 'Type opphold',
				value: Formatters.showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)
			},
			{
				id: 'oppholdFraDato',
				label: 'Oppholdstillatelse fra dato', //Denne skal egenltig ha dato fra tredjelandsborgere også
				value: Formatters.formateStringDates(
					_get(udiData.oppholdStatus, `${currentOppholdsrettType}Periode.fra`)
				)
			},
			{
				id: 'oppholdTilDato',
				label: 'Oppholdstillatelse til dato', //Denne skal egenltig ha dato fra tredjelandsborgere også
				value: Formatters.formateStringDates(
					_get(udiData.oppholdStatus, `${currentOppholdsrettType}Periode.til`)
				)
			},
			{
				id: 'effektueringsdato',
				label: 'Effektueringsdato', //Denne skal egenltig ha dato fra tredjelandsborgere også
				value: Formatters.formateStringDates(
					_get(udiData.oppholdStatus, `${currentOppholdsrettType}Effektuering`)
				)
			},
			{
				id: 'vedtaksdato',
				label: 'Vedtaksdato', //tredjelandsborger
				value: false //udiData.oppholdStatus
			},
			{
				id: 'grunnlagForOpphold',
				label: 'Grunnlag for opphold',
				value: Formatters.showLabel(
					'eosEllerEFTABeslutningOmOppholdsrett',
					udiData.oppholdStatus[currentOppholdsrettType]
				)
			},
			{
				id: 'uavklart',
				label: 'Uavklart',
				value: udiData.uavklart && Formatters.oversettBoolean(true) //Kan kanskje heller vise denne under status
			},
			// {
			// 	id: 'ikkeOppholdGrunn',
			// 	label: 'Grunn'
			// },
			// 	]
			// },
			// {
			// 	id: 'arbeidsadgang',
			// 	label: 'Arbeidsadgang',
			// 	value: [
			{
				id: 'harArbeidsadgang',
				label: 'Har arbeidsadgang',
				value: udiData.arbeidsadgang.harArbeidsAdgang
			},
			{
				id: 'typeArbeidsadgang',
				label: 'Type arbeidsadgang',
				value: Formatters.showLabel('typeArbeidsadgang', udiData.arbeidsadgang.typeArbeidsAdgang)
			},
			{
				id: 'arbeidsOmfang',
				label: 'Arbeidsomfang',
				value: Formatters.showLabel('arbeidsOmfang', udiData.arbeidsadgang.arbeidsOmfang)
			},
			{
				id: 'arbeidsadgangFraDato',
				label: 'Arbeidsadgang fra dato',
				value: Formatters.formateStringDates(udiData.arbeidsadgang.periode.fra)
			},
			{
				id: 'arbeidsadgangTilDato',
				label: 'Arbeidsadgang til dato',
				value: Formatters.formateStringDates(udiData.arbeidsadgang.periode.til)
			}
			// 	]
			// },
		] //.concat(mapAliasData(udiData.aliaser))
	}
}

const mapAliasData = aliasdata => {
	if (!aliasdata || aliasdata.length === 0) return null
	return {
		header: 'Alias',
		multiple: true,
		data: aliasdata.map((data, i) => {
			return {
				parent: 'aliaser',
				id: i,
				value: [
					{
						id: 'id',
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						id: 'fnr',
						label: 'Fnr/dnr',
						value: data.fnr
					},
					{
						id: 'navn',
						label: 'Navn',
						value: data.navn
					}
				]
			}
		})
	}
}
