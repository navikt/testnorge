import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
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

export const ArbeidsforholdForm = ({ path, formikBag, erLenket, arbeidsgiverType, brukerId }) => {
	const arbeidsforholdIndex = path.charAt(path.length - 1)
	//TODO Må vise type for ikke A-melding også
	const arbeidsforholdstype = _get(formikBag.values, 'aareg[0].arbeidsforholdstype')

	const virksomheter = SelectOptionsOppslag.hentVirksomheterFraOrgforvalter(brukerId)
	const virksomheterOptions = SelectOptionsOppslag.formatOptions('virksomheter', virksomheter)

	const onChangeLenket = (_erlenket, fieldPath) => {
		return field => {
			const amelding = _get(formikBag.values, 'aareg[0].amelding')
			if (_erlenket) {
				amelding.forEach((maaned, idx) => {
					formikBag.setFieldValue(
						`aareg[0].amelding[${idx}].arbeidsforhold[${arbeidsforholdIndex}].${fieldPath}`,
						field.value
					)
				})
			} else {
				formikBag.setFieldValue(`${path}.${fieldPath}`, field.value)
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
						size="large"
						isClearable={false}
					/>
				)}
				{arbeidsgiverType === ArbeidsgiverTyper.egen && (
					<FormikSelect
						name={`${path}.arbeidsgiver.orgnummer`}
						label="Organisasjonsnummer"
						//TODO: Laster ikke med en gang
						options={virksomheterOptions}
						size="xxlarge"
						isClearable={false}
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
				{arbeidsforholdstype === ArbeidsgiverTyper.privat && (
					<FormikTextInput name={`${path}.arbeidsgiver.ident`} label="Arbeidsgiver ident" />
				)}
				<FormikTextInput name={`${path}.arbeidsforholdId`} label="Arbeidsforhold-ID" type="text" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.fom`} label="Ansatt fra" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.tom`} label="Ansatt til" />
				<FormikSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					onChange={onChangeLenket(erLenket, 'ansettelsesPeriode.sluttaarsak')}
					disabled={
						_get(formikBag.values, `${path}.ansettelsesPeriode.tom`) === null ? true : false
					}
					fastField={false}
					// TODO disabled funker ikke!
				/>
			</div>

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />
			{arbeidsforholdstype === 'maritimtArbeidsforhold' && (
				<MaritimtArbeidsforholdForm formikBag={formikBag} path={`${path}.fartoy`} />
			)}

			<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<PermitteringForm path={`${path}.permittering`} />
		</React.Fragment>
	)
}
