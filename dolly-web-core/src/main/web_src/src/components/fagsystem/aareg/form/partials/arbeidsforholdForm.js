import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'

export const ArbeidsforholdForm = ({ formikBag, initial }) => {
	const initialTimeloennet = {
		periode: {
			fom: null,
			tom: null
		},
		antallTimer: 0
	}

	const initialPermisjon = {
		permisjonsPeriode: {
			fom: null,
			tom: null
		},
		permisjonsprosent: 100,
		permisjonOgPermittering: ''
	}

	const initialUtenlandsopphold = {
		periode: {
			fom: null,
			tom: null
		},
		land: ''
	}

	const arbeidsforholdArray = _get(formikBag.values, 'aareg', [])

	const fjern = (idx, path, currentArray) => {
		const nyArray = currentArray.filter((_, _idx) => _idx !== idx)
		formikBag.setFieldValue(path, nyArray)
	}

	const leggTil = (name, initial) =>
		formikBag.setFieldValue(name, _get(formikBag.values, name).concat([initial]))

	return (
		<FieldArray name="aareg">
			{({ push }) => (
				<div>
					{arbeidsforholdArray.map((arbeidsforhold, idx) => (
						<React.Fragment key={idx}>
							<div className="flexbox--flex-wrap">
								<FormikDatepicker
									name={`aareg[${idx}].ansettelsesPeriode.fom`}
									label="Ansatt fra"
								/>
								<FormikDatepicker
									name={`aareg[${idx}].ansettelsesPeriode.tom`}
									label="Ansatt til"
								/>
								<FormikSelect
									name={`aareg[${idx}].arbeidsforholdstype`}
									label="Type arbeidsforhold"
									kodeverk="Arbeidsforholdstyper"
									size="xxlarge"
								/>
								<FormikSelect
									name={`aareg[${idx}].arbeidsgiver.aktoertype`}
									label="Type arbeidsgiver"
									options={Options('aktoertype')}
									size="medium"
								/>
								{arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
									<FormikSelect // evt. felt man kan skrive i også?
										name={`aareg[${idx}].arbeidsgiver.aktoerId`}
										label="Arbeidsgiver orgnummer"
										options={Options('orgnummer')}
										type="text"
										size="large"
									/>
								)}
								{/* Skal vise value (kode) i tillegg */}
								{arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
									<FormikTextInput
										name={`aareg[${idx}].arbeidsgiver.aktoerId`}
										label="Arbeidsgiver ident"
									/>
								)}
							</div>
							<ArbeidsavtaleForm formikBag={formikBag} idx={idx} />
							{arbeidsforhold.antallTimerForTimeloennet && (
								<TimeloennetForm
									name={`aareg[${idx}].antallTimerForTimeloennet`}
									formikBag={formikBag}
									fjern={fjern}
								/>
							)}
							<FieldArrayAddButton
								title="Legg til antall timer for timelønnet"
								onClick={() =>
									leggTil(`aareg[${idx}].antallTimerForTimeloennet`, initialTimeloennet)
								}
							/>
							{arbeidsforhold.permisjon && (
								<PermisjonForm
									name={`aareg[${idx}].permisjon`}
									formikBag={formikBag}
									fjern={fjern}
								/>
							)}
							<FieldArrayAddButton
								title="Legg til permisjon"
								onClick={() => leggTil(`aareg[${idx}].permisjon`, initialPermisjon)}
							/>
							{arbeidsforhold.utenlandsopphold && (
								<UtenlandsoppholdForm
									name={`aareg[${idx}].utenlandsopphold`}
									formikBag={formikBag}
									fjern={fjern}
								/>
							)}
							<FieldArrayAddButton
								title="Legg til utenlandsopphold"
								onClick={() => leggTil(`aareg[${idx}].utenlandsopphold`, initialUtenlandsopphold)}
							/>
							<FieldArrayRemoveButton onClick={() => fjern(idx, 'aareg', arbeidsforholdArray)} />
						</React.Fragment>
					))}
					<FieldArrayAddButton title="Legg til arbeidsforhold" onClick={() => push(initial)} />
				</div>
			)}
		</FieldArray>
	)
}
