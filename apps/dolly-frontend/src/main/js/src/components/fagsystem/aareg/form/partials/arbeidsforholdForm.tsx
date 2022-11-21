import React, { useContext, useEffect, useState } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import _set from 'lodash/set'
import _cloneDeep from 'lodash/cloneDeep'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { PermitteringForm } from './permitteringForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { MaritimtArbeidsforholdForm } from './maritimtArbeidsforholdForm'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { ArbeidsgiverTyper } from '~/components/fagsystem/aareg/AaregTypes'
import {
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers,
	initialFartoy,
	initialForenkletOppgjoersordningOrg,
	initialForenkletOppgjoersordningPers,
} from '../initialValues'
import { ArbeidsgiverIdent } from '~/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'
import { isDate } from 'date-fns'
import { EgneOrganisasjoner } from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import _isEmpty from 'lodash/isEmpty'
import { FormikErrors, FormikTouched, FormikValues, useFormikContext } from 'formik'
import _, { isEqual } from 'lodash'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'

type Arbeidsforhold = {
	isOppdatering?: boolean
	type?: string
	ansettelsesPeriode?: Ansettelsesperiode
	antallTimerForTimeloennet?: Array<unknown>
	arbeidsavtaler?: Array<unknown>
	arbeidsgiver?: ArbeidsgiverProps
	fartoy?: any
	permisjonPermitteringer?: Array<unknown>
	utenlandsopphold?: Array<unknown>
	arbeidsforholdId?: string
	navArbeidsforholdPeriode?: Date
}

type ArbeidsgiverProps = {
	type?: string
	orgnummer?: string
	offentligIdent?: string
}

type Ansettelsesperiode = {
	fom?: string
	tom?: string
	sluttaarsak?: string
}

export const ArbeidsforholdForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	erLenket,
	arbeidsgiverType,
	warningMessage,
}) => {
	const hentUnikeAaregBestillinger = (bestillinger) => {
		if (_isEmpty(bestillinger) || ameldingIndex) {
			return null
		}
		const aaregBestillinger = bestillinger
			?.filter((bestilling) => bestilling?.data?.aareg)
			?.flatMap((bestilling) => bestilling.data.aareg)
			?.filter((bestilling) => _isEmpty(bestilling?.amelding))
			?.filter((bestilling) => !bestilling?.ansettelsesPeriode?.sluttaarsak)

		return _.uniqWith(
			aaregBestillinger,
			(best1: Arbeidsforhold, best2) =>
				best1?.arbeidsgiver?.orgnummer === best2?.arbeidsgiver?.orgnummer
		)
	}

	const harGjortFormEndringer = (formValues) => {
		if (formValues.length > 1) {
			return true
		}
		return !isEqual(values.aareg, [initialArbeidsforholdOrg])
	}

	const {
		touched,
		values,
		errors,
		setFieldValue,
	}: {
		touched: FormikTouched<any>
		values: FormikValues
		errors: FormikErrors<any>
		setFieldValue: any
	} = useFormikContext()
	const [navArbeidsforholdPeriode, setNavArbeidsforholdPeriode] = useState(null as Date)
	const { tidligereBestillinger } = useContext(BestillingsveilederContext)
	const tidligereAaregBestillinger = hentUnikeAaregBestillinger(tidligereBestillinger)
	const erLaastArbeidsforhold =
		(arbeidsgiverType === ArbeidsgiverTyper.felles ||
			arbeidsgiverType === ArbeidsgiverTyper.fritekst) &&
		arbeidsforholdIndex < tidligereAaregBestillinger?.length

	useEffect(() => {
		if (_isEmpty(tidligereAaregBestillinger) || harGjortFormEndringer(values.aareg)) {
			return
		}
		setFieldValue(
			'aareg',
			tidligereAaregBestillinger.map((aaregBestilling) => {
				aaregBestilling.isOppdatering = true
				return aaregBestilling
			})
		)
	}, [values.aareg])

	const gjeldendeArbeidsgiver = _get(values, `${path}.arbeidsgiver`)

	const arbeidsforholdstype =
		typeof ameldingIndex !== 'undefined'
			? _get(values, 'aareg[0].arbeidsforholdstype')
			: _get(values, `${path}.arbeidsforholdstype`)
	const onChangeLenket = (fieldPath: string) => {
		if (arbeidsgiverType !== ArbeidsgiverTyper.egen) {
			return (field) => {
				const value = isDate(field) ? field : field?.value || field?.target?.value || null
				setFieldValue(`${path}.${fieldPath}`, value)
			}
		} else {
			return (field) => {
				const value = isDate(field) ? field : field?.value || field?.target?.value || null
				const amelding = _get(values, 'aareg[0].amelding') || []
				amelding.forEach((_maaned, idx) => {
					if (!erLenket && idx < ameldingIndex) {
						return null
					} else {
						const arbeidsforholdClone = _cloneDeep(
							amelding[idx].arbeidsforhold[arbeidsforholdIndex]
						)
						_set(arbeidsforholdClone, fieldPath, value)
						_set(amelding[idx], `arbeidsforhold[${arbeidsforholdIndex}]`, arbeidsforholdClone)
					}
				})
				setFieldValue('aareg[0].amelding', amelding)
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
					setFieldValue(path, {
						...initialForenkletOppgjoersordningOrg,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					setFieldValue(path, {
						...initialForenkletOppgjoersordningPers,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
				}
			}
		} else {
			if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
				if (
					arbeidsgiverType === ArbeidsgiverTyper.felles ||
					arbeidsgiverType === ArbeidsgiverTyper.fritekst
				) {
					setFieldValue(path, {
						...initialArbeidsforholdOrg,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					setFieldValue(path, {
						...initialArbeidsforholdPers,
						arbeidsforholdstype: event.value,
						arbeidsgiver: gjeldendeArbeidsgiver,
					})
				}
			} else {
				setFieldValue(`${path}.arbeidsforholdstype`, event.value)
			}
			if (event.value === 'maritimtArbeidsforhold') {
				setFieldValue(`${path}.fartoy`, initialFartoy)
			} else {
				setFieldValue(`${path}.fartoy`, undefined)
			}
		}
	}

	useEffect(() => {
		setFieldValue(
			`${path}.navArbeidsforholdPeriode`,
			navArbeidsforholdPeriode
				? {
						year: navArbeidsforholdPeriode.getFullYear(),
						monthValue: navArbeidsforholdPeriode.getMonth(),
				  }
				: undefined
		)
	}, [navArbeidsforholdPeriode])

	const feilmelding = () => {
		if (
			!_get(values, `${path}.arbeidsforholdstype`) &&
			_has(touched, `${path}.arbeidsforholdstype`)
		) {
			return {
				feilmelding: _get(errors, `${path}.arbeidsforholdstype`),
			}
		}
	}

	const checkAktiveArbeidsforhold = (aaregValues) => {
		const aktiveArbeidsforhold = aaregValues.map((arbeidsforhold) => {
			const orgnummer = arbeidsforhold?.arbeidsgiver?.orgnummer
			if (!arbeidsforhold?.ansettelsesPeriode?.sluttaarsak) {
				return orgnummer
			}
		})
		const dupliserteAktiveArbeidsforhold = aktiveArbeidsforhold
			.filter((arbeidsforhold, index) => index !== aktiveArbeidsforhold.indexOf(arbeidsforhold))
			.filter((arbeidsforhold) => !_isEmpty(arbeidsforhold))
		return _isEmpty(dupliserteAktiveArbeidsforhold)
			? null
			: {
					feilmelding: `Identen har allerede pågående arbeidsforhold i org: ${dupliserteAktiveArbeidsforhold.toString()}`,
			  }
	}

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				{arbeidsgiverType === ArbeidsgiverTyper.egen && (
					<EgneOrganisasjoner
						path={`${path}.arbeidsgiver.orgnummer`}
						handleChange={onChangeLenket('arbeidsgiver.orgnummer')}
						warningMessage={warningMessage}
						filterValidEnhetstyper={true}
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.felles && (
					<OrganisasjonMedArbeidsforholdSelect
						path={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						feil={checkAktiveArbeidsforhold(values.aareg)}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.fritekst && (
					<FormikTextInput
						name={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						size="xlarge"
						feil={checkAktiveArbeidsforhold(values.aareg)}
						defaultValue={gjeldendeArbeidsgiver?.orgnummer}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{arbeidsgiverType !== ArbeidsgiverTyper.egen && (
					<FormikSelect
						name={`${path}.arbeidsforholdstype`}
						label="Type arbeidsforhold"
						kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
						size="large-plus"
						isClearable={false}
						onChange={handleArbeidsforholdstypeChange}
						feil={feilmelding()}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.privat && (
					<ArbeidsgiverIdent
						path={`${path}.arbeidsgiver.ident`}
						isDisabled={erLaastArbeidsforhold}
					/>
				)}
				<FormikDatepicker
					name={`${path}.ansettelsesPeriode.fom`}
					label="Ansatt fra"
					onChange={onChangeLenket('ansettelsesPeriode.fom')}
					fastfield={false}
				/>
				<FormikDatepicker
					name={`${path}.ansettelsesPeriode.tom`}
					label="Ansatt til"
					onChange={onChangeLenket('ansettelsesPeriode.tom')}
					fastfield={false}
				/>
				<FormikSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					onChange={onChangeLenket('ansettelsesPeriode.sluttaarsak')}
					isDisabled={!_get(values, `${path}.ansettelsesPeriode.tom`)}
				/>
				<Monthpicker
					name={`${path}.navArbeidsforholdPeriode`}
					date={navArbeidsforholdPeriode}
					label="NAV arbeidsforholdsperiode"
					onChange={setNavArbeidsforholdPeriode}
					isClearable={true}
				/>
				{arbeidsforholdstype === 'forenkletOppgjoersordning' && (
					<FormikSelect
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
