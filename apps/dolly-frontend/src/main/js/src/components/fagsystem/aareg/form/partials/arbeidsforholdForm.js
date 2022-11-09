import React, { useContext, useEffect } from 'react'
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
import { ArbeidsgiverIdent } from '~/components/fagsystem/aareg/form/partials/arbeidsgiverIdent.tsx'
import { isDate } from 'date-fns'
import { EgneOrganisasjoner } from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import _isEmpty from 'lodash/isEmpty'
import { useFormikContext } from 'formik'

export const ArbeidsforholdForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	erLenket,
	arbeidsgiverType,
	warningMessage,
}) => {
	const hentAaregBestillinger = (bestillinger) => {
		if (_isEmpty(bestillinger)) {
			return null
		}
		return bestillinger
			?.filter((bestilling) => bestilling?.data?.aareg)
			?.map((bestilling) => bestilling.data.aareg?.[0])
	}

	const { touched, values, errors, setFieldValue } = useFormikContext()
	const { tidligereBestillinger } = useContext(BestillingsveilederContext)
	const tidligereAaregBestillinger = hentAaregBestillinger(tidligereBestillinger)

	useEffect(() => {
		if (_isEmpty(tidligereAaregBestillinger)) {
			return null
		}
		setFieldValue('aareg', tidligereAaregBestillinger)
	}, [])

	const gjeldendeArbeidsgiver = _get(values, `${path}.arbeidsgiver`)
	const arbeidsforholdstype =
		typeof ameldingIndex !== 'undefined'
			? _get(values, 'aareg[0].arbeidsforholdstype')
			: _get(values, `${path}.arbeidsforholdstype`)
	const onChangeLenket = (fieldPath) => {
		if (arbeidsgiverType !== ArbeidsgiverTyper.egen) {
			return (field) => {
				const value = isDate(field) ? field : field?.value || field?.target?.value || null
				setFieldValue(`${path}.${fieldPath}`, value)
			}
		} else {
			return (field) => {
				const value = isDate(field) ? field : field?.value || field?.target?.value || null
				const amelding = _get(values, 'aareg[0].amelding')
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
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.fritekst && (
					<FormikTextInput
						name={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						size="xlarge"
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
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.privat && (
					<ArbeidsgiverIdent path={`${path}.arbeidsgiver.ident`} />
				)}
				{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
					<FormikTextInput
						key={`${path}.arbeidsforholdID`}
						name={`${path}.arbeidsforholdID`}
						label="Arbeidsforhold-ID"
						type="text"
						onBlur={onChangeLenket('arbeidsforholdID')}
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
				{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
					<FormikSelect
						name={`${path}.ansettelsesPeriode.sluttaarsak`}
						label="Sluttårsak"
						kodeverk={ArbeidKodeverk.SluttaarsakAareg}
						size="xlarge"
						onChange={onChangeLenket('ansettelsesPeriode.sluttaarsak')}
						disabled={_get(values, `${path}.ansettelsesPeriode.tom`) === null}
					/>
				)}
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
