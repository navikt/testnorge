import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { PermitteringForm } from './permitteringForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { MaritimtArbeidsforholdForm } from './maritimtArbeidsforholdForm'
import { ArbeidKodeverk } from '@/config/kodeverk'
import {
	initialArbeidsavtale,
	initialArbeidsforhold,
	initialFartoy,
	initialForenkletOppgjoersordning,
} from '../initialValues'
import { isDate } from 'date-fns'
import * as _ from 'lodash-es'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'

type ArbeidsforholdProps = {
	path: string
	arbeidsforholdIndex: number
	tidligereAaregdata: any
}

export const ArbeidsforholdForm = ({
	path,
	arbeidsforholdIndex,
	tidligereAaregdata,
}: ArbeidsforholdProps) => {
	const { watch, getValues, setValue, trigger } = useFormContext()

	const erLaastArbeidsforhold = arbeidsforholdIndex < tidligereAaregdata?.length

	const gjeldendeArbeidsgiver = watch(`${path}.arbeidsgiver`)
	const gjeldendeArbeidsavtale = watch(`${path}.arbeidsavtale`)

	const arbeidsforholdstype = watch(`${path}.arbeidsforholdstype`)

	const handleChange = (fieldPath: string) => {
		return (field: any) => {
			const value = isDate(field)
				? fixTimezone(field)
				: field?.value || field?.target?.value || null
			setValue(`${path}.${fieldPath}`, value)
			trigger(path)
		}
	}

	const handleArbeidsforholdstypeChange = (event: any) => {
		if (event.value === 'forenkletOppgjoersordning') {
			if (arbeidsforholdstype !== 'forenkletOppgjoersordning') {
				setValue(path, {
					...initialForenkletOppgjoersordning,
					arbeidsforholdstype: event.value,
					arbeidsgiver: gjeldendeArbeidsgiver,
					arbeidsavtale: { yrke: gjeldendeArbeidsavtale?.yrke || '' },
				})
				trigger(path)
			}
		} else {
			if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
				setValue(path, {
					...initialArbeidsforhold,
					arbeidsforholdstype: event.value,
					arbeidsgiver: gjeldendeArbeidsgiver,
					arbeidsavtale: { ...initialArbeidsavtale, yrke: gjeldendeArbeidsavtale?.yrke || '' },
				})
				trigger(path)
			} else {
				setValue(`${path}.arbeidsforholdstype`, event.value)
				trigger(path)
			}
			if (event.value === 'maritimtArbeidsforhold') {
				setValue(`${path}.fartoy`, initialFartoy)
				trigger(path)
			} else {
				setValue(`${path}.fartoy`, undefined)
				trigger(path)
			}
		}
	}

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				<div
					title={erLaastArbeidsforhold ? 'Kan ikke endre type på eksisterende arbeidsforhold' : ''}
				>
					<FormSelect
						name={`${path}.arbeidsforholdstype`}
						label="Type arbeidsforhold"
						kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
						size="xlarge"
						isClearable={false}
						onChange={handleArbeidsforholdstypeChange}
						isDisabled={erLaastArbeidsforhold}
					/>
				</div>
				{getValues(`${path}.isOppdatering`) && (
					<FormTextInput
						label="Arbeidsforhold-ID"
						name={`${path}.arbeidsforholdId`}
						isDisabled={true}
						title="Kan ikke endre arbeidsforhold-ID på eksisterende arbeidsforhold"
					/>
				)}
				<FormDatepicker
					name={`${path}.ansettelsesPeriode.fom`}
					label="Ansatt fra"
					onChange={handleChange('ansettelsesPeriode.fom')}
				/>
				<FormDatepicker
					name={`${path}.ansettelsesPeriode.tom`}
					label="Ansatt til"
					onChange={handleChange('ansettelsesPeriode.tom')}
				/>
				<FormSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					onChange={handleChange('ansettelsesPeriode.sluttaarsak')}
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
						onChange={handleChange('arbeidsavtale.yrke')}
					/>
				)}
			</div>

			{arbeidsforholdstype !== 'forenkletOppgjoersordning' && (
				<ArbeidsavtaleForm
					path={`${path}.arbeidsavtale`}
					onChangeLenket={handleChange}
					values={getValues(path)}
				/>
			)}
			{arbeidsforholdstype === 'maritimtArbeidsforhold' && (
				<MaritimtArbeidsforholdForm path={`${path}.fartoy[0]`} onChangeLenket={handleChange} />
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
