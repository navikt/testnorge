import React from 'react'
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
import EgneOrganisasjonerConnector from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjonerConnector'

export const ArbeidsforholdForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	formikBag,
	erLenket,
	arbeidsgiverType,
	warningMessage,
}) => {
	const arbeidsforholdstype =
		typeof ameldingIndex !== 'undefined'
			? _get(formikBag.values, 'aareg[0].arbeidsforholdstype')
			: _get(formikBag.values, `${path}.arbeidsforholdstype`)
	const onChangeLenket = (fieldPath) => {
		if (arbeidsgiverType !== ArbeidsgiverTyper.egen) {
			return (field) => {
				const value = isDate(field) ? field : field?.value || field?.target?.value || null
				formikBag.setFieldValue(`${path}.${fieldPath}`, value)
			}
		} else {
			return (field) => {
				const value = isDate(field) ? field : field?.value || field?.target?.value || null
				const amelding = _get(formikBag.values, 'aareg[0].amelding')
				amelding.forEach((maaned, idx) => {
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
				formikBag.setFieldValue('aareg[0].amelding', amelding)
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
					formikBag.setFieldValue(path, {
						...initialForenkletOppgjoersordningOrg,
						arbeidsforholdstype: event.value,
					})
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					formikBag.setFieldValue(path, {
						...initialForenkletOppgjoersordningPers,
						arbeidsforholdstype: event.value,
					})
				}
			}
		} else {
			if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
				if (
					arbeidsgiverType === ArbeidsgiverTyper.felles ||
					arbeidsgiverType === ArbeidsgiverTyper.fritekst
				) {
					formikBag.setFieldValue(path, {
						...initialArbeidsforholdOrg,
						arbeidsforholdstype: event.value,
					})
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					formikBag.setFieldValue(path, {
						...initialArbeidsforholdPers,
						arbeidsforholdstype: event.value,
					})
				}
			} else {
				formikBag.setFieldValue(`${path}.arbeidsforholdstype`, event.value)
			}
			if (event.value === 'maritimtArbeidsforhold') {
				formikBag.setFieldValue(`${path}.fartoy`, initialFartoy)
			} else {
				formikBag.setFieldValue(`${path}.fartoy`, undefined)
			}
		}
	}

	const feilmelding = () => {
		if (
			!_get(formikBag.values, `${path}.arbeidsforholdstype`) &&
			_has(formikBag.touched, `${path}.arbeidsforholdstype`)
		) {
			return {
				feilmelding: _get(formikBag.errors, `${path}.arbeidsforholdstype`),
			}
		}
	}
	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
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
				{arbeidsgiverType === ArbeidsgiverTyper.egen && (
					<EgneOrganisasjonerConnector
						path={`${path}.arbeidsgiver.orgnummer`}
						formikBag={formikBag}
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
				{arbeidsgiverType === ArbeidsgiverTyper.privat && (
					<ArbeidsgiverIdent formikBag={formikBag} path={`${path}.arbeidsgiver.ident`} />
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
						disabled={_get(formikBag.values, `${path}.ansettelsesPeriode.tom`) === null}
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
