import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput, DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { PermitteringForm } from './permitteringForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { MaritimtArbeidsforholdForm } from './maritimtArbeidsforholdForm'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { ArbeidKodeverk } from '~/config/kodeverk'
import Hjelpetekst from '~/components/hjelpetekst'
import { ArbeidsgiverTyper } from '~/components/fagsystem/aareg/AaregTypes'
import EgenOrganisasjonConnector from '~/components/organisasjonSelect/EgenOrganisasjonConnector'
import {
	initialForenkletOppgjoersordningOrg,
	initialForenkletOppgjoersordningPers,
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers,
	initialFartoy
} from '../initialValues'

export const ArbeidsforholdForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	formikBag,
	erLenket,
	arbeidsgiverType,
	brukerId
}) => {
	const arbeidsforholdstype =
		_get(formikBag.values, 'aareg[0].arbeidsforholdstype') ||
		_get(formikBag.values, `${path}.arbeidsforholdstype`)

	const onChangeLenket = fieldPath => {
		if (arbeidsgiverType !== ArbeidsgiverTyper.egen) {
			return field => {
				formikBag.setFieldValue(
					`${path}.${fieldPath}`,
					field?.value || field?.target?.value || field
				)
			}
		}

		return field => {
			const amelding = _get(formikBag.values, 'aareg[0].amelding')

			amelding.forEach((maaned, idx) => {
				if (!erLenket && idx < ameldingIndex) return
				formikBag.setFieldValue(
					`aareg[0].amelding[${idx}].arbeidsforhold[${arbeidsforholdIndex}].${fieldPath}`,
					field?.value || field?.target?.value || field
				)
			})
		}
	}

	const handleArbeidsforholdstypeChange = event => {
		if (event.value === 'forenkletOppgjoersordning') {
			if (arbeidsforholdstype !== 'forenkletOppgjoersordning') {
				if (
					arbeidsgiverType === ArbeidsgiverTyper.felles ||
					arbeidsgiverType === ArbeidsgiverTyper.fritekst
				) {
					formikBag.setFieldValue(`aareg[0].arbeidsforhold[${arbeidsforholdIndex}]`, {
						...initialForenkletOppgjoersordningOrg,
						arbeidsforholdstype: event.value
					})
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					formikBag.setFieldValue(`aareg[0].arbeidsforhold[${arbeidsforholdIndex}]`, {
						...initialForenkletOppgjoersordningPers,
						arbeidsforholdstype: event.value
					})
				}
			}
		} else {
			if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
				if (
					arbeidsgiverType === ArbeidsgiverTyper.felles ||
					arbeidsgiverType === ArbeidsgiverTyper.fritekst
				) {
					formikBag.setFieldValue(`aareg[0].arbeidsforhold[${arbeidsforholdIndex}]`, {
						...initialArbeidsforholdOrg,
						arbeidsforholdstype: event.value
					})
				} else if (arbeidsgiverType === ArbeidsgiverTyper.privat) {
					formikBag.setFieldValue(`aareg[0].arbeidsforhold[${arbeidsforholdIndex}]`, {
						...initialArbeidsforholdPers,
						arbeidsforholdstype: event.value
					})
				}
			} else {
				formikBag.setFieldValue(
					`aareg[0].arbeidsforhold[${arbeidsforholdIndex}].arbeidsforholdstype`,
					event.value
				)
			}
			if (event.value === 'maritimtArbeidsforhold') {
				formikBag.setFieldValue(
					`aareg[0].arbeidsforhold[${arbeidsforholdIndex}].fartoy`,
					initialFartoy
				)
			} else {
				formikBag.setFieldValue(`aareg[0].arbeidsforhold[${arbeidsforholdIndex}].fartoy`, undefined)
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
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.egen && (
					<EgenOrganisasjonConnector
						name={`${path}.arbeidsgiver.orgnummer`}
						isClearable={false}
						onChange={onChangeLenket('arbeidsgiver.orgnummer')}
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
					<FormikTextInput name={`${path}.arbeidsgiver.ident`} label="Arbeidsgiver ident" />
				)}
				{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
					<DollyTextInput
						name={`${path}.arbeidsforholdID`}
						label="Arbeidsforhold-ID"
						type="text"
						value={_get(formikBag.values, `${path}.arbeidsforholdID`)}
						onChange={onChangeLenket('arbeidsforholdID')}
					/>
				)}
				<FormikDatepicker
					name={`${path}.ansettelsesPeriode.fom`}
					label="Ansatt fra"
					onChange={onChangeLenket('ansettelsesPeriode.fom')}
				/>
				<FormikDatepicker
					name={`${path}.ansettelsesPeriode.tom`}
					label="Ansatt til"
					onChange={onChangeLenket('ansettelsesPeriode.tom')}
				/>
				{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
					<FormikSelect
						name={`${path}.ansettelsesPeriode.sluttaarsak`}
						label="Sluttårsak"
						kodeverk={ArbeidKodeverk.SluttaarsakAareg}
						size="xlarge"
						onChange={onChangeLenket('ansettelsesPeriode.sluttaarsak')}
						disabled={
							_get(formikBag.values, `${path}.ansettelsesPeriode.tom`) === null ? true : false
						}
						// TODO disabled funker ikke!
					/>
				)}
				{arbeidsforholdstype === 'forenkletOppgjoersordning' && (
					<FormikSelect
						name={`${path}.yrke`}
						label="Yrke"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxxlarge"
						isClearable={false}
						optionHeight={50}
					/>
				)}
			</div>

			{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
				<ArbeidsavtaleForm
					formikBag={formikBag}
					path={`${path}.arbeidsavtale`}
					onChangeLenket={onChangeLenket}
				/>
			)}
			{arbeidsforholdstype === 'maritimtArbeidsforhold' && (
				<MaritimtArbeidsforholdForm path={`${path}.fartoy[0]`} onChangeLenket={onChangeLenket} />
			)}

			{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
				<>
					<TimeloennetForm
						path={`${path}.antallTimerForTimeloennet`}
						ameldingIndex={ameldingIndex}
						arbeidsforholdIndex={arbeidsforholdIndex}
						formikBag={formikBag}
						erLenket={erLenket}
					/>

					<UtenlandsoppholdForm
						path={`${path}.utenlandsopphold`}
						ameldingIndex={ameldingIndex}
						arbeidsforholdIndex={arbeidsforholdIndex}
						formikBag={formikBag}
						erLenket={erLenket}
					/>

					<PermisjonForm
						path={`${path}.permisjon`}
						ameldingIndex={ameldingIndex}
						arbeidsforholdIndex={arbeidsforholdIndex}
						formikBag={formikBag}
						erLenket={erLenket}
					/>

					<PermitteringForm
						path={`${path}.permittering`}
						ameldingIndex={ameldingIndex}
						arbeidsforholdIndex={arbeidsforholdIndex}
						formikBag={formikBag}
						erLenket={erLenket}
					/>
				</>
			)}
		</React.Fragment>
	)
}
