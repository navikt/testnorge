import React, { useContext } from 'react'
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
	initialArbeidsforhold,
	initialFartoy,
	initialForenkletOppgjoersordning,
} from '../initialValues'
import { isDate } from 'date-fns'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import _ from 'lodash'
import { fixTimezone } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'

export const ArbeidsforholdForm = ({ path, arbeidsforholdIndex }) => {
	const { watch, getValues, setValue, trigger } = useFormContext()

	const { personFoerLeggTil } = useContext(BestillingsveilederContext)
	const tidligereAaregdata = hentAaregEksisterendeData(personFoerLeggTil)
	const erLaastArbeidsforhold = arbeidsforholdIndex < tidligereAaregdata?.length

	const gjeldendeArbeidsgiver = watch(`${path}.arbeidsgiver`)

	const arbeidsforholdstype = watch(`${path}.arbeidsforholdstype`)

	const handleChange = (fieldPath: string) => {
		return (field) => {
			const value = isDate(field)
				? fixTimezone(field)
				: field?.value || field?.target?.value || null
			setValue(`${path}.${fieldPath}`, value)
			trigger()
		}
	}

	const handleArbeidsforholdstypeChange = (event) => {
		if (event.value === 'forenkletOppgjoersordning') {
			if (arbeidsforholdstype !== 'forenkletOppgjoersordning') {
				setValue(path, {
					...initialForenkletOppgjoersordning,
					arbeidsforholdstype: event.value,
					arbeidsgiver: gjeldendeArbeidsgiver,
				})
				// TODO: Behold arbeidsavtale.yrke?
				trigger()
			}
		} else {
			if (arbeidsforholdstype === 'forenkletOppgjoersordning' || arbeidsforholdstype === '') {
				setValue(path, {
					...initialArbeidsforhold,
					arbeidsforholdstype: event.value,
					arbeidsgiver: gjeldendeArbeidsgiver,
				})
				// TODO: Behold arbeidsavtale.yrke?
				trigger()
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
				{/*//TODO: Bare vises ved legg til?*/}
				<FormTextInput
					label="Arbeidsforhold-ID"
					name={`${path}.arbeidsforholdId`}
					isDisabled={true}
					title="Kan ikke endre arbeidsforhold-ID på eksisterende arbeidsforhold"
				/>
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
				<ArbeidsavtaleForm path={`${path}.arbeidsavtale`} onChangeLenket={handleChange} />
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
