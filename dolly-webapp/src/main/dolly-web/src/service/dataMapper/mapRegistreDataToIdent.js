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
						value: Formatters.formatStringDates(data.startdato)
					},
					{
						id: 'faktiskSluttdato',
						label: 'Sluttdato',
						value: Formatters.formatStringDates(data.faktiskSluttdato)
					}
				]
			}
		})
	}
}

export function mapUdiData(udiData, asylsøker) {
	if (!udiData) return null
	if (
		udiData.arbeidsadgang == null &&
		udiData.flyktning == null &&
		udiData.harOppholdsTillatelse == null &&
		udiData.oppholdStatus == null &&
		(udiData.soeknadOmBeskyttelseUnderBehandling == null || !asylsøker)
	)
		return null

	const oppholdsrettTyper = [
		'eosEllerEFTABeslutningOmOppholdsrett',
		'eosEllerEFTAVedtakOmVarigOppholdsrett',
		'eosEllerEFTAOppholdstillatelse'
	]
	const currentOppholdsrettType =
		udiData.oppholdStatus && oppholdsrettTyper.find(type => udiData.oppholdStatus[type])

	const currentTredjelandsborgereStatus =
		udiData.oppholdStatus && udiData.oppholdStatus.oppholdSammeVilkaar
			? 'Oppholdstillatelse eller opphold på samme vilkår'
			: udiData.oppholdStatus && udiData.oppholdStatus.uavklart
				? 'Uavklart'
				: udiData.harOppholdsTillatelse === false
					? 'Ikke oppholdstillatalse eller ikke opphold på samme vilkår'
					: null

	const oppholdsrett = Boolean(currentOppholdsrettType)
	const tredjelandsborger = Boolean(currentTredjelandsborgereStatus)
	return {
		header: 'UDI',
		data: [
			{
				id: 'oppholdsstatus',
				label: 'Oppholdsstatus',
				value: oppholdsrett
					? 'EØS- eller EFTA-opphold'
					: tredjelandsborger
						? 'Tredjelandsborger'
						: null
			},
			{
				id: 'typeOpphold',
				label: 'Type opphold',
				value:
					oppholdsrett && Formatters.showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)
			},
			{
				id: 'status',
				label: 'Status',
				value: currentTredjelandsborgereStatus
			},
			{
				id: 'oppholdFraDato',
				label: 'Oppholdstillatelse fra dato',
				value: Formatters.formatStringDates(
					_get(udiData.oppholdStatus, `${currentOppholdsrettType}Periode.fra`) ||
						_get(udiData.oppholdStatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra')
				)
			},
			{
				id: 'oppholdTilDato',
				label: 'Oppholdstillatelse til dato',
				value: Formatters.formatStringDates(
					_get(udiData.oppholdStatus, `${currentOppholdsrettType}Periode.til`) ||
						_get(udiData.oppholdStatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til')
				)
			},
			{
				id: 'effektueringsdato',
				label: 'Effektueringsdato',
				value: Formatters.formatStringDates(
					_get(udiData.oppholdStatus, `${currentOppholdsrettType}Effektuering`) ||
						_get(udiData.oppholdStatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering')
				)
			},
			{
				id: 'typeOppholdstillatelse',
				label: 'Type oppholdstillatelse',
				value: Formatters.showLabel(
					'oppholdstillatelseType',
					_get(udiData.oppholdStatus, 'oppholdSammeVilkaar.oppholdstillatelseType')
				)
			},
			{
				id: 'vedtaksdato',
				label: 'Vedtaksdato',
				value: Formatters.formatStringDates(
					_get(udiData.oppholdStatus, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato')
				)
			},
			{
				id: 'grunnlagForOpphold',
				label: 'Grunnlag for opphold',
				value:
					oppholdsrett &&
					Formatters.showLabel(
						'eosEllerEFTABeslutningOmOppholdsrett',
						udiData.oppholdStatus[currentOppholdsrettType]
					)
			},
			{
				id: 'uavklart',
				label: 'Uavklart',
				value: udiData.uavklart && Formatters.oversettBoolean(true)
			},
			{
				id: 'harArbeidsadgang',
				label: 'Har arbeidsadgang',
				value: Formatters.allCapsToCapitalized(
					udiData.arbeidsadgang && udiData.arbeidsadgang.harArbeidsAdgang
				)
			},
			{
				id: 'typeArbeidsadgang',
				label: 'Type arbeidsadgang',
				value:
					udiData.arbeidsadgang &&
					Formatters.showLabel('typeArbeidsadgang', udiData.arbeidsadgang.typeArbeidsadgang)
			},
			{
				id: 'arbeidsOmfang',
				label: 'Arbeidsomfang',
				value:
					udiData.arbeidsadgang &&
					Formatters.showLabel('arbeidsOmfang', udiData.arbeidsadgang.arbeidsOmfang)
			},
			{
				id: 'arbeidsadgangFraDato',
				label: 'Arbeidsadgang fra dato',
				value:
					_get(udiData, 'arbeidsadgang.periode.til') &&
					Formatters.formatStringDates(udiData.arbeidsadgang.periode.fra)
			},
			{
				id: 'arbeidsadgangTilDato',
				label: 'Arbeidsadgang til dato',
				value:
					_get(udiData, 'arbeidsadgang.periode.til') &&
					Formatters.formatStringDates(udiData.arbeidsadgang.periode.til)
			},
			{
				id: 'flyktningstatus',
				label: 'Flyktningstatus',
				value: Formatters.oversettBoolean(udiData.flyktning)
			},
			{
				id: 'asylsøker',
				label: 'Asylsøker',
				value:
					asylsøker &&
					Formatters.showLabel('jaNeiUavklart', udiData.soeknadOmBeskyttelseUnderBehandling)
			}
		]
	}
}

export function mapAliasData(aliasdata) {
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
						label: 'FNR/DNR',
						value: data.fnr
					},
					{
						id: 'fornavn',
						label: 'Fornavn',
						value: data.navn.fornavn
					},
					{
						id: 'mellomnavn',
						label: 'Mellomnavn',
						value: data.navn.mellomnavn
					},
					{
						id: 'etternavn',
						label: 'Etternavn',
						value: data.navn.etternavn
					}
				]
			}
		})
	}
}
