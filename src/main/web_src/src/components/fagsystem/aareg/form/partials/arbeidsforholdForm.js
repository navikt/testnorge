import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
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

	const virksomheter = SelectOptionsOppslag.hentVirksomheterFraOrgforvalter(brukerId)
	const virksomheterOptions = SelectOptionsOppslag.formatOptions('virksomheter', virksomheter)

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
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.egen && (
					<FormikSelect
						name={`${path}.arbeidsgiver.orgnummer`}
						label="Organisasjonsnummer"
						options={virksomheterOptions}
						size="xxlarge"
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
				<DollyTextInput
					name={`${path}.arbeidsforholdID`}
					label="Arbeidsforhold-ID"
					type="text"
					value={_get(formikBag.values, `${path}.arbeidsforholdID`)}
					onChange={onChangeLenket('arbeidsforholdID')}
				/>
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
			</div>

			<ArbeidsavtaleForm
				formikBag={formikBag}
				path={`${path}.arbeidsavtale`}
				onChangeLenket={onChangeLenket}
			/>
			{arbeidsforholdstype === 'maritimtArbeidsforhold' && (
				<MaritimtArbeidsforholdForm path={`${path}.fartoy`} onChangeLenket={onChangeLenket} />
			)}

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
		</React.Fragment>
	)
}
