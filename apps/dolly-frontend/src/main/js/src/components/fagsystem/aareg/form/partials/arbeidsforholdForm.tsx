import React, { useContext, useEffect } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { PermitteringForm } from './permitteringForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { MaritimtArbeidsforholdForm } from './maritimtArbeidsforholdForm'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import {
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers,
	initialFartoy,
	initialForenkletOppgjoersordningOrg,
	initialForenkletOppgjoersordningPers,
} from '../initialValues'
import { ArbeidsgiverIdent } from '@/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'
import { isDate } from 'date-fns'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import _ from 'lodash'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import subYears from 'date-fns/subYears'

export const hentStoersteAaregdata = () => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)

	if (_.isEmpty(personFoerLeggTil?.aareg)) {
		return null
	}

	let stoersteAaregdata: any = []
	personFoerLeggTil?.aareg?.forEach((aareg: any) => {
		if (aareg.data?.length > stoersteAaregdata.length) {
			stoersteAaregdata = aareg?.data
		}
	})
	stoersteAaregdata.sort((a: any, b: any) => a.arbeidsforholdId.localeCompare(b.arbeidsforholdId))

	const aaregdataClean: any = []

	stoersteAaregdata.forEach((aareg: any) => {
		const arbeidsforhold = {
			arbeidsforholdstype: aareg.type || 'ordinaertArbeidsforhold',
			arbeidsgiver: {
				aktoertype: aareg.arbeidsgiver?.type === 'Person' ? 'PERS' : 'ORG',
				orgnummer: aareg.arbeidsgiver?.organisasjonsnummer || undefined,
				ident: aareg.arbeidsgiver?.offentligIdent || undefined,
			},
			arbeidsforholdId: aareg.arbeidsforholdId || '',
			ansettelsesPeriode: {
				fom: aareg.ansettelsesperiode?.periode?.fom || subYears(new Date(), 20),
				tom: aareg.ansettelsesperiode?.periode?.tom || null,
				sluttaarsak: aareg.ansettelsesperiode?.sluttaarsak || null,
			},
			arbeidsavtale: {
				yrke: aareg.arbeidsavtaler?.[0]?.yrke || '',
				ansettelsesform: aareg.arbeidsavtaler?.[0]?.ansettelsesform || 'fast',
				stillingsprosent: aareg.arbeidsavtaler?.[0]?.stillingsprosent || 100,
				endringsdatoStillingsprosent: aareg.arbeidsavtaler?.[0]?.sistStillingsendring || null,
				sisteLoennsendringsdato: aareg.arbeidsavtaler?.[0]?.sistLoennsendring || null,
				arbeidstidsordning: aareg.arbeidsavtaler?.[0]?.arbeidstidsordning || 'ikkeSkift',
				avtaltArbeidstimerPerUke: aareg.arbeidsavtaler?.[0]?.antallTimerPrUke || 37.5,
			},
			antallTimerForTimeloennet:
				aareg.antallTimerForTimeloennet?.map((timeloennet) => {
					return {
						periode: {
							fom: timeloennet.periode?.fom || null,
							tom: timeloennet.periode?.tom || null,
						},
						antallTimer: timeloennet.antallTimer || 0,
					}
				}) || [],
			utenlandsopphold:
				aareg.utenlandsopphold?.map((opphold) => {
					return {
						periode: {
							fom: opphold.periode?.fom || null,
							tom: opphold.periode?.tom || null,
						},
						land: opphold.landkode || '',
					}
				}) || [],
			permisjon:
				aareg.permisjonPermitteringer
					?.filter((item) => item.type !== 'permittering')
					?.map((permisjon) => {
						return {
							permisjonsPeriode: {
								fom: permisjon.periode?.fom || null,
								tom: permisjon.periode?.tom || null,
							},
							permisjonsprosent: permisjon.prosent || 100,
							permisjon: permisjon.type || '',
						}
					}) || [],
			permittering:
				aareg.permisjonPermitteringer
					?.filter((item) => item.type === 'permittering')
					?.map((permittering) => {
						return {
							permitteringsPeriode: {
								fom: permittering.periode?.fom || null,
								tom: permittering.periode?.tom || null,
							},
							permitteringsprosent: permittering.prosent || 100,
						}
					}) || [],
			fartoy:
				aareg.type === 'maritimtArbeidsforhold'
					? [
							{
								skipsregister: aareg.arbeidsavtaler?.[0]?.skipsregister || '',
								skipstype: aareg.arbeidsavtaler?.[0]?.skipstype || '',
								fartsomraade: aareg.arbeidsavtaler?.[0]?.fartsomraade || '',
							},
						]
					: undefined,
		}
		aaregdataClean.push(arbeidsforhold)
	})
	return aaregdataClean
}

export const ArbeidsforholdForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	erLenket,
	arbeidsgiverType,
	warningMessage,
}) => {
	const harGjortFormEndringer = () => {
		if (watch('aareg').length > 1) {
			return true
		}
		return !_.isEqual(
			_.omit(watch('aareg')?.[0], ['ansettelsesPeriode']),
			_.omit(initialArbeidsforholdOrg, ['ansettelsesPeriode']),
		)
	}

	const { setError, watch, getValues, setValue, trigger } = useFormContext()

	const tidligereAaregdata = hentStoersteAaregdata()

	const erLaastArbeidsforhold = arbeidsforholdIndex < tidligereAaregdata?.length

	useEffect(() => {
		if (_.isEmpty(tidligereAaregdata) || harGjortFormEndringer()) {
			return
		}
		setValue(
			'aareg',
			tidligereAaregdata?.map((aaregBestilling) => {
				aaregBestilling.isOppdatering = true
				return aaregBestilling
			}),
		)
		trigger('aareg')
	}, [watch('aareg')])

	const gjeldendeArbeidsgiver = watch(`${path}.arbeidsgiver`)

	const arbeidsforholdstype =
		typeof ameldingIndex !== 'undefined'
			? _.get(getValues(), 'aareg[0].arbeidsforholdstype')
			: _.get(getValues(), `${path}.arbeidsforholdstype`)
	const onChangeLenket = (fieldPath: string) => {
		if (arbeidsgiverType !== ArbeidsgiverTyper.egen) {
			return (field) => {
				const value = isDate(field)
					? fixTimezone(field)
					: field?.value || field?.target?.value || null
				setValue(`${path}.${fieldPath}`, value)
				trigger()
			}
		} else {
			return (field) => {
				const value = isDate(field)
					? fixTimezone(field)
					: field?.value || field?.target?.value || null
				const amelding = watch('aareg[0].amelding') || []
				amelding.forEach((_maaned, idx) => {
					if (!erLenket && idx < ameldingIndex) {
						return null
					} else {
						const arbeidsforholdClone = _.cloneDeep(
							amelding[idx].arbeidsforhold[arbeidsforholdIndex],
						)
						_.set(arbeidsforholdClone, fieldPath, value)
						_.set(amelding[idx], `arbeidsforhold[${arbeidsforholdIndex}]`, arbeidsforholdClone)
					}
				})
				setValue('aareg[0].amelding', amelding)
				trigger('aareg[0].amelding')
			}
		}
	}

	const handleArbeidsforholdstypeChange = (event) => {
		if (event.value === 'forenkletOppgjoersordning') {
			if (arbeidsforholdstype !== 'forenkletOppgjoersordning') {
				if (
					arbeidsgiverType === ArbeidsgiverTyper.felles ||
					arbeidsgiverType === ArbeidsgiverTyper.fritekst
				) {
					setValue(path, {
						...initialForenkletOppgjoersordningOrg,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
					trigger()
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					setValue(path, {
						...initialForenkletOppgjoersordningPers,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
					trigger()
				}
			}
		} else {
			if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
				if (
					arbeidsgiverType === ArbeidsgiverTyper.felles ||
					arbeidsgiverType === ArbeidsgiverTyper.fritekst
				) {
					setValue(path, {
						...initialArbeidsforholdOrg,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
					trigger()
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					setValue(path, {
						...initialArbeidsforholdPers,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
					trigger()
				}
			} else {
				setValue(`${path}.arbeidsforholdstype`, event.value)
				trigger()
			}
			if (event.value === 'maritimtArbeidsforhold') {
				setValue(`${path}.fartoy`, initialFartoy)
				trigger()
			} else {
				setValue(`${path}.fartoy`, undefined)
				trigger()
			}
		}
	}

	const checkAktiveArbeidsforhold = () => {
		const aaregValues = getValues('aareg')
		const aktiveArbeidsforhold = aaregValues.map((arbeidsforhold) => {
			const orgnummer = arbeidsforhold?.arbeidsgiver?.orgnummer
			if (!arbeidsforhold?.ansettelsesPeriode?.sluttaarsak) {
				return orgnummer
			}
		})
		const dupliserteAktiveArbeidsforhold = aktiveArbeidsforhold
			.filter((arbeidsforhold, index) => index !== aktiveArbeidsforhold.indexOf(arbeidsforhold))
			.filter((arbeidsforhold) => !_.isEmpty(arbeidsforhold))
		if (!_.isEmpty(dupliserteAktiveArbeidsforhold)) {
			setError(`${path}.arbeidsgiver.orgnummer`, {
				message: `Identen har allerede pågående arbeidsforhold i org: ${dupliserteAktiveArbeidsforhold.toString()}`,
			})
		}
	}

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				{arbeidsgiverType === ArbeidsgiverTyper.egen && (
					<div className="flex-box">
						<EgneOrganisasjoner
							path={`${path}.arbeidsgiver.orgnummer`}
							handleChange={onChangeLenket('arbeidsgiver.orgnummer')}
							warningMessage={warningMessage}
							filterValidEnhetstyper={true}
						/>
					</div>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.felles && (
					<OrganisasjonMedArbeidsforholdSelect
						path={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						afterChange={() => checkAktiveArbeidsforhold()}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.fritekst && (
					<FormTextInput
						name={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						size="xlarge"
						onKeyPress={() => checkAktiveArbeidsforhold()}
						defaultValue={gjeldendeArbeidsgiver?.orgnummer}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{arbeidsgiverType !== ArbeidsgiverTyper.egen && (
					<FormSelect
						name={`${path}.arbeidsforholdstype`}
						label="Type arbeidsforhold"
						kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
						size="xlarge"
						isClearable={false}
						onChange={handleArbeidsforholdstypeChange}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.privat && (
					<ArbeidsgiverIdent
						path={`${path}.arbeidsgiver.ident`}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				<FormTextInput
					label="Arbeidsforhold ID"
					name={`${path}.arbeidsforholdId`}
					isDisabled={true}
				/>
				<FormDatepicker
					name={`${path}.ansettelsesPeriode.fom`}
					label="Ansatt fra"
					onChange={onChangeLenket('ansettelsesPeriode.fom')}
				/>
				<FormDatepicker
					name={`${path}.ansettelsesPeriode.tom`}
					label="Ansatt til"
					onChange={onChangeLenket('ansettelsesPeriode.tom')}
				/>
				<FormSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					onChange={onChangeLenket('ansettelsesPeriode.sluttaarsak')}
					isDisabled={!_.get(getValues(), `${path}.ansettelsesPeriode.tom`)}
				/>
				{arbeidsforholdstype === 'forenkletOppgjoersordning' && (
					<FormSelect
						value={watch(`${path}.arbeidsavtale.yrke`)}
						name={`${path}.arbeidsavtale.yrke`}
						label="Yrke"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxxlarge"
						isClearable={false}
						optionHeight={50}
						onChange={onChangeLenket('arbeidsavtale.yrke')}
					/>
				)}
			</div>

			{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
				<ArbeidsavtaleForm path={`${path}.arbeidsavtale`} onChangeLenket={onChangeLenket} />
			)}
			{arbeidsforholdstype === 'maritimtArbeidsforhold' && (
				<MaritimtArbeidsforholdForm path={`${path}.fartoy[0]`} onChangeLenket={onChangeLenket} />
			)}

			{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
				<>
					<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />
					<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />
					<PermisjonForm path={`${path}.permisjon`} />
					<PermitteringForm path={`${path}.permittering`} />
				</>
			)}
		</React.Fragment>
	)
}
